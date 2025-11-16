package pl.petgo.backend.dto.review;

import pl.petgo.backend.domain.Dog;
import pl.petgo.backend.domain.Review;
import pl.petgo.backend.domain.ReviewType;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.exception.DtoBuildException;
import pl.petgo.backend.utils.CollectionUtil;

import java.util.List;
import java.util.Optional;

public class ReviewWalkDTO extends ReviewAbstractDTO{

    public final String walkerFullName; //TODO replace by UserDTO in future
    public final String dogName; //TODO replace by DogDTO in future

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

        String dogName = dogOptional.get().getName();
        User walker = walkerOptional.get();
        String walkerFullName = walker.getFirstName() + " " + walker.getLastName();
        return new ReviewWalkDTO(dogName, walkerFullName, reviews);
    }

    private ReviewWalkDTO(String dogName, String walkerFullName, List<Review> reviews) {
        super(reviews, ReviewType.WALK);
        this.dogName = dogName;
        this.walkerFullName = walkerFullName;
    }
}
