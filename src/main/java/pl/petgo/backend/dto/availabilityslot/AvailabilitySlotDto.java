package pl.petgo.backend.dto.availabilityslot;

import pl.petgo.backend.domain.AvailabilitySlot;
import java.time.Instant;

public record AvailabilitySlotDto(
        Long slotId,
        Instant startTime,
        Instant endTime,
        Double latitude,
        Double longitude,
        boolean isReserved
) {
    public static AvailabilitySlotDto fromEntity(AvailabilitySlot slot) {
        return new AvailabilitySlotDto(
                slot.getSlotId(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getLatitude(),
                slot.getLongitude(),
                slot.getReservation() != null
        );
    }
}