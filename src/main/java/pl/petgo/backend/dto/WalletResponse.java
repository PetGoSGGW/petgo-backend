package pl.petgo.backend.dto.wallet;

import java.time.Instant;

public record WalletResponse(
        Long walletId,
        Long userId,
        String currency,
        Long balanceCents,
        Instant createdAt,
        Instant updatedAt
) { }
