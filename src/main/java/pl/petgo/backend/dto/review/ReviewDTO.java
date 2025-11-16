package pl.petgo.backend.dto.review;

import pl.petgo.backend.domain.Review;
import pl.petgo.backend.domain.User;

public record ReviewDTO(String comment, Integer rating, String author) {

    public static ReviewDTO getReviewDTO(Review review) {
        String comment = review.getComment();
        Integer rating = review.getRating();
        User author = review.getAuthor();
        String authorName  = author.getFirstName() + " " + author.getLastName(); //TODO replace by UserDTO in future
        return new ReviewDTO(comment, rating, authorName);
    }
}
