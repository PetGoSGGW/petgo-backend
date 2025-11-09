package pl.petgo.backend.dto;

import java.time.Instant;

public record WalletResponse(
        Long walletId,
        String currency,
        Long balanceCents,
        Instant createdAt,
        Instant updatedAt
) {
}
