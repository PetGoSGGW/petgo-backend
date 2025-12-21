package pl.petgo.backend.dto;

import pl.petgo.backend.domain.Role;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,        // np. "ADMIN"
        String firstName,
        String lastName,
        LocalDate dateOfBirth
) {
}

