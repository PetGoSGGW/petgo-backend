package pl.petgo.backend.dto.review;

import pl.petgo.backend.domain.Review;
import pl.petgo.backend.domain.ReviewType;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.user.BasicUserInfoDto;
import pl.petgo.backend.exception.DtoBuildException;
import pl.petgo.backend.utils.CollectionUtil;

import java.util.List;
import java.util.Optional;

public class ReviewWalkerDTO extends ReviewAbstractDTO {

    private final BasicUserInfoDto walkerInfoDto;

    public static ReviewWalkerDTO getReviewWalkerDTO(List<Review> reviews) throws DtoBuildException {
        Optional<User> walkerOptional = reviews.stream()
                .map(Review::getSubjectUser)
                .distinct()
                .collect(CollectionUtil.zeroOrOne());
        if (walkerOptional.isEmpty()) {
            throw new DtoBuildException();
        }

        return new ReviewWalkerDTO(walkerOptional.get(), reviews);
    }

    private ReviewWalkerDTO(User walker, List<Review> reviewList) {
        super(reviewList, ReviewType.WALKER);
        this.walkerInfoDto = BasicUserInfoDto.from(walker);
    }

    public BasicUserInfoDto getWalkerInfoDto() {
        return walkerInfoDto;
    }
}
