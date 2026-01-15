package pl.petgo.backend.dto.user;

import pl.petgo.backend.domain.Role;

import java.time.LocalDate;

public record UpdateUserRequest(String username,
                                String firstName,
                                String lastName,
                                LocalDate dateOfBirth,
                                String email,
                                Role role) {
}
