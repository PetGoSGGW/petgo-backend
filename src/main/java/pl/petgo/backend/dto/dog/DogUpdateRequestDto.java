package pl.petgo.backend.dto.dog;

import jakarta.validation.constraints.Positive;

public record DogUpdateRequestDto(
        String breedCode,
        String name,
        String size,
        String notes,

        @Positive
        Double weightKg,

        Boolean isActive) {
}

