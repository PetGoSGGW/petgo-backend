package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
