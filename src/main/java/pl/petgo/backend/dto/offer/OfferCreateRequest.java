package pl.petgo.backend.dto.offer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OfferCreateRequest(
        @NotNull(message = "Cena jest wymagana")
        @Min(value = 0, message = "Cena nie może być ujemna")
        Integer priceCents,

        @Size(max = 1000, message = "Opis może mieć maksymalnie 1000 znaków")
        String description
) {}