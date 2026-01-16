package pl.petgo.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.Offer;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByWalker_UserId(Long userId);

    Page<Offer> findDistinctByIsActiveTrueAndAvailabilitySlots_ReservationIsNullAndAvailabilitySlots_LatitudeBetweenAndAvailabilitySlots_LongitudeBetween(
            Double minLat, Double maxLat,
            Double minLon, Double maxLon,
            Pageable pageable
    );

    Page<Offer> findDistinctByIsActiveTrueAndAvailabilitySlots_ReservationIsNull(Pageable pageable);
}