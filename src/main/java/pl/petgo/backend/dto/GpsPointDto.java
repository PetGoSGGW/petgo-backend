package pl.petgo.backend.dto;

import java.time.Instant;

public record GpsPointDto(
        Double latitude,
        Double longitude,
        Instant recordedAt
) {}