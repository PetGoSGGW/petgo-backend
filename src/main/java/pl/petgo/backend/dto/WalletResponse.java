package pl.petgo.backend.dto;

package pl.petgo.backend.dto;

import java.time.Instant;

public record WalletResponse(
        Long walletId,
        Long userId,
        String currency,
        Long balanceCents,
        Instant createdAt,
        Instant updatedAt
) {
}
