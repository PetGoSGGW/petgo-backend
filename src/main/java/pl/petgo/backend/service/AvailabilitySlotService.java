package pl.petgo.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.petgo.backend.domain.AvailabilitySlot;
import pl.petgo.backend.domain.Offer;
import pl.petgo.backend.dto.availabilityslot.AvailabilitySlotDto;
import pl.petgo.backend.dto.availabilityslot.AvailableSlotRequest;
import pl.petgo.backend.repository.AvailabilitySlotRepository;
import pl.petgo.backend.repository.OfferRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilitySlotService {

    private final AvailabilitySlotRepository slotRepository;
    private final OfferRepository offerRepository;

    @Transactional(readOnly = true)
    public List<AvailabilitySlotDto> getSlotsForOffer(Long offerId) {
        return slotRepository.findAllByOffer_OfferIdOrderByStartTimeAsc(offerId)
                .stream()
                .map(AvailabilitySlotDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AvailabilitySlotDto> addSlots(Long offerId, List<AvailableSlotRequest> requests, Long userId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found: " + offerId));

        if (!offer.getWalker().getUserId().equals(userId)) {
            throw new SecurityException("Nie masz uprawnień do zarządzania tą ofertą.");
        }

        for (AvailableSlotRequest req : requests) {
            if (!req.startTime().isBefore(req.endTime())) {
                throw new IllegalArgumentException("Data rozpoczęcia musi być wcześniejsza niż data zakończenia.");
            }

            boolean overlap = slotRepository.existsByOffer_OfferIdAndStartTimeBeforeAndEndTimeAfter(
                    offerId, req.endTime(), req.startTime()
            );
            if (overlap) {
                throw new IllegalArgumentException("Konflikt terminów! W podanym czasie (" + req.startTime() + ") masz już inny slot.");
            }

            AvailabilitySlot slot = AvailabilitySlot.builder()
                    .offer(offer)
                    .startTime(req.startTime())
                    .endTime(req.endTime())
                    .latitude(req.latitude())
                    .longitude(req.longitude())
                    .build();

            slotRepository.save(slot);
        }

        return getSlotsForOffer(offerId);
    }

    @Transactional
    public void deleteSlot(Long slotId, Long userId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + slotId));

        if (!slot.getOffer().getWalker().getUserId().equals(userId)) {
            throw new SecurityException("Nie możesz usunąć cudzego slotu.");
        }

        slotRepository.delete(slot);
    }
}