package pl.petgo.backend.dto.review;

import pl.petgo.backend.domain.Review;
import pl.petgo.backend.domain.ReviewType;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.exception.DtoBuildException;
import pl.petgo.backend.utils.CollectionUtil;

import java.util.List;
import java.util.Optional;

public class ReviewWalkerDTO extends ReviewAbstractDTO {

    public final String walkerFullName; //TODO replace by UserDTO in future

    public static ReviewWalkerDTO getReviewWalkerDTO(List<Review> reviews) throws DtoBuildException {
        Optional<User> walkerOptional = reviews.stream()
                .map(Review::getSubjectUser)
                .distinct()
                .collect(CollectionUtil.zeroOrOne());
        if (walkerOptional.isEmpty()) {
            throw new DtoBuildException();
        }

        User walker = walkerOptional.get();
        String walkerFullName = walker.getFirstName() + " " + walker.getLastName();
        return new ReviewWalkerDTO(walkerFullName, reviews);
    }


    private ReviewWalkerDTO(String walkerFullName, List<Review> reviewList) {
        super(reviewList, ReviewType.WALKER);
        this.walkerFullName = walkerFullName;
    }


    public String getWalkerDTO() {
        return this.walkerFullName;
    }

}
