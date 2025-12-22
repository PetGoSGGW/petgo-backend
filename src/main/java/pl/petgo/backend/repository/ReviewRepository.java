package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.petgo.backend.domain.Review;
import pl.petgo.backend.domain.ReviewType;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByDogDogIdAndAuthorUserId(long dogId, long authorId);

    List<Review> findBySubjectUserUserId(long walkerId);

    List<Review> findByDogDogId(long dogId);

    boolean existsByReservationReservationIdAndReviewType(long reservationId, ReviewType reviewType);
}
