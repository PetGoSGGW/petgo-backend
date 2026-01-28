package pl.petgo.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.petgo.backend.domain.Offer;
import java.util.Optional;
import java.time.Instant;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByWalker_UserId(Long userId);

    @Query("""
    SELECT o
    FROM Offer o
    WHERE o.isActive = true
      AND EXISTS (
          SELECT 1
          FROM AvailabilitySlot s
          WHERE s.offer = o
            AND s.reservation IS NULL
            AND s.latitude BETWEEN :minLat AND :maxLat
            AND s.longitude BETWEEN :minLon AND :maxLon
            AND (
                6371 * acos(
                    cos(radians(:lat)) * cos(radians(s.latitude)) *
                    cos(radians(s.longitude) - radians(:lon)) +
                    sin(radians(:lat)) * sin(radians(s.latitude))
                )
            ) <= :radius
      )
""")
    Page<Offer> findOffersInRadius(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radius") double radius,
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLon") double minLon,
            @Param("maxLon") double maxLon,
            Instant now,
            Pageable pageable
    );

    Page<Offer> findDistinctByIsActiveTrueAndAvailabilitySlots_ReservationIsNullAndAvailabilitySlots_EndTimeAfter(
            Instant now,
            Pageable pageable
    );
}