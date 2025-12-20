package pl.petgo.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.AvailabillitySlot.AvailableSlotRequest;
import pl.petgo.backend.dto.AvailabillitySlot.AvailabilitySlotDto;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.AvailabilitySlotService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AvailabilitySlotController {

    private final AvailabilitySlotService slotService;

    @GetMapping("/offers/{offerId}/slots")
    public ResponseEntity<List<AvailabilitySlotDto>> getSlots(@PathVariable Long offerId) {
        return ResponseEntity.ok(slotService.getSlotsForOffer(offerId));
    }

    @PostMapping("/offers/{offerId}/slots")
    public ResponseEntity<List<AvailabilitySlotDto>> addSlots(
            @PathVariable Long offerId,
            @RequestBody @Valid List<AvailableSlotRequest> requests,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        List<AvailabilitySlotDto> result = slotService.addSlots(offerId, requests, userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/slots/{slotId}")
    public ResponseEntity<Void> deleteSlot(
            @PathVariable Long slotId,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        slotService.deleteSlot(slotId, userId);
        return ResponseEntity.noContent().build();
    }
}