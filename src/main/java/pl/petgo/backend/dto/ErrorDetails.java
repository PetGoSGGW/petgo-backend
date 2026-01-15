package pl.petgo.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record ErrorDetails(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        Map<String, String> validationErrors
) {
    public ErrorDetails(Integer status, String error, String message) {
        this(Instant.now(), status, error, message, null);
    }

    public ErrorDetails(Integer status, String error, String message, Map<String, String> validationErrors) {
        this(Instant.now(), status, error, message, validationErrors);
    }
}
