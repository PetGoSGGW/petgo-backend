package pl.petgo.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.wallet.*;
import pl.petgo.backend.service.WalletService;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.getWallet(id));
    }

    @GetMapping("/{id}/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.getTransactions(id));
    }

    @PostMapping("/{id}/topup")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletResponse> topup(
            @PathVariable Long id,
            @Valid @RequestBody TopupRequest request
    ) {
        return ResponseEntity.ok(walletService.topup(id, request));
    }

    @PostMapping("/{id}/payout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletResponse> payout(
            @PathVariable Long id,
            @Valid @RequestBody PayoutRequest request
    ) {
        return ResponseEntity.ok(walletService.payout(id, request));
    }
}

