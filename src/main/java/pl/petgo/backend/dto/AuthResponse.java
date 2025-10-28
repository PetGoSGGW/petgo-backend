package pl.petgo.backend.dto;

public record AuthResponse(String accessToken, String tokenType, Long userId, String email, String role) {
    public AuthResponse(String accessToken, Long userId, String email, String role) {
        this(accessToken, "Bearer", userId, email, role);
    }
}
