package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.Dog;
import pl.petgo.backend.domain.Reservation;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByOwner_UserId(Long ownerUserId);
    List<Reservation> findAllByDog_DogId(Long dogId);

}
