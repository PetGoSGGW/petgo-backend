package pl.petgo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.petgo.backend.domain.Offer;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.offer.OfferCreateRequest;
import pl.petgo.backend.repository.OfferRepository;
import pl.petgo.backend.repository.UserRepository;
import pl.petgo.backend.dto.offer.OfferDto;
import pl.petgo.backend.dto.offer.OfferUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createOffer(OfferCreateRequest request, Long walkerId) {
        User walker = userRepository.findById(walkerId)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik nie istnieje"));

        Offer offer = Offer.builder()
                .walker(walker)
                .priceCents(request.priceCents())
                .description(request.description())
                .isActive(true)
                .availabilitySlots(new ArrayList<>())
                .build();


        Offer savedOffer = offerRepository.save(offer);

        return savedOffer.getOfferId();
    }

    @Transactional(readOnly = true)
    public List<OfferDto> getOffersForWalker(User walker) {
        return offerRepository.findAllByWalker_UserId(walker.getUserId())
                .stream()
                .map(OfferDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public OfferDto updateOffer(Long offerId, OfferUpdateRequest request, Long userId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta nie znaleziona ID: " + offerId));

        if (!offer.getWalker().getUserId().equals(userId)) {
            throw new SecurityException("Brak uprawnień do edycji oferty ID: " + offerId);
        }

        if (request.priceCents() != null) {
            offer.setPriceCents(request.priceCents());
        }

        if (request.description() != null) {
            offer.setDescription(request.description());
        }

        if (request.isActive() != null) {
            offer.setActive(request.isActive());
        }

        return OfferDto.fromEntity(offerRepository.save(offer));
    }

    @Transactional
    public void deleteOffer(Long offerId, Long userId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta nie znaleziona ID: " + offerId));

        if (!offer.getWalker().getUserId().equals(userId)) {
            throw new SecurityException("Brak uprawnień do usunięcia oferty ID: " + offerId);
        }

        offerRepository.delete(offer);
    }

    @Transactional(readOnly = true)
    public OfferDto getOfferById(Long offerId) {
        return offerRepository.findById(offerId)
                .map(OfferDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found with id: " + offerId));
    }

    @Transactional(readOnly = true)
    public Page<OfferDto> searchOffers(Double lat, Double lon, Double radiusKm, Pageable pageable) {
        if (lat == null || lon == null) {
            return offerRepository.findAllByIsActiveTrue(pageable)
                    .map(OfferDto::fromEntity);
        }

        if (radiusKm == null) {
            radiusKm = 10.0;
        }

        return offerRepository.findAllActiveInRadius(lat, lon, radiusKm, pageable)
                .map(OfferDto::fromEntity);
    }
}