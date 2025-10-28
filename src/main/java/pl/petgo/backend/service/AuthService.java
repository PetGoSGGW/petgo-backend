package pl.petgo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.petgo.backend.dto.*;
import pl.petgo.backend.entity.User;
import pl.petgo.backend.repository.UserRepository;
import pl.petgo.backend.security.JwtService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthResponse register(RegisterRequest req) {
        if (users.existsByEmail(req.email())) throw new IllegalArgumentException("Email already used");
        if (req.username()!=null && users.existsByUsername(req.username())) throw new IllegalArgumentException("Username already used");

        User u = User.builder()
                .email(req.email())
                .username(req.username())
                .passwordHash(encoder.encode(req.password()))
                .firstName(req.firstName())
                .lastName(req.lastName())
                .role(req.role())
                .isActive(true)
                .build();
        users.save(u);

        String token = jwt.generate(u.getEmail(), Map.of("uid", u.getUserId(), "role", u.getRole().name()));
        return new AuthResponse(token, u.getUserId(), u.getEmail(), u.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        User u = users.findByEmail(req.email()).orElseThrow();
        String token = jwt.generate(u.getEmail(), Map.of("uid", u.getUserId(), "role", u.getRole().name()));
        return new AuthResponse(token, u.getUserId(), u.getEmail(), u.getRole().name());
    }
}