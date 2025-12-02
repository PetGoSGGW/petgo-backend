package pl.petgo.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import pl.petgo.backend.dto.review.DogReviewDTO;
import pl.petgo.backend.dto.review.ReviewWalkDTO;
import pl.petgo.backend.dto.review.ReviewWalkerDTO;
import pl.petgo.backend.service.ReviewService;

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
}
