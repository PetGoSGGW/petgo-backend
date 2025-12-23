package pl.petgo.backend.dto.offer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record OfferUpdateRequest(
        @Min(value = 1, message = "Cena musi być większa niż 0")
        Integer priceCents,

        @Size(max = 1000, message = "Opis za długi")
        String description,

        Boolean isActive
) {}