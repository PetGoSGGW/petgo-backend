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
import pl.petgo.backend.dto.dog.*;
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
@Tag(name = "Dog Module", description = "Endpoints for managing dogs and their photos")
public class DogController {

    DogService dogService;

    @Operation(
            summary = "Get all dogs",
            description = "Fetches a list of all dogs"
    )
    @GetMapping
    public ResponseEntity<List<DogDto>> getDogs() {
        return ok(dogService.getAllDogs());
    }

    @Operation(
            summary = "Get a dog by ID",
            description = "Returns dog information based on the id"
    )
    @GetMapping("/{id}")
    public ResponseEntity<DogDto> getDog(@PathVariable Long id) {
        return ok(dogService.getDogById(id));
    }

    @Operation(
            summary = "Add a new dog",
            description = "Adds a new dog to the system"
    )
    @PostMapping
    public ResponseEntity<DogDto> addDog(@RequestBody @Valid DogCreateRequestDto request) {
        return status(CREATED)
                .body(dogService.addDog(request));
    }

    @Operation(
            summary = "Update dog details",
            description = "Updates specific information for an existing dog"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<DogDto> updateDog(@PathVariable Long id,
                                            @RequestBody @Valid DogUpdateRequestDto request) {
        return ok(dogService.updateDog(id, request));
    }

    @Operation(
            summary = "Delete a dog",
            description = "Removes a dog data"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDog(@PathVariable Long id) {
        dogService.deleteDog(id);
        return noContent().build();
    }

    @Operation(
            summary = "Get dogs by owner",
            description = "Fetches a list of dogs belonging to a specific owner"
    )
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<DogDto>> getOwnerDogs(@PathVariable Long ownerId) {
        return ok(dogService.getDogsByOwner(ownerId));
    }

    @Operation(
            summary = "Get dog photos",
            description = "Returns a list of photo metadata associated with a specific dog"
    )
    @GetMapping("/{id}/photos")
    public ResponseEntity<List<DogPhotoDto>> getDogPhotos(@PathVariable Long id) {
        return ok(dogService.getDogPhotos(id));
    }

    @Operation(
            summary = "Get all breeds",
            description = "Fetches a list of all available dog breeds"
    )
    @GetMapping("/breeds")
    public ResponseEntity<List<BreedDto>> getBreeds() {
        return ok(dogService.getAllBreeds());
    }

    @Operation(
            summary = "Add photos for a dog",
            description = "Adds one or more image files and associate them with dog by dog Id"
    )
    @PostMapping("/{id}/photos")
    public ResponseEntity<List<DogPhotoDto>> addDogPhotos(@PathVariable Long id,
                                                          @RequestPart("files") List<MultipartFile> files) {
        return status(CREATED)
                .body(dogService.addDogPhotos(id, files));
    }

    @Operation(
            summary = "Delete a specific photo",
            description = "Deletes a specific photo associated with a dog based on the dog Id and photo Id"
    )
    @DeleteMapping("/{id}/photos/{photoId}")
    public ResponseEntity<Void> deleteDogPhoto(@PathVariable Long id, @PathVariable Long photoId) {
        dogService.deleteDogPhoto(id, photoId);
        return noContent().build();
    }
}