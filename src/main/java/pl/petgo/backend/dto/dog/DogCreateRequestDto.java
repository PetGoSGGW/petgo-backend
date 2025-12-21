package pl.petgo.backend.dto.dog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DogCreateRequestDto(
        @NotNull(message = "Owner ID is required")
        Long ownerId,

        @NotBlank(message = "Breed code is required")
        String breedCode,

        @NotBlank(message = "Name is required")
        String name,

        String size,

        String notes,

        @Positive(message = "Weight must be positive")
        Double weightKg) {
}
