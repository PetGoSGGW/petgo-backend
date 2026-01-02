package pl.petgo.backend.dto.reservation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReservationCreateRequest(
        @NotNull(message = "Offer id is required.")
        Long offerId,

        @NotNull(message = "Dog id is required.")
        Long dogId,

        @NotEmpty(message = "At least one availability slot id is required.")
        List<@NotNull Long> availabilitySlotIds
) {}