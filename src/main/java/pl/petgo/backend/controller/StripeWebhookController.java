
package pl.petgo.backend.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.domain.Payment;
import pl.petgo.backend.domain.PaymentStatus;
import pl.petgo.backend.repository.PaymentRepository;
import pl.petgo.backend.service.ReservationService;
import pl.petgo.backend.service.WalletService;

import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhooks", description = "Endpoints for handling Stripe asynchronous callbacks")
public class StripeWebhookController {

    private final PaymentRepository paymentRepository;
    private final ReservationService reservationService;
    private final WalletService walletService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Operation(
            summary = "Handle Stripe Events",
            description = "Processes events from Stripe. Handles 'completed' (success), 'expired' (timeout), and 'async_payment_failed' (rejection)."
    )
    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
                                                    @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("=== STRIPE WEBHOOK RECEIVED ===");
        log.info("Payload length: {} characters", payload != null ? payload.length() : 0);
        log.info("Signature header present: {}", sigHeader != null && !sigHeader.isEmpty());

        Event event;
        long startTime = System.currentTimeMillis();

        try {
            log.debug("Constructing Stripe event from payload...");
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            log.info("Successfully constructed Stripe event. Type: {}, ID: {}", event.getType(), event.getId());
        } catch (SignatureVerificationException e) {
            log.error("CRITICAL: Invalid Stripe Webhook signature verification failed", e);
            log.error("Signature header: {}", sigHeader);
            log.error("Endpoint secret configured: {}", endpointSecret != null && !endpointSecret.isEmpty());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            log.error("CRITICAL: Failed to construct Stripe event from payload", e);
            log.error("Payload preview: {}", payload != null && payload.length() > 100 ?
                    payload.substring(0, 100) + "..." : payload);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }

        Session session = null;
        try {
            log.debug("Extracting session data from event...");
            session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session == null) {
                log.warn("No session data found in event. Event type: {}, Event ID: {}",
                        event.getType(), event.getId());
                return ResponseEntity.ok("Received (No session data)");
            }

