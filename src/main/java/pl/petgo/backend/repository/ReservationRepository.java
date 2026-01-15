package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.*;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByOwner_UserId(Long ownerUserId);
    List<Reservation> findAllByWalker_UserId(Long walkerUserId);
    List<Reservation> findAllByDog_DogId(Long dogId);

    boolean existsByOfferAndDogAndOwnerAndWalkerAndScheduledStartAndScheduledEndAndStatusNotIn(
            Offer offer,
            Dog dog,
            User owner,
            User walker,
            Instant scheduledStart,
            Instant scheduledEnd,
            Collection<ReservationStatus> excludedStatuses
    );
}
