package pl.petgo.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.petgo.backend.domain.Breed;
import pl.petgo.backend.dto.dog.*;
import pl.petgo.backend.exception.FileStorageException;
import pl.petgo.backend.exception.NotFoundException;
import pl.petgo.backend.mapper.DogMapper;
import pl.petgo.backend.repository.BreedRepository;
import pl.petgo.backend.repository.DogPhotoRepository;
import pl.petgo.backend.repository.DogRepository;
import pl.petgo.backend.repository.UserRepository;
import pl.petgo.backend.utils.PhotosUtils;
import pl.petgo.backend.utils.SecurityUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static java.lang.String.valueOf;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Paths.get;
import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class DogService {

    public static final String DOG_NOT_FOUND_WITH_ID = "Dog not found with id: ";

    DogRepository dogRepository;
    DogPhotoRepository dogPhotoRepository;
    UserRepository userRepository;
    BreedRepository breedRepository;
    DogMapper dogMapper;

    @NonFinal
    @Value("${storage.location}")
    String baseUploadDir;

    public List<DogDto> getAllDogs() {
        return dogRepository.findAll().stream()
                .map(dogMapper::toDto)
                .toList();
    }

    public DogDto getDogById(Long id) {
        var dog = dogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND_WITH_ID + id));

        return dogMapper.toDto(dog);
    }

    public DogDto addDog(DogCreateRequestDto dogCreateRequestDto) {
        var dog = dogMapper.toEntity(dogCreateRequestDto);

        var owner = userRepository.findByEmail(SecurityUtils.getUserEmail())
                .orElseThrow(() -> new NotFoundException("Owner not found: " + SecurityUtils.getUserEmail()));

        var breed = (Breed) breedRepository.findByBreedCode(dogCreateRequestDto.breedCode())
                .orElseThrow(() -> new NotFoundException("Breed not found with code: " + dogCreateRequestDto.breedCode()));

        dog.setOwner(owner);
        dog.setBreed(breed);

        var savedDog = dogRepository.save(dog);
        return dogMapper.toDto(savedDog);
    }

    @Transactional
    public DogDto updateDog(Long id, DogUpdateRequestDto dogUpdateRequestDto) {
        var dog = dogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND_WITH_ID + id));

        if (!dog.getOwner().getEmail().equals(SecurityUtils.getUserEmail()) && !SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("Access denied!");
        }

        dogMapper.updateEntityFromDto(dogUpdateRequestDto, dog);

        if (dogUpdateRequestDto.breedCode() != null) {
            var breed = (Breed) breedRepository.findByBreedCode(dogUpdateRequestDto.breedCode())
                    .orElseThrow(() -> new NotFoundException("Breed not found with code: " + dogUpdateRequestDto.breedCode()));
            dog.setBreed(breed);
        }

        var updatedDog = dogRepository.save(dog);
        return dogMapper.toDto(updatedDog);
    }

    @Transactional
    public void deleteDog(Long id) {
        var dog = dogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND_WITH_ID + id));

        if (!dog.getOwner().getEmail().equals(SecurityUtils.getUserEmail()) && !SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("Access denied!");
        }

        dogRepository.delete(dog);

        Path dogDirectory = get(baseUploadDir, "dogs", String.valueOf(id));
        PhotosUtils.deleteDirectoryRecursively(dogDirectory);
    }

    public List<DogDto> getDogsByOwner(Long ownerId) {
        var dogsByOwnerId = dogRepository.findAllByOwner_UserId(ownerId);
        return dogsByOwnerId.stream()
                .map(dogMapper::toDto)
                .toList();
    }

    public List<DogPhotoDto> getDogPhotos(Long id) {
        if (!dogRepository.existsById(id)) {
            throw new NotFoundException(DOG_NOT_FOUND_WITH_ID + id);
        }
        return dogPhotoRepository.getDogPhotosByDog_DogId(id).stream()
                .map(dogMapper::toPhotoDto)
                .toList();
    }

    @Transactional
    public List<DogPhotoDto> addDogPhotos(Long id, List<MultipartFile> files) {
        var dog = dogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DOG_NOT_FOUND_WITH_ID + id));

        if (!dog.getOwner().getEmail().equals(SecurityUtils.getUserEmail()) && !SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("Access denied!");
        }

        var uploadDir = get(baseUploadDir, "dogs", valueOf(id));

        try {
            createDirectories(uploadDir);
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload directory at " + uploadDir, e);
        }

        var newPhotos = files.stream()
                .filter(Objects::nonNull)
                .filter(file -> !file.isEmpty())
                .map(file -> PhotosUtils.saveSinglePhoto(file, dog, uploadDir))
                .toList();

        dog.getPhotos().addAll(newPhotos);

        dogRepository.save(dog);

        return newPhotos.stream()
                .map(dogMapper::toPhotoDto)
                .toList();
    }

    @Transactional
    public void deleteDogPhoto(Long id, Long photoId) {
        var photo = dogPhotoRepository.findById(photoId)
                .orElseThrow(() -> new NotFoundException("Photo not found"));

        if (!photo.getDog().getOwner().getEmail().equals(SecurityUtils.getUserEmail()) && !SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("Access denied!");
        }

        if (!photo.getDog().getDogId().equals(id)) {
            throw new IllegalArgumentException("Photo does not belong to the specified dog");
        }

        try {
            deleteIfExists(get(photo.getUrl()));
        } catch (IOException e) {
            log.error("Error: Could not delete file: {}", photo.getUrl(), e);
        }

        var dog = photo.getDog();
        dog.getPhotos().remove(photo);
        dogRepository.save(dog);
    }

    public List<BreedDto> getAllBreeds() {
        return breedRepository.findAll().stream()
                .map(dogMapper::toBreedDto)
                .toList();
    }
}
