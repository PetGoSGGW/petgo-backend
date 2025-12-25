package pl.petgo.backend.dto.offer;

import pl.petgo.backend.domain.Offer;
import pl.petgo.backend.dto.availabilityslot.AvailabilitySlotDto;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record OfferDto(
        Long offerId,
        Long walkerId,         // ID Walkera
        String walkerName,      // Imię i Nazwisko Walkera
        Integer priceCents,
        String description,
        boolean isActive,
        Instant createdAt,
        List<AvailabilitySlotDto> slots // Lista slotów przypisana do oferty
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
}