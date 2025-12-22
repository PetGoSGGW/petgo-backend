package pl.petgo.backend.events;

public record ReservationCompletedEvent(
        Long reservationId,
        Long walkerId,
        Long ownerId,
        String dogName
) {}