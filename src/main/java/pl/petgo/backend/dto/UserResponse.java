package pl.petgo.backend.dto;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role,        // np. "ADMIN"
        String firstName,
        String lastName,
        LocalDate dateOfBirth
) {
}

