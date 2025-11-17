package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.GpsPoint;

import java.util.List;

public interface GpsPointRepository extends JpaRepository<GpsPoint, Long> {
    List<GpsPoint> findBySession_SessionIdOrderByRecordedAtAsc(Long sessionId);
}
