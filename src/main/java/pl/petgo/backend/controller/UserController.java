package pl.petgo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "User Module",
        description = "Endpoints for managing users, their profiles, addresses, and wallets"
)
public class UserController {
    private final UserService users;

    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users. Typically restricted to administrators."
    )
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(@AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(users.findAll());
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves detailed information about a user by their ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal AppUserDetails principal,
                                                @PathVariable Long id) {
        return ResponseEntity.ok(users.findById(id));
    }

    @Operation(
            summary = "Get user addresses",
            description = "Returns all addresses associated with a specific user. " +
                    "Accessible by the user themselves or an administrator."
    )
    @GetMapping("/{id}/addresses")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.userId")
    public ResponseEntity<List<AddressResponse>> getAddresses(@AuthenticationPrincipal AppUserDetails principal,
                                                              @PathVariable Long id) {
        return ResponseEntity.ok(users.getAddressesForUser(id));
    }

    @Operation(
            summary = "Get user wallet",
            description = "Retrieves wallet details for a specific user, including balance information."
    )
    @GetMapping("/{id}/wallet")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.userId")
    public ResponseEntity<WalletResponse> getWallet(@AuthenticationPrincipal AppUserDetails principal,
                                                    @PathVariable Long id) {
        return ResponseEntity.ok(users.getWalletByUser(id));
    }

    @Operation(
            summary = "Add address to user",
            description = "Creates a new address and assigns it to the specified user."
    )
    @PostMapping(value = "/{id}/addresses", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.userId")
    public ResponseEntity<AddressResponse> addAddress(@AuthenticationPrincipal AppUserDetails principal,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody CreateAddressRequest address) {
        AddressResponse created = users.addAddress(id, address);
        URI location = URI.create("/api/v1/users/%d/addresses/%d".formatted(id, created.addressId()));
        return ResponseEntity.created(location).body(created);
    }

    @Operation(
            summary = "Update user data",
            description = "Updates user profile data such as name, email, or other editable fields."
    )
    @PatchMapping(value = "/{id}", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.userId")
    public ResponseEntity<UserResponse> changeUser(@AuthenticationPrincipal AppUserDetails principal,
                                                   @PathVariable Long id,
                                                   @Valid @RequestBody UpdateUserRequest user) {
        return ResponseEntity.ok(users.updateUser(id, user));
    }
}