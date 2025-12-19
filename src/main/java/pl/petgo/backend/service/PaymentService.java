package pl.petgo.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Do logowania (opcjonalne)
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.petgo.backend.domain.*;
import pl.petgo.backend.dto.payment.PaymentRequest;
import pl.petgo.backend.dto.payment.PaymentResponse;
import pl.petgo.backend.repository.PaymentRepository;
import pl.petgo.backend.repository.ReservationRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.currency}")
    private String currency; // np. "pln"

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public PaymentResponse initPayment(PaymentRequest request) throws StripeException {
        // 1. Pobierz rezerwację
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("Rezerwacja o podanym ID nie istnieje"));

        // 2. Walidacja
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Nie można opłacić anulowanej rezerwacji");
        }

        // 3. Pobierz dane do płatności
        Offer offer = reservation.getOffer();
        User payer = reservation.getOwner();
        User payee = reservation.getWalker();

        // Rzutowanie Integer (Offer) na Long (Stripe/Payment)
        Long amountCents = Long.valueOf(offer.getPriceCents());

        // 4. Tworzenie PaymentIntent w Stripe
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountCents)
                .setCurrency(currency)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                )
                .setDescription("Oplata za spacer: " + reservation.getReservationId())
                .setReceiptEmail(payer.getEmail()) // Stripe wyśle potwierdzenie na maila (w trybie live)
                // Metadane kluczowe dla Webhooka
                .putMetadata("reservationId", reservation.getReservationId().toString())
                .putMetadata("userId", payer.getUserId().toString())
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        // 5. Zapisz płatność w bazie ze statusem PENDING
        Payment payment = Payment.builder()
                .stripePaymentIntentId(intent.getId())
                .amountCents(amountCents)
                .currency(currency)
                .status(PaymentStatus.PENDING)
                .reservation(reservation)
                .payer(payer)
                .payee(payee)
                .createdAt(Instant.now())
                .build();

        paymentRepository.save(payment);

        log.info("Zainicjowano płatność ID: {} dla rezerwacji: {}", payment.getPaymentId(), reservation.getReservationId());

        return new PaymentResponse(intent.getClientSecret(), payment.getPaymentId());
    }
}