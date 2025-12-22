package pl.petgo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.review.CreateReviewRequest;
import pl.petgo.backend.dto.review.DogReviewDTO;
import pl.petgo.backend.dto.review.ReviewWalkDTO;
import pl.petgo.backend.dto.review.ReviewWalkerDTO;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.ReviewService;

import java.net.URI;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Module", description = "API for managing reviews for dogs and walkers")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(
            summary = "Get walk review",
            description = "Retrieve a review for a walk by dog ID and walker ID"
    )
    @GetMapping("/walk")
    public ResponseEntity<ReviewWalkDTO> getWalkReview(@RequestParam Long dogId, @RequestParam Long walkerId) {
        ReviewWalkDTO result = reviewService.getWalksByWalkerWithDogReview(dogId, walkerId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Get walker review",
            description = "Retrieve a general summarize review for a walker by walker ID"
    )
    @GetMapping("/walker")
    public ResponseEntity<ReviewWalkerDTO> getWalkerReview(@RequestParam Long walkerId) {
        return ResponseEntity.ok(reviewService.getWalkerReview(walkerId));
    }

    @Operation(
            summary = "Get dog review",
            description = "Retrieve a general summarize review for a dog by dog ID"
    )
    @GetMapping("/dog")
    public ResponseEntity<DogReviewDTO> getDogReview(@RequestParam Long dogId) {
        return ResponseEntity.ok(reviewService.getDogReview(dogId));
    }

    @Operation(
            summary = "Create a new review",
            description = "Create a new review for a reservation"
    )
    @PostMapping
    public ResponseEntity<Void> createReview(@Valid @RequestBody CreateReviewRequest request, @AuthenticationPrincipal AppUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        Long savedReviewId = reviewService.addReview(userId, request);
        return ResponseEntity.created(URI.create("/api/reviews/" + savedReviewId)).build();
    }
}
