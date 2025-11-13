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
        User walker = userRepository.findById(walkerId)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik nie istnieje"));

        Offer offer = Offer.builder()
                .walker(walker)
                .priceCents(request.priceCents())
                .description(request.description())
                .isActive(true)
                .availabilitySlots(new ArrayList<>())
                .build();

        if (request.initialSlots() != null && !request.initialSlots().isEmpty()) {
            List<AvailabilitySlot> slots = new ArrayList<>();

            for (var slotDto : request.initialSlots()) {
                if (!slotDto.endTime().isAfter(slotDto.startTime())) {
                    throw new IllegalArgumentException("Data zakończenia musi być późniejsza niż data rozpoczęcia");
                }

                boolean overlaps = slots.stream().anyMatch(existing ->
                        isOverlapping(existing.getStartTime(), existing.getEndTime(), slotDto.startTime(), slotDto.endTime())
                );

                if (overlaps) {
                    throw new IllegalArgumentException("Zdefiniowane sloty czasowe nakładają się na siebie!");
                }

                slots.add(AvailabilitySlot.builder()
                        .startTime(slotDto.startTime())
                        .endTime(slotDto.endTime())
                        .latitude(slotDto.latitude())
                        .longitude(slotDto.longitude())
                        .offer(offer)
                        .build());
            }
            offer.getAvailabilitySlots().addAll(slots);
        }

        Offer savedOffer = offerRepository.save(offer);

        return savedOffer.getOfferId();
    }

    private boolean isOverlapping(java.time.Instant start1, java.time.Instant end1, java.time.Instant start2, java.time.Instant end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}