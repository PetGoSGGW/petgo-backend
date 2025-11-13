package pl.petgo.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;

public record AvailableSlotRequest(
        @NotNull(message = "Data rozpoczęcia jest wymagana")
        @Future(message = "Data rozpoczęcia musi być w przyszłości")
        Instant startTime,

        @NotNull(message = "Data zakończenia jest wymagana")
        @Future(message = "Data zakończenia musi być w przyszłości")
        Instant endTime,

        @NotNull(message = "Szerokość geograficzna jest wymagana")
        @Min(value = -90, message = "Szerokość geograficzna musi być >= -90")
        @Max(value = 90, message = "Szerokość geograficzna musi być <= 90")
        Double latitude,

        @NotNull(message = "Długość geograficzna jest wymagana")
        @Min(value = -180, message = "Długość geograficzna musi być >= -180")
        @Max(value = 180, message = "Długość geograficzna musi być <= 180")
        Double longitude
) {}