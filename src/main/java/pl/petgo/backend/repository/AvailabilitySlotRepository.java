package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.petgo.backend.domain.AvailabilitySlot;
import java.time.Instant;
import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findAllByOffer_OfferIdOrderByStartTimeAsc(Long offerId);

    // Zapytanie sprawdzające, czy nowy slot nie nachodzi na istniejący
    boolean existsByOffer_OfferIdAndStartTimeBeforeAndEndTimeAfter(
            Long offerId,
            Instant newSlotEndTime,
            Instant newSlotStartTime
    );
}