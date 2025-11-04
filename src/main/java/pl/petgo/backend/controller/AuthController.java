package pl.petgo.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.*;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(auth.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(auth.login(req));
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal AppUserDetails principal) {
        var u = principal.getUser();
        return new MeResponse(
                u.getUserId(),
                u.getEmail(),
                u.getRole().name(),
                u.getFirstName(),
                u.getLastName()
        );
    }
}