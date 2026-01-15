package pl.petgo.backend.dto.review;

import pl.petgo.backend.domain.Dog;
import pl.petgo.backend.domain.Review;
import pl.petgo.backend.domain.ReviewType;
import pl.petgo.backend.dto.dog.BasicDogInfoDto;
import pl.petgo.backend.exception.DtoBuildException;
import pl.petgo.backend.utils.CollectionUtil;

import java.util.List;
import java.util.Optional;

public class DogReviewDTO extends ReviewAbstractDTO {

    private final BasicDogInfoDto dogDto;

    public static DogReviewDTO getReviewDogDTO(List<Review> reviews) throws DtoBuildException {
        Optional<Dog> dogOptional = reviews.stream()
                .map(Review::getDog)
                .distinct()
                .collect(CollectionUtil.zeroOrOne());
        if (dogOptional.isEmpty()) {
            throw new DtoBuildException();
        }

        Dog dog = dogOptional.get();
        return new DogReviewDTO(dog, reviews);
    }

    public static DogReviewDTO getReviewDogDTOForNoReviews(Dog dog) {
        List<Review> emptyReviews = List.of();
        return new DogReviewDTO(dog, emptyReviews);
    }

    private DogReviewDTO(Dog dog, List<Review> reviewList) {
        super(reviewList, ReviewType.DOG);
        this.dogDto = BasicDogInfoDto.from(dog);
    }

    public BasicDogInfoDto getDogDto() {
        return this.dogDto;
    }
}
