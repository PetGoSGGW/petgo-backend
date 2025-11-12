package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long> {
}