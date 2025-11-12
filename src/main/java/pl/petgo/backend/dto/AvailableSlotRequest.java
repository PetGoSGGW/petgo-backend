package pl.petgo.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record AvailableSlotRequest(
        @NotNull(message = "Data rozpoczęcia jest wymagana")
        @Future(message = "Data rozpoczęcia musi być w przyszłości")
        Instant startTime,

        @NotNull(message = "Data zakończenia jest wymagana")
        @Future(message = "Data zakończenia musi być w przyszłości")
        Instant endTime,

        @NotNull(message = "Szerokość geograficzna jest wymagana")
        Double latitude,

        @NotNull(message = "Długość geograficzna jest wymagana")
        Double longitude
) {}