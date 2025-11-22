package pl.petgo.backend.dto.payment;

import pl.petgo.backend.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentHistoryDto(
        Long paymentId,
        Long reservationId,
        String counterpartName,   // nazwa drugiego użytkownika
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        Instant createdAt,
        Instant paidAt
) {}