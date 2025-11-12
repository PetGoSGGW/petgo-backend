package pl.petgo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.petgo.backend.domain.AvailabilitySlot;
import pl.petgo.backend.domain.Offer;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.OfferCreateRequest;
import pl.petgo.backend.repository.OfferRepository;
import pl.petgo.backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createOffer(OfferCreateRequest request, Long walkerId) {
        // 1. Pobierz użytkownika (Walkera) z bazy na podstawie ID z tokena
        User walker = userRepository.findById(walkerId)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik nie istnieje"));

        // 2. Utwórz obiekt Oferty
        Offer offer = Offer.builder()
                .walker(walker)
                .priceCents(request.priceCents())
                .description(request.description())
                .isActive(true)
                .availabilitySlots(new ArrayList<>())
                .build();

        // 3. Przetwarzanie slotów (jeśli zostały przesłane)
        if (request.initialSlots() != null && !request.initialSlots().isEmpty()) {
            List<AvailabilitySlot> slots = request.initialSlots().stream()
                    .map(slotDto -> {
                        if (!slotDto.endTime().isAfter(slotDto.startTime())) {
                            throw new IllegalArgumentException("Data zakończenia musi być późniejsza niż data rozpoczęcia");
                        }

                        return AvailabilitySlot.builder()
                                .startTime(slotDto.startTime())
                                .endTime(slotDto.endTime())
                                .latitude(slotDto.latitude())
                                .longitude(slotDto.longitude())
                                .offer(offer)
                                .build();
                    })
                    .toList();

            offer.getAvailabilitySlots().addAll(slots);
        }

        // 4. Zapis do bazy (CascadeType.ALL w encji Offer zapisze też sloty)
        Offer savedOffer = offerRepository.save(offer);

        return savedOffer.getOfferId();
    }
}