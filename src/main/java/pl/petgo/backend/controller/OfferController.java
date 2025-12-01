package pl.petgo.backend.controller;

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

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping
    public ResponseEntity<Void> createOffer(
            @Valid @RequestBody OfferCreateRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();

        Long offerId = offerService.createOffer(request, userId);

        return ResponseEntity.created(URI.create("/api/offers/" + offerId)).build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<OfferDto>> getMyOffers(@AuthenticationPrincipal AppUserDetails principal) {
        List<OfferDto> offers = offerService.getOffersForWalker(principal.getUser());
        return ResponseEntity.ok(offers);
    }

    @PatchMapping("/{offerId}")
    public ResponseEntity<OfferDto> updateOffer(
            @PathVariable Long offerId,
            @Valid @RequestBody OfferUpdateRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        OfferDto updatedOffer = offerService.updateOffer(offerId, request, userId);
        return ResponseEntity.ok(updatedOffer);
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<Void> deleteOffer(
            @PathVariable Long offerId,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        offerService.deleteOffer(offerId, userId);

        return ResponseEntity.noContent().build();
    }
}