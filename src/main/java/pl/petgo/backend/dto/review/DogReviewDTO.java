package pl.petgo.backend.dto.review;

import pl.petgo.backend.domain.Dog;
import pl.petgo.backend.domain.Review;
import pl.petgo.backend.domain.ReviewType;
import pl.petgo.backend.exception.DtoBuildException;
import pl.petgo.backend.utils.CollectionUtil;

import java.util.List;
import java.util.Optional;

public class DogReviewDTO extends ReviewAbstractDTO {

    private final String dogName; //TODO replace by DogDTO in future

    public static DogReviewDTO getReviewDogDTO(List<Review> reviews) throws DtoBuildException {
        Optional<Dog> dogOptional = reviews.stream()
                .map(Review::getDog)
                .distinct()
                .collect(CollectionUtil.zeroOrOne());
        if (dogOptional.isEmpty()) {
            throw new DtoBuildException();
        }

        String dogName = dogOptional.get().getName();
        return new DogReviewDTO(dogName, reviews);
    }


    private DogReviewDTO(String dogName, List<Review> reviewList) {
        super(reviewList, ReviewType.DOG);
        this.dogName = dogName;
    }

    public String getDogName() {
        return this.dogName;
    }
}
