package pl.petgo.backend.dto;

import jakarta.validation.constraints.*;
import pl.petgo.backend.domain.Role;

public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Role role,
        @NotBlank String username
) {

}

