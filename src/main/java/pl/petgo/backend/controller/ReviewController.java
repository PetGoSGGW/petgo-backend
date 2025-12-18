package pl.petgo.backend.controller;

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
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/walk")
    public ResponseEntity<ReviewWalkDTO> getWalkReview(@RequestParam Long dogId, @RequestParam Long walkerId) {
        ReviewWalkDTO result = reviewService.getWalksByWalkerWithDogReview(dogId, walkerId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/walker")
    public ResponseEntity<ReviewWalkerDTO> getWalkerReview(@RequestParam Long walkerId) {
        return ResponseEntity.ok(reviewService.getWalkerReview(walkerId));
    }

    @GetMapping("/dog")
    public ResponseEntity<DogReviewDTO> getDogReview(@RequestParam Long dogId) {
        return ResponseEntity.ok(reviewService.getDogReview(dogId));
    }

    @PostMapping
    public ResponseEntity<Void> createReview(@Valid @RequestBody CreateReviewRequest request, @AuthenticationPrincipal AppUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        Long savedReviewId = reviewService.addReview(userId, request);
        return ResponseEntity.created(URI.create("/api/reviews/" + savedReviewId)).build();
    }
}
