package pl.petgo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.offer.OfferCreateRequest;
import pl.petgo.backend.dto.offer.OfferDto;
import pl.petgo.backend.dto.offer.OfferUpdateRequest;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.OfferService;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Tag(name = "Offer Module", description = "Endpoints for managing dog walker offers and searching")
public class OfferController {

    private final OfferService offerService;

    @Operation(summary = "Create your unique walker offer")
    @PostMapping
    public ResponseEntity<Void> createOffer(
            @Valid @RequestBody OfferCreateRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        offerService.createOffer(request, userDetails.getUser().getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Retrieve current authenticated user's offer")
    @GetMapping("/my")
    public ResponseEntity<OfferDto> getMyOffer(@AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(offerService.getMyOffer(principal.getUser().getUserId()));
    }

    @Operation(summary = "Update current authenticated user's offer")
    @PatchMapping("/my")
    public ResponseEntity<OfferDto> updateMyOffer(
            @Valid @RequestBody OfferUpdateRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        return ResponseEntity.ok(offerService.updateOffer(userDetails.getUser().getUserId(), request));
    }

    @Operation(summary = "Search for available offers (active with open slots only)")
    @GetMapping("/search")
    public ResponseEntity<Page<OfferDto>> searchOffers(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double radiusKm,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(offerService.searchOffers(lat, lon, radiusKm, pageable));
    }

    @Operation(summary = "Get offer details by ID")
    @GetMapping("/{offerId}")
    public ResponseEntity<OfferDto> getOfferById(@PathVariable Long offerId) {
        return ResponseEntity.ok(offerService.getOfferById(offerId));
    }
}