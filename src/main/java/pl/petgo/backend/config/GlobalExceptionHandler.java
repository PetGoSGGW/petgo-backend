package pl.petgo.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pl.petgo.backend.dto.ErrorDetails;
import pl.petgo.backend.exception.DtoBuildException;
import pl.petgo.backend.exception.FileStorageException;
import pl.petgo.backend.exception.NotFoundException;

import java.util.HashMap;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String REQUESTED_RESOURCE_WAS_NOT_FOUND_MESSAGE = "The requested resource was not found";
    private static final String INVALID_EMAIL_OR_PASSWORD_MESSAGE = "Invalid email or password";
    private static final String VALIDATION_ERROR_MESSAGE = "Validation failed for some fields!";
    private static final String FILE_TO_LARGE_ERROR_MESSAGE = "Uploaded file is too large!";
    private static final String FILE_STORAGE_ERROR_MESSAGE = "Error with file storage occurred. Please try again later!";
    private static final String ACCESS_DENIED_ERROR_MESSAGE = "You do not have permission to access this resource!";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An unexpected internal error occurred!";

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFound(NoResourceFoundException ex) {
        log.debug("Resource not found: {}", ex.getResourcePath());

        var errorDetails = new ErrorDetails(
                NOT_FOUND.value(),
                NOT_FOUND.getReasonPhrase(),
                REQUESTED_RESOURCE_WAS_NOT_FOUND_MESSAGE
        );

        return new ResponseEntity<>(errorDetails, NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentials(UsernameNotFoundException ex) {
        log.debug("Username not found: {}", ex.getMessage());

        var errorDetails = new ErrorDetails(
                NOT_FOUND.value(),
                NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );

        return new ResponseEntity<>(errorDetails, NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentials(BadCredentialsException ex) {
        log.debug("Authentication failed: {}", ex.getMessage());

        var errorDetails = new ErrorDetails(
                UNAUTHORIZED.value(),
                UNAUTHORIZED.getReasonPhrase(),
                INVALID_EMAIL_OR_PASSWORD_MESSAGE
        );

        return new ResponseEntity<>(errorDetails, UNAUTHORIZED);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> handleEntityNotFound(NotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());

        var errorDetails = new ErrorDetails(
                NOT_FOUND.value(),
                NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorDetails, NOT_FOUND);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorDetails> handleFileStorageException(FileStorageException ex) {
        log.error("File storage error: {}", ex.getMessage(), ex);

        var errorDetails = new ErrorDetails(
                INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR.getReasonPhrase(),
                FILE_STORAGE_ERROR_MESSAGE
        );

        return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        var validationErrors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    validationErrors.put(fieldName, errorMessage);
                });

        var errorDetails = new ErrorDetails(
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                VALIDATION_ERROR_MESSAGE,
                validationErrors
        );

        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());

        var errorDetails = new ErrorDetails(
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );

        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorDetails> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        log.warn("File too large: {}", ex.getMessage());

        var errorDetails = new ErrorDetails(
                PAYLOAD_TOO_LARGE.value(),
                PAYLOAD_TOO_LARGE.getReasonPhrase(),
                FILE_TO_LARGE_ERROR_MESSAGE
        );

        return new ResponseEntity<>(errorDetails, PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());

        var errorDetails = new ErrorDetails(
                FORBIDDEN.value(),
                FORBIDDEN.getReasonPhrase(),
                ACCESS_DENIED_ERROR_MESSAGE
        );

        return new ResponseEntity<>(errorDetails, FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        var errorDetails = new ErrorDetails(
                INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR.getReasonPhrase(),
                INTERNAL_SERVER_ERROR_MESSAGE
        );

        return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DtoBuildException.class)
    public ResponseEntity<String> handleDtoBuildException(DtoBuildException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Błąd podczas generowania recenzji");
    }
}
