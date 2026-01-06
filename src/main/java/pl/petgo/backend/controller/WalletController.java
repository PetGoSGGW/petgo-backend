package pl.petgo.backend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import pl.petgo.backend.dto.TransactionResponse;
import pl.petgo.backend.dto.TopupRequest;
import pl.petgo.backend.dto.PayoutRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.WalletResponse;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Tag(
    name = "Wallets Module",
    description = "Operations related to user wallets (balance, transactions, top-ups, payouts)"
)
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get my wallet",
            description = "Returns wallet details for the currently logged-in user"
    )
    public ResponseEntity<WalletResponse> getMyWallet(@AuthenticationPrincipal AppUserDetails principal) {
        Long userId = principal.getUser().getUserId();
        return ResponseEntity.ok(walletService.getWalletByUserId(userId));
    }

    @GetMapping("/me/transactions")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get my wallet transactions",
            description = "Returns a list of all transactions associated with the current user's wallet"
    )
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(@AuthenticationPrincipal AppUserDetails principal) {
        Long userId = principal.getUser().getUserId();
        return ResponseEntity.ok(walletService.getTransactionsByUserId(userId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get wallet",
        description = "Returns wallet details for the given wallet ID"
    )
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.getWallet(id));
    }

    @GetMapping("/{id}/transactions")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get wallet transactions",
        description = "Returns a list of all transactions associated with the given wallet"
    )
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.getTransactions(id));
    }

    @PostMapping("/{id}/topup")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Top up wallet",
        description = "Increases the wallet balance by the specified amount"
    )
    public ResponseEntity<WalletResponse> topup(
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable Long id,
            @Valid @RequestBody TopupRequest request
    ) {
        Long userId = principal.getUser().getUserId();

        return ResponseEntity.ok(walletService.topup(id, request, userId));
    }

    @PostMapping("/{id}/payout")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Payout from wallet",
        description = "Decreases the wallet balance by the specified amount and records a payout transaction"
    )
    public ResponseEntity<WalletResponse> payout(
            @AuthenticationPrincipal AppUserDetails principal,
            @PathVariable Long id,
            @Valid @RequestBody PayoutRequest request
    ) {
        Long userId = principal.getUser().getUserId();

        return ResponseEntity.ok(walletService.payout(id, request, userId));
    }
}

