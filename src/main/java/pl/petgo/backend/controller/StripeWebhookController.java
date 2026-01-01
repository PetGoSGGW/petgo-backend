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

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhooks", description = "Endpoints for handling Stripe asynchronous callbacks")
public class StripeWebhookController {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Operation(
            summary = "Handle Stripe Events",
            description = "Processes events from Stripe (specifically 'checkout.session.completed'). " +
                    "Verifies signature, updates payment status, and swaps Session ID with PaymentIntent ID."
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

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                log.info("Checkout Session completed: {}", session.getId());
                handleCheckoutSessionCompleted(session);
            }
        }

        return ResponseEntity.ok("Received");
    }

    private void handleCheckoutSessionCompleted(Session session) {
        paymentRepository.findByStripePaymentIntentId(session.getId())
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.PAID);

                    String paymentIntentId = session.getPaymentIntent();
                    if (paymentIntentId != null) {
                        payment.setStripePaymentIntentId(paymentIntentId);
                    }

                    paymentRepository.save(payment);

                    log.info("Payment updated to PAID. Swapped Session ID for PaymentIntent ID: {}", paymentIntentId);

                    // TODO: Update Reservation status to CONFIRMED
                    // TODO: Top up Walker's Wallet
                });
    }
}