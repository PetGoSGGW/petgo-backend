package pl.petgo.backend.dto.payment;

public record PaymentCallbackDto(
        String providerRef,     // np. "pi_3..."
        String status,          // "succeeded", "failed" itp.
        String providerEventId
) {}