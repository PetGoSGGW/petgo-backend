package pl.petgo.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.petgo.backend.domain.Offer;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByWalker_UserId(Long userId);

    @Query("SELECT DISTINCT o FROM Offer o " +
            "JOIN o.availabilitySlots s " +
            "WHERE o.isActive = true " +
            "AND s.reservation IS NULL " +
            "AND (6371 * acos(cos(radians(:lat)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(s.latitude)))) < :radiusKm")
    Page<Offer> findAllActiveInRadius(
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable
    );

    Page<Offer> findAllByIsActiveTrue(Pageable pageable);
}