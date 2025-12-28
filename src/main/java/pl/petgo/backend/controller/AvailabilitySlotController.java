package pl.petgo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.availabilityslot.AvailableSlotRequest;
import pl.petgo.backend.dto.availabilityslot.AvailabilitySlotDto;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.AvailabilitySlotService;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@Tag(name = "Availability Module", description = "Endpoints for managing dog walker availability slots")
public class AvailabilitySlotController {

    private final AvailabilitySlotService slotService;

    @Operation(summary = "Add new availability slots to your offer")
    @PostMapping
    public ResponseEntity<List<AvailabilitySlotDto>> addSlots(
            @RequestBody @Valid List<AvailableSlotRequest> requests,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        return ResponseEntity.ok(slotService.addSlots(requests, userDetails.getUser().getUserId()));
    }

    @Operation(summary = "Delete an availability slot (only if it is not reserved)")
    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> deleteSlot(
            @PathVariable Long slotId,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        slotService.deleteSlot(slotId, userDetails.getUser().getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all availability slots for a specific offer")
    @GetMapping("/offer/{offerId}")
    public ResponseEntity<List<AvailabilitySlotDto>> getSlotsForOffer(@PathVariable Long offerId) {
        return ResponseEntity.ok(slotService.getSlotsForOffer(offerId));
    }
}