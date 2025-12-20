package pl.petgo.backend.dto.wallet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TopupRequest(
        @NotNull
        @Positive
        Long amountCents,

        @Size(max = 255)
        String description
) { }

