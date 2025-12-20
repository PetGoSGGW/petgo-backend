package pl.petgo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Availability Module", description = "Endpoints for managing dog walker availability slots and schedules.")
public class AvailabilitySlotController {

    private final AvailabilitySlotService slotService;

    @Operation(
            summary = "Get all availability slots for a specific offer",
            description = "Retrieves a list of all time windows (slots) defined for a specific offer. Used by pet owners to check a walker's schedule."
    )
    @GetMapping("/offers/{offerId}/slots")
    public ResponseEntity<List<AvailabilitySlotDto>> getSlots(
            @Parameter(description = "ID of the offer to fetch slots for") @PathVariable Long offerId
    ) {
        return ResponseEntity.ok(slotService.getSlotsForOffer(offerId));
    }

    @Operation(
            summary = "Add new availability slots to an offer",
            description = "Allows a Dog Walker to add multiple time windows to their offer. The system validates if the authenticated user owns the offer."
    )
    @PostMapping("/offers/{offerId}/slots")
    public ResponseEntity<List<AvailabilitySlotDto>> addSlots(
            @Parameter(description = "ID of the offer to which slots will be added") @PathVariable Long offerId,
            @RequestBody @Valid List<AvailableSlotRequest> requests,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        List<AvailabilitySlotDto> result = slotService.addSlots(offerId, requests, userId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Remove a specific availability slot",
            description = "Deletes a single availability slot. Only the walker who created the slot is authorized to remove it."
    )
    @DeleteMapping("/slots/{slotId}")
    public ResponseEntity<Void> deleteSlot(
            @Parameter(description = "Unique ID of the slot to delete") @PathVariable Long slotId,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        slotService.deleteSlot(slotId, userId);
        return ResponseEntity.noContent().build();
    }
}