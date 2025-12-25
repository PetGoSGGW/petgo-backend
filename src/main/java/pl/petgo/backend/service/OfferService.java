package pl.petgo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.petgo.backend.domain.Offer;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.offer.OfferCreateRequest;
import pl.petgo.backend.dto.offer.OfferDto;
import pl.petgo.backend.dto.offer.OfferUpdateRequest;
import pl.petgo.backend.repository.OfferRepository;
import pl.petgo.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createOffer(OfferCreateRequest request, Long walkerId) {
        if (offerRepository.findByWalker_UserId(walkerId).isPresent()) {
            throw new IllegalStateException("Użytkownik już posiada ofertę.");
        }

        User walker = userRepository.findById(walkerId)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik nie istnieje"));

        Offer offer = Offer.builder()
                .walker(walker)
                .priceCents(request.priceCents())
                .description(request.description())
                .isActive(true)
                .build();

        return offerRepository.save(offer).getOfferId();
    }

    @Transactional(readOnly = true)
    public OfferDto getMyOffer(Long userId) {
        return offerRepository.findByWalker_UserId(userId)
                .map(OfferDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono Twojej oferty."));
    }

    @Transactional
    public OfferDto updateOffer(Long userId, OfferUpdateRequest request) {
        Offer offer = offerRepository.findByWalker_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta nie istnieje."));

        if (request.priceCents() != null) offer.setPriceCents(request.priceCents());
        if (request.description() != null) offer.setDescription(request.description());
        if (request.isActive() != null) offer.setActive(request.isActive());

        return OfferDto.fromEntity(offerRepository.save(offer));
    }

    @Transactional(readOnly = true)
    public Page<OfferDto> searchOffers(Double lat, Double lon, Double radius, Pageable pageable) {
        return offerRepository.findAllActiveInRadius(lat, lon, radius != null ? radius : 10.0, pageable)
                .map(OfferDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public OfferDto getOfferById(Long offerId) {
        return offerRepository.findById(offerId)
                .map(OfferDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));
    }
}