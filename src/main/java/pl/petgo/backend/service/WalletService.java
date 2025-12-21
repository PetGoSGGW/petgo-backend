package pl.petgo.backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.petgo.backend.domain.Transaction;
import pl.petgo.backend.domain.TransactionType;
import pl.petgo.backend.domain.Wallet;
<<<<<<< HEAD
import pl.petgo.backend.dto.*;
=======
import pl.petgo.backend.dto.PayoutRequest;
import pl.petgo.backend.dto.TopupRequest;
import pl.petgo.backend.dto.WalletResponse;
import pl.petgo.backend.dto.TransactionResponse;
>>>>>>> 70434ab2dbddafc2cacb9cb7e41936548d2e08b3
import pl.petgo.backend.repository.TransactionRepository;
import pl.petgo.backend.repository.WalletRepository;
import pl.petgo.backend.security.CurrentUserService;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public pl.petgo.backend.dto.WalletResponse getWallet(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + id));

        return new pl.petgo.backend.dto.WalletResponse(
                wallet.getWalletId(),
                wallet.getUser().getUserId(),
                wallet.getCurrency(),
                wallet.getBalanceCents(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<pl.petgo.backend.dto.wallet.TransactionResponse> getTransactions(Long walletId) {
        return transactionRepository
                .findByWalletWalletIdOrderByCreatedAtDesc(walletId)
                .stream()
                .map(tx -> new pl.petgo.backend.dto.wallet.TransactionResponse(
                        tx.getTransactionId(),
                        tx.getUser().getUserId(),
                        tx.getAmountCents(),
                        tx.getBalanceAfterCents(),
                        tx.getType().name(),
                        tx.getDescription(),
                        tx.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
<<<<<<< HEAD
    public pl.petgo.backend.dto.WalletResponse topup(Long walletId, pl.petgo.backend.dto.wallet.TopupRequest request) {
=======
    public WalletResponse topup(Long walletId, @Valid TopupRequest request) {
>>>>>>> 70434ab2dbddafc2cacb9cb7e41936548d2e08b3
        Long currentUserId = currentUserService.getCurrentUserId();

        if (request.amountCents() == null || request.amountCents() <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));

        if (!wallet.getUser().getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("You cannot modify someone else's wallet");
        }

        Long newBalance = wallet.getBalanceCents() + request.amountCents();
        wallet.setBalanceCents(newBalance);

        Transaction tx = Transaction.builder()
                .wallet(wallet)
                .user(wallet.getUser())
                .amountCents(request.amountCents())
                .balanceAfterCents(newBalance)
                .type(TransactionType.TOPUP)
                .description(request.description())
                .createdAt(Instant.now())
                .build();

        transactionRepository.save(tx);
        walletRepository.save(wallet);

        return getWallet(walletId);
    }

    @Transactional
<<<<<<< HEAD
    public pl.petgo.backend.dto.WalletResponse payout(Long walletId, pl.petgo.backend.dto.wallet.PayoutRequest request) {
=======
    public WalletResponse payout(Long walletId, @Valid PayoutRequest request) {
>>>>>>> 70434ab2dbddafc2cacb9cb7e41936548d2e08b3
        Long currentUserId = currentUserService.getCurrentUserId();

        if (request.amountCents() == null || request.amountCents() <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));

        if (!wallet.getUser().getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("You cannot withdraw from someone else's wallet");
        }

        if (wallet.getBalanceCents() < request.amountCents()) {
            throw new IllegalStateException("Insufficient funds");
        }

        Long newBalance = wallet.getBalanceCents() - request.amountCents();
        wallet.setBalanceCents(newBalance);

        Transaction tx = Transaction.builder()
                .wallet(wallet)
                .user(wallet.getUser())
                .amountCents(request.amountCents())
                .balanceAfterCents(newBalance)
                .type(TransactionType.PAYOUT)
                .description(request.description())
                .createdAt(Instant.now())
                .build();

        transactionRepository.save(tx);
        walletRepository.save(wallet);

        return getWallet(walletId);
    }
}

