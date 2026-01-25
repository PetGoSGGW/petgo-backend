package pl.petgo.backend.dto.reservation;
import pl.petgo.backend.domain.Reservation;
import pl.petgo.backend.domain.ReservationStatus;
import pl.petgo.backend.dto.GpsPointDto;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public record ReservationDetailsDto(
        Long id,
        ReservationStatus status,
        String dogName,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Long durationMinutes,
        GpsRouteDto gpsRoute
) {
    public static ReservationDetailsDto fromEntity(Reservation reservation) {

        OffsetDateTime start = reservation.getScheduledStart()
                .atOffset(ZoneOffset.UTC);
        OffsetDateTime end = reservation.getScheduledEnd()
                .atOffset(ZoneOffset.UTC);

        return new ReservationDetailsDto(
                reservation.getReservationId(),
                reservation.getStatus(),
                reservation.getDog().getName(),
                start,
                end,
                Duration.between(start, end).toMinutes(),
                null
        );
    }
}

