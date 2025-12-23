package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.Dog;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Long> {
    List<Dog> findAllByOwner_UserId(Long ownerUserId);
}
