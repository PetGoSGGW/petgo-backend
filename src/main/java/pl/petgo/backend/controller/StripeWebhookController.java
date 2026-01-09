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
import pl.petgo.backend.domain.PaymentStatus;
import pl.petgo.backend.repository.PaymentRepository;
import pl.petgo.backend.service.ReservationService;
import pl.petgo.backend.service.WalletService;

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
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe Webhook signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }

        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

        if (session == null) {
            return ResponseEntity.ok("Received (No session data)");
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                log.info("Checkout Session completed: {}", session.getId());
                handleCheckoutSessionCompleted(session);
                break;

            case "checkout.session.expired":
                log.info("Checkout Session expired: {}", session.getId());
                handleCheckoutSessionExpired(session);
                break;

            case "checkout.session.async_payment_failed":
                log.info("Checkout Session payment failed: {}", session.getId());
                handlePaymentFailed(session);
                break;

            default:
                log.debug("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("Received");
    }

    private void handleCheckoutSessionCompleted(Session session) {
        paymentRepository.findByStripePaymentIntentId(session.getId())
                .ifPresent(payment -> {
                    if (payment.getStatus() == PaymentStatus.PAID) {
                        return;
                    }

                    payment.setStatus(PaymentStatus.PAID);
                    String paymentIntentId = session.getPaymentIntent();
                    if (paymentIntentId != null) {
                        payment.setStripePaymentIntentId(paymentIntentId);
                    }
                    paymentRepository.save(payment);

                    try {
                        reservationService.confirmReservationSystem(payment.getReservation().getReservationId());
                    } catch (Exception e) {
                        log.error("Failed to confirm reservation", e);
                    }

                    Long amount = payment.getAmountCents();
                    String desc = "Reservation #" + payment.getReservation().getReservationId();

                    try {
                        walletService.addFundsSystem(payment.getPayee().getUserId(), amount, "Payment received: " + desc);
                    } catch (Exception e) {
                        log.error("Failed to add funds to walker", e);
                    }

                    try {
                        walletService.deductFundsSystem(payment.getPayer().getUserId(), amount, "Payment sent: " + desc);
                    } catch (Exception e) {
                        log.error("Failed to deduct funds from owner", e);
                    }
                });
    }

    private void handleCheckoutSessionExpired(Session session) {
        paymentRepository.findByStripePaymentIntentId(session.getId())
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.CANCELED);
                    paymentRepository.save(payment);
                    log.info("Payment {} canceled due to session expiration.", payment.getPaymentId());

                    try {
                        reservationService.cancelReservationSystem(payment.getReservation().getReservationId());
                    } catch (Exception e) {
                        log.error("Failed to cancel reservation systemically", e);
                    }
                });
    }

    private void handlePaymentFailed(Session session) {
        paymentRepository.findByStripePaymentIntentId(session.getId())
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.FAILED);
                    paymentRepository.save(payment);
                    log.info("Payment {} failed (async rejection).", payment.getPaymentId());

                    try {
                        reservationService.cancelReservationSystem(payment.getReservation().getReservationId());
                    } catch (Exception e) {
                        log.error("Failed to cancel reservation systemically", e);
                    }
                });
    }
}