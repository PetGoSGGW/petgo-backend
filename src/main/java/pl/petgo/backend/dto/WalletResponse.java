package pl.petgo.backend.dto;
<<<<<<< HEAD
=======

>>>>>>> 70434ab2dbddafc2cacb9cb7e41936548d2e08b3
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
