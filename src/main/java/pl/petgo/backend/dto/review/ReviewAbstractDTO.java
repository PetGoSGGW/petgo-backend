package pl.petgo.backend.dto.review;

import pl.petgo.backend.domain.Review;
import pl.petgo.backend.domain.ReviewType;

import java.util.List;

public abstract class ReviewAbstractDTO {

    private final List<ReviewDTO> reviewDTOList;
    private final double avgRating;
    private final ReviewType type;

    protected ReviewAbstractDTO(List<Review> reviewList, ReviewType reviewType) {
        this.type = reviewType;

        List<Review> reviewsFilteredByType = reviewList.stream()
                .filter(review -> reviewType.equals(review.getReviewType()))
                .toList();

        this.reviewDTOList = reviewsFilteredByType.stream()
                .map(ReviewDTO::getReviewDTO)
                .toList();

        this.avgRating = reviewsFilteredByType.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
    }

    public List<ReviewDTO> getReviewDTOList() {
        return reviewDTOList;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public ReviewType getType() {
        return type;
    }
}