            log.info("Session extracted successfully. Session ID: {}, Payment Intent: {}, Status: {}",
                    session.getId(), session.getPaymentIntent(), session.getPaymentStatus());

        } catch (Exception e) {
            log.error("Failed to extract session data from event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process session data");
        }

        try {
            log.info("Processing event type: {} for session: {}", event.getType(), session.getId());

            switch (event.getType()) {
                case "checkout.session.completed":
                    log.info("=== PROCESSING CHECKOUT SESSION COMPLETED ===");
                    log.info("Session ID: {}, Payment Intent: {}", session.getId(), session.getPaymentIntent());
                    handleCheckoutSessionCompleted(session);
                    break;

                case "checkout.session.expired":
                    log.info("=== PROCESSING CHECKOUT SESSION EXPIRED ===");
                    log.info("Session ID: {}", session.getId());
                    handleCheckoutSessionExpired(session);
                    break;

                case "checkout.session.async_payment_failed":
                    log.info("=== PROCESSING CHECKOUT SESSION ASYNC PAYMENT FAILED ===");
                    log.info("Session ID: {}", session.getId());
                    handlePaymentFailed(session);
                    break;

                default:
                    log.warn("Unhandled event type received: {} for session: {}", event.getType(), session.getId());
                    log.debug("Full event data: {}", event.toJson());
            }

            long processingTime = System.currentTimeMillis() - startTime;
            log.info("=== STRIPE WEBHOOK PROCESSING COMPLETED ===");
            log.info("Total processing time: {}ms, Event type: {}, Session ID: {}",
                    processingTime, event.getType(), session.getId());

        } catch (Exception e) {
            log.error("CRITICAL: Unexpected error during webhook processing", e);
            log.error("Event type: {}, Session ID: {}", event.getType(), session != null ? session.getId() : "null");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing failed");
        }

        return ResponseEntity.ok("Received");
    }

    private void handleCheckoutSessionCompleted(Session session) {
        log.info("Starting handleCheckoutSessionCompleted for session: {}", session.getId());

        try {
            log.debug("Searching for payment with Stripe payment intent ID: {}", session.getId());
            Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(session.getId());

            if (paymentOpt.isEmpty()) {
                log.warn("No payment found for session ID: {}", session.getId());
                return;
            }

            Payment payment = paymentOpt.get();
            log.info("Payment found - ID: {}, Current status: {}, Amount: {} cents",
                    payment.getPaymentId(), payment.getStatus(), payment.getAmountCents());

            if (payment.getStatus() == PaymentStatus.PAID) {
                log.info("Payment {} already marked as PAID, skipping processing", payment.getPaymentId());
                return;
            }

            // Update payment status
            PaymentStatus oldStatus = payment.getStatus();
            payment.setStatus(PaymentStatus.PAID);

            String paymentIntentId = session.getPaymentIntent();
            if (paymentIntentId != null) {
                log.debug("Updating payment intent ID from {} to {}",
                        payment.getStripePaymentIntentId(), paymentIntentId);
                payment.setStripePaymentIntentId(paymentIntentId);
            } else {
                log.warn("No payment intent ID provided in session");
            }

            try {
                Payment savedPayment = paymentRepository.save(payment);
                log.info("Payment {} status updated from {} to {}",
                        savedPayment.getPaymentId(), oldStatus, savedPayment.getStatus());
            } catch (Exception e) {
                log.error("CRITICAL: Failed to save payment {} status update", payment.getPaymentId(), e);
                throw e; // Re-throw to prevent further processing
            }

            // Confirm reservation
            Long reservationId = payment.getReservation().getReservationId();
            log.info("Confirming reservation {} for payment {}", reservationId, payment.getPaymentId());

            try {
                reservationService.confirmReservationSystem(reservationId);
                log.info("Successfully confirmed reservation {}", reservationId);
            } catch (Exception e) {
                log.error("CRITICAL: Failed to confirm reservation {} for payment {}",
                        reservationId, payment.getPaymentId(), e);
            }

            // Process wallet transactions
            Long amount = payment.getAmountCents();
            String desc = "Reservation #" + reservationId;
            Long payeeUserId = payment.getPayee().getUserId();
            Long payerUserId = payment.getPayer().getUserId();

            log.info("Processing wallet transactions - Amount: {} cents, Payee: {}, Payer: {}",
                    amount, payeeUserId, payerUserId);

            // Add funds to payee (walker)
            try {
                walletService.addFundsSystem(payeeUserId, amount, "Payment received: " + desc);
                log.info("Successfully added {} cents to payee wallet (User ID: {})", amount, payeeUserId);
            } catch (Exception e) {
                log.error("CRITICAL: Failed to add {} cents to payee wallet (User ID: {})",
                        amount, payeeUserId, e);
            }

            // Deduct funds from payer (owner)
            try {
                walletService.deductFundsSystem(payerUserId, amount, "Payment sent: " + desc);
                log.info("Successfully deducted {} cents from payer wallet (User ID: {})", amount, payerUserId);
            } catch (Exception e) {
                log.error("CRITICAL: Failed to deduct {} cents from payer wallet (User ID: {})",
                        amount, payerUserId, e);
            }

            log.info("Completed handleCheckoutSessionCompleted for payment {}", payment.getPaymentId());

        } catch (Exception e) {
            log.error("CRITICAL: Unexpected error in handleCheckoutSessionCompleted for session {}",
                    session.getId(), e);
        }
    }

    private void handleCheckoutSessionExpired(Session session) {
        log.info("Starting handleCheckoutSessionExpired for session: {}", session.getId());

        try {
            Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(session.getId());

            if (paymentOpt.isEmpty()) {
                log.warn("No payment found for expired session ID: {}", session.getId());
                return;
            }

            Payment payment = paymentOpt.get();
            PaymentStatus oldStatus = payment.getStatus();
            Long reservationId = payment.getReservation().getReservationId();

            log.info("Processing payment expiration - Payment ID: {}, Current status: {}, Reservation ID: {}",
                    payment.getPaymentId(), oldStatus, reservationId);

            payment.setStatus(PaymentStatus.CANCELED);

            try {
                Payment savedPayment = paymentRepository.save(payment);
                log.info("Payment {} status updated from {} to {} due to session expiration",
                        savedPayment.getPaymentId(), oldStatus, savedPayment.getStatus());
            } catch (Exception e) {
                log.error("CRITICAL: Failed to save payment {} cancellation", payment.getPaymentId(), e);
                throw e;
            }

            try {
                reservationService.cancelReservationSystem(reservationId);
                log.info("Successfully canceled reservation {} due to payment expiration", reservationId);
            } catch (Exception e) {
                log.error("CRITICAL: Failed to cancel reservation {} systemically after payment expiration",
                        reservationId, e);
            }

            log.info("Completed handleCheckoutSessionExpired for payment {}", payment.getPaymentId());

        } catch (Exception e) {
            log.error("CRITICAL: Unexpected error in handleCheckoutSessionExpired for session {}",
                    session.getId(), e);
        }
    }

    private void handlePaymentFailed(Session session) {
        log.info("Starting handlePaymentFailed for session: {}", session.getId());

        try {
            Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(session.getId());

            if (paymentOpt.isEmpty()) {
                log.warn("No payment found for failed session ID: {}", session.getId());
                return;
            }

            Payment payment = paymentOpt.get();
            PaymentStatus oldStatus = payment.getStatus();
            Long reservationId = payment.getReservation().getReservationId();

            log.info("Processing payment failure - Payment ID: {}, Current status: {}, Reservation ID: {}",
                    payment.getPaymentId(), oldStatus, reservationId);

            payment.setStatus(PaymentStatus.FAILED);

            try {
                Payment savedPayment = paymentRepository.save(payment);
                log.info("Payment {} status updated from {} to {} due to async payment failure",
                        savedPayment.getPaymentId(), oldStatus, savedPayment.getStatus());
            } catch (Exception e) {
                log.error("CRITICAL: Failed to save payment {} failure status", payment.getPaymentId(), e);
                throw e;
            }

            try {
                reservationService.cancelReservationSystem(reservationId);
                log.info("Successfully canceled reservation {} due to payment failure", reservationId);
            } catch (Exception e) {
                log.error("CRITICAL: Failed to cancel reservation {} systemically after payment failure",
                        reservationId, e);
            }

            log.info("Completed handlePaymentFailed for payment {}", payment.getPaymentId());

        } catch (Exception e) {
            log.error("CRITICAL: Unexpected error in handlePaymentFailed for session {}",
                    session.getId(), e);
        }
    }
}