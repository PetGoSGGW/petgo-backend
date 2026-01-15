package pl.petgo.backend.dto.dog;

import java.time.Instant;

public record DogPhotoDto(
        Long photoId,
        String url,
        Instant uploadedAt) {
}