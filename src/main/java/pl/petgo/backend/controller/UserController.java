package pl.petgo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.*;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.UserService;

import java.util.List;
@RequiredArgsConstructor
@Tag(
        name = "Users",
        description = "Operations related to application users"
)
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService users;

    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users. доступ restricted to administrators only."
    )
    @GetMapping("/")
    public ResponseEntity<List<UserResponse>> getUsers(@AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(users.findAll());
    }

    @Operation(
            summary = "Get user by ID",
            description = "Returns user details. Accessible by administrators or the account owner."
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        return ResponseEntity.ok(users.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(
            summary = "Get user addresses",
            description = "Returns all addresses assigned to the user. Accessible by administrators or the account owner."
    )
    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id) {
        return ResponseEntity.ok(users.getAddressesForUser(id));
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(
            summary = "Add user address",
            description = "Adds a new address to the user account. Accessible by administrators or the account owner."
    )
    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressResponse> setAddresses(@AuthenticationPrincipal AppUserDetails principal, @PathVariable Long id, @Valid @RequestBody CreateAddressRequest address) {
        return ResponseEntity.ok(users.addAddress(id, address));
    }


    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(
            summary = "Update user data",
            description = "Updates user personal data. Accessible by administrators or the account owner."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> changeUser(@AuthenticationPrincipal AppUserDetails principal, @PathVariable long id, @Valid @RequestBody UpdateUserRequest user) {
        return ResponseEntity.ok(users.updateUser(id, user));
    }
}
