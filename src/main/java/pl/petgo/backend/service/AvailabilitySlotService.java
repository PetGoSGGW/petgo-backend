package pl.petgo.backend.service;

import lombok.RequiredArgsConstructor;
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
public class AvailabilitySlotService {

    private final AvailabilitySlotRepository slotRepository;
    private final OfferRepository offerRepository;

    @Transactional
    public List<AvailabilitySlotDto> addSlots(List<AvailableSlotRequest> requests, Long userId) {
        Offer offer = offerRepository.findByWalker_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Najpierw stwórz ofertę!"));

        for (AvailableSlotRequest req : requests) {
            // Rzeczywista walidacja konfliktów
            boolean overlap = slotRepository.existsByOffer_OfferIdAndStartTimeBeforeAndEndTimeAfter(
                    offer.getOfferId(), req.endTime(), req.startTime());

            if (overlap) {
                throw new IllegalArgumentException("Konflikt terminów dla czasu: " + req.startTime());
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
        return getSlotsForOffer(offer.getOfferId());
    }
    @Transactional(readOnly = true)
    public List<AvailabilitySlotDto> getSlotsForOffer(Long offerId) {
        return slotRepository.findAllByOffer_OfferIdOrderByStartTimeAsc(offerId)
                .stream().map(AvailabilitySlotDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void deleteSlot(Long slotId, Long userId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot nie istnieje."));

        if (!slot.getOffer().getWalker().getUserId().equals(userId)) {
            throw new SecurityException("Nie możesz usuwać cudzych slotów.");
        }

        if (slot.getReservation() != null) {
            throw new IllegalStateException("Nie można usunąć zarezerwowanego slotu.");
        }

        slotRepository.delete(slot);
    }
}