package pl.petgo.backend.dto.review;

import pl.petgo.backend.domain.Review;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.user.BasicUserInfoDto;

import java.time.Instant;

public record ReviewDTO(String comment, Integer rating, BasicUserInfoDto authorDto, Instant createdAt) {

    public static ReviewDTO getReviewDTO(Review review) {
        String comment = review.getComment();
        Integer rating = review.getRating();
        User author = review.getAuthor();
        BasicUserInfoDto authorInfoDto = BasicUserInfoDto.from(author);
        Instant createdTime = review.getCreatedAt();
        return new ReviewDTO(comment, rating, authorInfoDto, createdTime);
    }
}
