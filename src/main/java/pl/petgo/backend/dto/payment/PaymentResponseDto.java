package pl.petgo.backend.dto.payment;

import pl.petgo.backend.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponseDto(
        Long paymentId,
        Long reservationId,
        Long payerId,
        String payerName,
        Long payeeId,
        String payeeName,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String providerRef,        // np. Stripe PaymentIntent ID
        String paymentUrl,         // link do płatności (jeśli przekierowanie)
        Instant createdAt,
        Instant paidAt
) {}