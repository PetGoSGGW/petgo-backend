package pl.petgo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.petgo.backend.domain.Offer;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.OfferCreateRequest;
import pl.petgo.backend.repository.OfferRepository;
import pl.petgo.backend.repository.UserRepository;

import java.util.ArrayList;

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

}