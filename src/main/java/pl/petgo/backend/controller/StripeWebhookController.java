package pl.petgo.backend.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
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
@Tag(name = "Payments Module - Stripe Webhooks", description = "Endpoints for handling asynchronous events and callbacks from Stripe")
public class StripeWebhookController {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Operation(
            summary = "Handle Stripe Webhook events",
            description = "Receives asynchronous events from Stripe servers. Verifies the 'Stripe-Signature' header to ensure security. " +
                    "Handles 'payment_intent.succeeded' and 'payment_intent.payment_failed' events to update payment status in the database. " +
                    "NOTE: This endpoint is intended for machine-to-machine communication, not for manual testing via Swagger UI."
    )
    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
                                                    @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            // 1. Weryfikacja podpisu -  czy to na pewno Stripe
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            log.error("Nieprawidłowy podpis Webhooka Stripe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }

        // 2. Obsługa konkretnych zdarzeń
        if ("payment_intent.succeeded".equals(event.getType())) {
            // Deserializacja obiektu Stripe
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);

            if (paymentIntent != null) {
                log.info("Płatność udana: " + paymentIntent.getId());
                handlePaymentSuccess(paymentIntent);
            }
        } else if ("payment_intent.payment_failed".equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
            if (paymentIntent != null) {
                log.info("Płatność nieudana: " + paymentIntent.getId());
                handlePaymentFailure(paymentIntent);
            }
        }

        return ResponseEntity.ok("Received");
    }

    private void handlePaymentSuccess(PaymentIntent paymentIntent) {
        // Znajdź płatność w bazie po ID ze Stripe
        paymentRepository.findByStripePaymentIntentId(paymentIntent.getId())
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.PAID);
                    paymentRepository.save(payment);

                    // TODO: Integracja platnosci z rezerwacjami i portfelem:
                    // 1. Zmiana statusu Rezerwacji na CONFIRMED
                    // 2. Doładowanie portfela (Wallet) wyprowadzacza
                    log.info("Zaktualizowano status płatności {} na PAID", payment.getPaymentId());
                });
    }

    private void handlePaymentFailure(PaymentIntent paymentIntent) {
        paymentRepository.findByStripePaymentIntentId(paymentIntent.getId())
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.FAILED);
                    paymentRepository.save(payment);
                    log.info("Zaktualizowano status płatności {} na FAILED", payment.getPaymentId());
                });
    }
}