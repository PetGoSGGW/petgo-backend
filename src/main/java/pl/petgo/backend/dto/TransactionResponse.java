package pl.petgo.backend.dto.wallet;

import java.time.Instant;

public record TransactionResponse(
        Long transactionId,
        Long userId,
        Long amountCents,
        Long balanceAfterCents,
        String type,
        String description,
        Instant createdAt
) { }
