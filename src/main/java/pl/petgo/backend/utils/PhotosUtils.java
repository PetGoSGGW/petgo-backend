package pl.petgo.backend.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import pl.petgo.backend.domain.Dog;
import pl.petgo.backend.domain.DogPhoto;
import pl.petgo.backend.exception.FileStorageException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.copy;
import static java.nio.file.Files.exists;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Comparator.reverseOrder;
import static java.util.UUID.randomUUID;

@UtilityClass
@Slf4j
public class PhotosUtils {

    public DogPhoto saveSinglePhoto(MultipartFile file, Dog dog, Path uploadDir) {
        var originalFilename = file.getOriginalFilename();
        var extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        var fileName = randomUUID() + extension;
        var filePath = uploadDir.resolve(fileName);

        try {
            copy(file.getInputStream(), filePath, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Failed to save file " + fileName, e);
        }

        return DogPhoto.builder()
                .dog(dog)
                .url(filePath.toString())
                .build();
    }

    public void deleteDirectoryRecursively(Path path) {
        if (!exists(path)) {
            return;
        }

        try (var walk = Files.walk(path)) {
            walk.sorted(reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        var deleted = file.delete();
                        if (!deleted) {
                            log.warn("Warning: Could not delete file: {}", file.getAbsolutePath());
                        }
                    });
        } catch (IOException e) {
            log.error("Error occurred while deleting directory recursively: {}", path, e);
        }
    }
}
