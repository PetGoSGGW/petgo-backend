package pl.petgo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.petgo.backend.domain.DogPhoto;

import java.util.List;

public interface DogPhotoRepository extends JpaRepository<DogPhoto, Long> {
    List<DogPhoto> getDogPhotosByDog_DogId(Long dogDogId);
}
