package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.Offer;
import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findAllByWalker_UserId(Long userId);
}