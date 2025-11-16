package pl.petgo.backend.service;

import org.springframework.stereotype.Service;
import pl.petgo.backend.domain.Review;
import pl.petgo.backend.dto.review.DogReviewDTO;
import pl.petgo.backend.dto.review.ReviewWalkDTO;
import pl.petgo.backend.dto.review.ReviewWalkerDTO;
import pl.petgo.backend.repository.ReviewRepository;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public ReviewWalkerDTO getWalkerReview(Long walkerId) {
        List<Review> reviews = reviewRepository.findBySubjectUserUserId(walkerId);
        return ReviewWalkerDTO.getReviewWalkerDTO(reviews);
    }

    public DogReviewDTO getDogReview(Long dogId) {
        List<Review> reviews = reviewRepository.findByDogDogId(dogId);
        return DogReviewDTO.getReviewDogDTO(reviews);
    }

    public ReviewWalkDTO getWalksByWalkerWithDogReview(Long dogId, Long walkerId) {
        List<Review> reviews = reviewRepository.findByDogDogIdAndAuthorUserId(dogId, walkerId);
        return ReviewWalkDTO.getReviewWalkDTO(reviews);
    }
}
