package pl.petgo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.petgo.backend.dto.dog.DogCreateRequestDto;
import pl.petgo.backend.dto.dog.DogDto;
import pl.petgo.backend.dto.dog.DogPhotoDto;
import pl.petgo.backend.dto.dog.DogUpdateRequestDto;
import pl.petgo.backend.service.DogService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/api/dogs")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Dog Module", description = "Endpoints for managing dogs and their photos.")
public class DogController {

    DogService dogService;

    @GetMapping
    public ResponseEntity<List<DogDto>> getDogs() {
        return ok(dogService.getAllDogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DogDto> getDog(@PathVariable Long id) {
        return ok(dogService.getDogById(id));
    }

    @Operation(summary = "Add a new dog")
    @PostMapping
    public ResponseEntity<DogDto> addDog(@RequestBody @Valid DogCreateRequestDto request) {
        return status(CREATED)
                .body(dogService.addDog(request));
    }

    @Operation(summary = "Update a dog")
    @PatchMapping("/{id}")
    public ResponseEntity<DogDto> updateDog(@PathVariable Long id,
                                            @RequestBody @Valid DogUpdateRequestDto request) {
        return ok(dogService.updateDog(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDog(@PathVariable Long id) {
        dogService.deleteDog(id);
        return noContent().build();
    }

    @GetMapping(params = "ownerId")
    public ResponseEntity<List<DogDto>> getOwnerDogs(@RequestParam Long ownerId) {
        return ok(dogService.getDogsByOwner(ownerId));
    }

    @GetMapping("/{id}/photos")
    public ResponseEntity<List<DogPhotoDto>> getDogPhotos(@PathVariable Long id) {
        return ok(dogService.getDogPhotos(id));
    }

    @Operation(summary = "Add a photo to a dog")
    @PostMapping("/{id}/photos")
    public ResponseEntity<List<DogPhotoDto>> addDogPhotos(@PathVariable Long id,
                                                          @RequestPart("files") List<MultipartFile> files) {
        return status(CREATED)
                .body(dogService.addDogPhotos(id, files));
    }

    @DeleteMapping("/{id}/photos/{photoId}")
    public ResponseEntity<Void> deleteDogPhoto(@PathVariable Long id, @PathVariable Long photoId) {
        dogService.deleteDogPhoto(id, photoId);
        return noContent().build();
    }
}
