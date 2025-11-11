package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.GpsSession;

import java.util.Optional;

public interface GpsSessionRepository extends JpaRepository<GpsSession, Long> {
    Optional<GpsSession> findByReservationId(Long reservationId);
}

