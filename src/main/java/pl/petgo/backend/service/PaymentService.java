package pl.petgo.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private String currency;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public PaymentResponse initPayment(PaymentRequest request) throws StripeException {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation with this ID does not exist"));
        
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Cannot pay for canceled reservation");
        }

        Offer offer = reservation.getOffer();
        User payer = reservation.getOwner();
        User payee = reservation.getWalker();

        Long amountCents = Long.valueOf(offer.getPriceCents());

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountCents)
                .setCurrency(currency)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                )
                .setDescription("Oplata za spacer: " + reservation.getReservationId())
                .setReceiptEmail(payer.getEmail())
                .putMetadata("reservationId", reservation.getReservationId().toString())
                .putMetadata("userId", payer.getUserId().toString())
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

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

        log.info("Payment initiated ID: {} for reservation: {}", payment.getPaymentId(), reservation.getReservationId());

        return new PaymentResponse(intent.getClientSecret(), payment.getPaymentId());
    }
}