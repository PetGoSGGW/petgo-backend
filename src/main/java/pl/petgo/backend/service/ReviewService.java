package pl.petgo.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.petgo.backend.domain.Dog;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.domain.Reservation;
import pl.petgo.backend.domain.ReservationStatus;
import pl.petgo.backend.domain.Review;
import pl.petgo.backend.domain.ReviewType;
import pl.petgo.backend.dto.review.CreateReviewRequest;
import pl.petgo.backend.dto.review.DogReviewDTO;
import pl.petgo.backend.dto.review.ReviewWalkDTO;
import pl.petgo.backend.dto.review.ReviewWalkerDTO;
import pl.petgo.backend.exception.NotFoundException;
import pl.petgo.backend.repository.DogRepository;
import pl.petgo.backend.repository.ReservationRepository;
import pl.petgo.backend.repository.ReviewRepository;
import pl.petgo.backend.repository.UserRepository;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final DogRepository dogRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ReservationRepository reservationRepository,
                         UserRepository userRepository,
                         DogRepository dogRepository) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.dogRepository = dogRepository;
    }

    public ReviewWalkerDTO getWalkerReview(Long walkerId) {
        List<Review> reviews = reviewRepository.findBySubjectUserUserId(walkerId);
        if (!reviews.isEmpty()) {
            return ReviewWalkerDTO.getReviewWalkerDTO(reviews);
        }

        User walker = userRepository.findById(walkerId)
                .orElseThrow(() -> new NotFoundException("Walker does not exist with the specified ID: " + walkerId));
        return ReviewWalkerDTO.getReviewWalkerDTOForNoReviews(walker);
    }

    public DogReviewDTO getDogReview(Long dogId) {
        List<Review> reviews = reviewRepository.findByDogDogId(dogId);
        if (!reviews.isEmpty()) {
            return DogReviewDTO.getReviewDogDTO(reviews);
        }

        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new NotFoundException("Dog does not exist with the specified ID: " + dogId));
        return DogReviewDTO.getReviewDogDTOForNoReviews(dog);

    }

    public ReviewWalkDTO getWalksByWalkerWithDogReview(Long dogId, Long walkerId) {
        List<Review> reviews = reviewRepository.findByDogDogIdAndAuthorUserId(dogId, walkerId);
        if (!reviews.isEmpty()) {
            return ReviewWalkDTO.getReviewWalkDTO(reviews);
        }

        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new NotFoundException("Dog does not exist with the specified ID: " + dogId));

        User walker = userRepository.findById(walkerId)
                .orElseThrow(() -> new NotFoundException("Walker does not exist with the specified ID: " + walkerId));
        return ReviewWalkDTO.getReviewWalkDTOForNoReviews(dog, walker);
    }

    @Transactional
    public Long addReview(Long reviewerId, CreateReviewRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new NotFoundException(String.format("Reservation with the specified ID %s does not exist", request.reservationId())));

        if (!ReservationStatus.COMPLETED.equals(reservation.getStatus())) {
            throw new IllegalStateException("Cannot review an incomplete reservation");
        }

        ReviewType reviewType = request.reviewType();
        validateReviewerPermissions(reservation, reviewerId, reviewType);
        Review review = Review.createFromReservation(reservation, reviewType, request.rating(), request.comment(), reviewerId);
        Review saved = reviewRepository.save(review);
        return saved.getReviewId();
    }


    private void validateReviewerPermissions(Reservation reservation, Long reviewerId, ReviewType type) {
        boolean isWalker = reservation.getWalker().getUserId().equals(reviewerId);
        boolean isOwner = reservation.getOwner().getUserId().equals(reviewerId);
        if (!isWalker && !isOwner) {
            throw new AccessDeniedException(String.format("Review cannot be created by user %s who is not associated with reservation %s", reviewerId, reservation.getReservationId()));
        }

        switch (type) {
            case WALKER -> {
                if (!isOwner) {
                    throw new AccessDeniedException("Only the Owner can review the Walker.");
                }
            }
            case DOG -> {
                if (!isWalker) {
                    throw new AccessDeniedException("Only the Walker can review the Dog.");
                }
            }
            case WALK -> {
                if (!isWalker) {
                    throw new AccessDeniedException(("Only the Walker can submit a Walk Report."));
                }
            }
        }
    }

}
