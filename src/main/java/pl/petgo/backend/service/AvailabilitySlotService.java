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
                .orElseThrow(() -> new IllegalArgumentException("Create an offer first!"));

        for (AvailableSlotRequest req : requests) {
            boolean overlap = slotRepository.existsByOffer_OfferIdAndStartTimeBeforeAndEndTimeAfter(
                    offer.getOfferId(), req.endTime(), req.startTime());

            if (overlap) {
                throw new IllegalArgumentException("Time conflict for start time: " + req.startTime());
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
                .orElseThrow(() -> new IllegalArgumentException("Slot does not exist."));

        if (!slot.getOffer().getWalker().getUserId().equals(userId)) {
            throw new SecurityException("You cannot delete slots belonging to another user.");
        }

        if (slot.getReservation() != null) {
            throw new IllegalStateException("Cannot delete a reserved slot.");
        }

        slotRepository.delete(slot);
    }
}