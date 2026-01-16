package pl.petgo.backend.dto.offer;

import pl.petgo.backend.domain.Offer;
import pl.petgo.backend.dto.availabilityslot.AvailabilitySlotDto;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record OfferDto(
        Long offerId,
        Long walkerId,
        String walkerName,
        Integer priceCents,
        String description,
        boolean isActive,
        Instant createdAt,
        List<AvailabilitySlotDto> slots
) {
    public static OfferDto fromEntity(Offer offer) {
        return new OfferDto(
                offer.getOfferId(),
                offer.getWalker().getUserId(),
                offer.getWalker().getFirstName() + " " + offer.getWalker().getLastName(),
                offer.getPriceCents(),
                offer.getDescription(),
                offer.isActive(),
                offer.getCreatedAt(),
                offer.getAvailabilitySlots() != null
                        ? offer.getAvailabilitySlots().stream()
                        .map(AvailabilitySlotDto::fromEntity)
                        .collect(Collectors.toList())
                        : List.of()
        );
    }

    public static OfferDto fromEntityNotReserved(Offer offer) {
        return new OfferDto(
                offer.getOfferId(),
                offer.getWalker().getUserId(),
                offer.getWalker().getFirstName() + " " + offer.getWalker().getLastName(),
                offer.getPriceCents(),
                offer.getDescription(),
                offer.isActive(),
                offer.getCreatedAt(),
                offer.getAvailabilitySlots().stream()
                        .filter(as -> as.getReservation() == null)
                        .map(AvailabilitySlotDto::fromEntity)
                        .toList()
        );
    }
}