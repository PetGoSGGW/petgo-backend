package pl.petgo.backend.dto.Reservation;
import pl.petgo.backend.domain.Reservation;
import pl.petgo.backend.domain.ReservationStatus;
import pl.petgo.backend.dto.AvailabillitySlot.AvailabilitySlotDto;
import java.time.Instant;
import java.util.List;

public record ReservationDto (
        Long reservationId,
        Long offerId,
        Long dogId,
        Long walkerId,
        Long ownerId,
        Instant scheduleStart,
        Instant scheduleEnd,
        ReservationStatus status,
        Instant createdAt,
        Instant updatedAt,
        List<AvailabilitySlotDto> bookedSlots
)
{
    public static ReservationDto fromEntity(Reservation reservation) {
        return new ReservationDto(
                reservation.getReservationId(),
                reservation.getOffer().getOfferId(),
                reservation.getDog().getDogId(),
                reservation.getWalker().getUserId(),
                reservation.getOwner().getUserId(),
                reservation.getScheduledStart(),
                reservation.getScheduledEnd(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt(),
                reservation.getBookedSlots().stream()
                        .map(AvailabilitySlotDto::fromEntity)
                        .toList()
        );
    }
}
