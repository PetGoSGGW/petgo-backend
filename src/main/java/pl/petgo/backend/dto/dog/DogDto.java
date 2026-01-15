package pl.petgo.backend.dto.dog;

import java.time.Instant;
import java.util.List;

public record DogDto(
        Long dogId,
        Long ownerId,
        BreedDto breed,
        String name,
        String size,
        String notes,
        Double weightKg,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt,
        List<DogPhotoDto> photos) {
}
