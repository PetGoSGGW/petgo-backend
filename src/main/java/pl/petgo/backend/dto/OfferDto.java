package pl.petgo.backend.dto;

import pl.petgo.backend.domain.Offer;

import java.time.Instant;


public record OfferDto(
        Long offerId,
        Integer priceCents,
        String description,
        boolean isActive,
        Instant createdAt
) {
    public static OfferDto fromEntity(Offer offer) {
        return new OfferDto(
                offer.getOfferId(),
                offer.getPriceCents(),
                offer.getDescription(),
                offer.isActive(),
                offer.getCreatedAt()
        );
    }
}