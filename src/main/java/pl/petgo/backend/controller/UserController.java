package pl.petgo.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.*;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService users;

    @GetMapping("/")
    public ResponseEntity<List<UserResponse>> getUsers(@AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(users.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        return ResponseEntity.ok(users.findById(id));
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        return ResponseEntity.ok(users.getAddressesForUser(id));
    }

    @GetMapping("/{id}/wallet")
    public ResponseEntity<WalletResponse> getWallet(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        return ResponseEntity.ok(users.getWalletByUser(id));
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressResponse> setAddresses(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id, @Valid @RequestBody CreateAddressRequest address) {
        return ResponseEntity.ok(users.addAddress(id, address));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> changeUser(@AuthenticationPrincipal AppUserDetails principal, @PathVariable long id, @Valid @RequestBody UpdateUserRequest user) {
        return ResponseEntity.ok(users.updateUser(id, user));
    }
}
