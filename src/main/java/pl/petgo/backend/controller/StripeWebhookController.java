package pl.petgo.backend.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.domain.Payment;
import pl.petgo.backend.domain.PaymentStatus;
import pl.petgo.backend.repository.PaymentRepository;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

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