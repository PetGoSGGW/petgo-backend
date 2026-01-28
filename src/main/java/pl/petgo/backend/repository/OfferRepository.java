import java.time.Instant;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByWalker_UserId(Long userId);

    Page<Offer> findDistinctByIsActiveTrueAndAvailabilitySlots_ReservationIsNullAndAvailabilitySlots_EndTimeAfterAndAvailabilitySlots_LatitudeBetweenAndAvailabilitySlots_LongitudeBetween(
            Instant now,
            Double minLat, Double maxLat,
            Double minLon, Double maxLon,
            Pageable pageable
    );

    Page<Offer> findDistinctByIsActiveTrueAndAvailabilitySlots_ReservationIsNullAndAvailabilitySlots_EndTimeAfter(
            Instant now,
            Pageable pageable
    );
}