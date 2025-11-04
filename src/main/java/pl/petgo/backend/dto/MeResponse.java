package pl.petgo.backend.dto;

public record MeResponse(
        Long id,
        String email,
        String role,
        String firstName,
        String lastName
) {}
