package pl.petgo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.Offer.OfferCreateRequest;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.OfferService;
import pl.petgo.backend.dto.Offer.OfferDto;
import pl.petgo.backend.dto.Offer.OfferUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Tag(name = "Offer Module", description = "Endpoints for managing dog walking offers, availability slots, and location-based searching.")
public class OfferController {

    private final OfferService offerService;

    @Operation(
            summary = "Create a new dog walking offer",
            description = "Allows an authenticated Dog Walker to publish a new offer. The offer includes price, description, and initial availability slots."
    )
    @PostMapping
    public ResponseEntity<Void> createOffer(
            @Valid @RequestBody OfferCreateRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        Long offerId = offerService.createOffer(request, userId);
        return ResponseEntity.created(URI.create("/api/offers/" + offerId)).build();
    }

    @Operation(
            summary = "Get current walker's offers",
            description = "Retrieves a list of all offers associated with the currently authenticated Dog Walker account."
    )
    @GetMapping("/my")
    public ResponseEntity<List<OfferDto>> getMyOffers(@AuthenticationPrincipal AppUserDetails principal) {
        List<OfferDto> offers = offerService.getOffersForWalker(principal.getUser());
        return ResponseEntity.ok(offers);
    }

    @Operation(
            summary = "Update an existing offer",
            description = "Modifies the details of a specific offer. Validates if the authenticated user is the owner (walker) of the offer."
    )
    @PatchMapping("/{offerId}")
    public ResponseEntity<OfferDto> updateOffer(
            @Parameter(description = "ID of the offer to update") @PathVariable Long offerId,
            @Valid @RequestBody OfferUpdateRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        OfferDto updatedOffer = offerService.updateOffer(offerId, request, userId);
        return ResponseEntity.ok(updatedOffer);
    }

    @Operation(
            summary = "Delete an offer",
            description = "Performs a deletion of the specified offer. Only the creator of the offer can perform this action."
    )
    @DeleteMapping("/{offerId}")
    public ResponseEntity<Void> deleteOffer(
            @Parameter(description = "ID of the offer to delete") @PathVariable Long offerId,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        offerService.deleteOffer(offerId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get offer details by ID",
            description = "Retrieves full information about a specific offer, including the walker's profile and availability."
    )
    @GetMapping("/{offerId}")
    public ResponseEntity<OfferDto> getOfferById(
            @Parameter(description = "Unique ID of the offer") @PathVariable Long offerId
    ) {
        OfferDto offer = offerService.getOfferById(offerId);
        return ResponseEntity.ok(offer);
    }

    @Operation(
            summary = "Search for available offers",
            description = "Search for dog walking offers using geographical filters. Supports pagination."
    )
    @GetMapping("/search")
    public ResponseEntity<Page<OfferDto>> searchOffers(
            @Parameter(description = "Latitude for proximity search") @RequestParam(required = false) Double lat,
            @Parameter(description = "Longitude for proximity search") @RequestParam(required = false) Double lon,
            @Parameter(description = "Search radius in kilometers") @RequestParam(required = false) Double radiusKm,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<OfferDto> offers = offerService.searchOffers(lat, lon, radiusKm, pageable);
        return ResponseEntity.ok(offers);
    }
}