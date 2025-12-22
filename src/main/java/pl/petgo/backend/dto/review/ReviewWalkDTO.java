package pl.petgo.backend.dto.review;

import pl.petgo.backend.domain.Dog;
import pl.petgo.backend.domain.Review;
import pl.petgo.backend.domain.ReviewType;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.dog.BasicDogInfoDto;
import pl.petgo.backend.dto.user.BasicUserInfoDto;
import pl.petgo.backend.exception.DtoBuildException;
import pl.petgo.backend.utils.CollectionUtil;

import java.util.List;
import java.util.Optional;

public class ReviewWalkDTO extends ReviewAbstractDTO{

    public final BasicUserInfoDto walkerInfoDto;
    public final BasicDogInfoDto dogInfoDto;

    public static ReviewWalkDTO getReviewWalkDTO(List<Review> reviews) throws DtoBuildException {
        Optional<Dog> dogOptional = reviews.stream()
                .map(Review::getDog)
                .distinct()
                .collect(CollectionUtil.zeroOrOne());

        Optional<User> walkerOptional = reviews.stream()
                .map(Review::getSubjectUser)
                .distinct()
                .collect(CollectionUtil.zeroOrOne());

        if (dogOptional.isEmpty() || walkerOptional.isEmpty()) {
            throw new DtoBuildException();
        }

        return new ReviewWalkDTO(dogOptional.get(), walkerOptional.get(), reviews);
    }

    private ReviewWalkDTO(Dog dog, User walker, List<Review> reviews) {
        super(reviews, ReviewType.WALK);

        this.dogInfoDto = BasicDogInfoDto.from(dog);
        this.walkerInfoDto = BasicUserInfoDto.from(walker);
    }
}
