package pl.petgo.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
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

        // TODO: In production, these URLs should come from configuration or match the frontend app URL, now it is mocked
        String successUrl = "http://localhost:8080/api/payments/success-mock";
        String cancelUrl = "http://localhost:8080/api/payments/cancel-mock";

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setCustomerEmail(payer.getEmail())
                .setClientReferenceId(reservation.getReservationId().toString())
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .putMetadata("reservationId", reservation.getReservationId().toString())
                                .putMetadata("userId", payer.getUserId().toString())
                                .build()
                )
                // IMPORTANT: item below will be displayed on Stripe hosted page
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(amountCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Dog Walk Service (Reservation #" + reservation.getReservationId() + ")")
                                                                .setDescription(offer.getDescription())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        Payment payment = Payment.builder()
                .stripePaymentIntentId(session.getId())
                .amountCents(amountCents)
                .currency(currency)
                .status(PaymentStatus.PENDING)
                .reservation(reservation)
                .payer(payer)
                .payee(payee)
                .createdAt(Instant.now())
                .build();

        paymentRepository.save(payment);

        log.info("Created Stripe Checkout Session ID: {} for Reservation: {}", session.getId(), reservation.getReservationId());

        return new PaymentResponse(session.getUrl(), session.getId());
    }
}