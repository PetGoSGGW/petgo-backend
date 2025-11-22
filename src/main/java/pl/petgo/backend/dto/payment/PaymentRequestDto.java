package pl.petgo.backend.dto.payment;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PaymentRequestDto(

        @Positive(message = "Reservation ID is required")
        Long reservationId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3)
        String currency

) {}