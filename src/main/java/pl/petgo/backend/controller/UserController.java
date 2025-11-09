package pl.petgo.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.*;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users", produces = "application/json")
@RequiredArgsConstructor
@Validated
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
    @PreAuthorize("hasRole('ADMIN') or #id == principal.userId")
    public ResponseEntity<List<AddressResponse>> getAddresses(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        return ResponseEntity.ok(users.getAddressesForUser(id));
    }

    @GetMapping("/{id}/wallet")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.userId")
 ResponseEntity<WalletResponse> getWallet(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        return ResponseEntity.ok(users.getWalletByUser(id));
    }

    // POST /api/v1/users/{id}/addresses
    @PostMapping(value = "/{id}/addresses", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.userId")
    public ResponseEntity<AddressResponse> addAddress(@AuthenticationPrincipal AppUserDetails principal,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody CreateAddressRequest address) {
        AddressResponse created = users.addAddress(id, address);
        URI location = URI.create("/api/v1/users/%d/addresses/%d".formatted(id, created.addressId()));
        return ResponseEntity.created(location).body(created);
    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressResponse> setAddresses(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id, @Valid @RequestBody CreateAddressRequest address) {
        return ResponseEntity.ok(users.addAddress(id, address));
    }

    // PATCH /api/v1/users/{id}
    @PatchMapping(value = "/{id}", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.userId")
    public ResponseEntity<UserResponse> changeUser(@AuthenticationPrincipal AppUserDetails principal,
                                                   @PathVariable Long id,
                                                   @Valid @RequestBody UpdateUserRequest user) {
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> changeUser(@AuthenticationPrincipal AppUserDetails principal, @PathVariable long id, @Valid @RequestBody UpdateUserRequest user) {
        return ResponseEntity.ok(users.updateUser(id, user));
    }
}
