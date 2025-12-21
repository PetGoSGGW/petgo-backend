package pl.petgo.backend.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.petgo.backend.domain.Transaction;
import pl.petgo.backend.domain.TransactionType;
import pl.petgo.backend.domain.Wallet;
import pl.petgo.backend.dto.PayoutRequest;
import pl.petgo.backend.dto.TopupRequest;
import pl.petgo.backend.dto.WalletResponse;
import pl.petgo.backend.dto.TransactionResponse;
import pl.petgo.backend.repository.TransactionRepository;
import pl.petgo.backend.repository.WalletRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public WalletResponse getWallet(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + id));

        return new WalletResponse(
                wallet.getWalletId(),
                wallet.getUser().getUserId(),
                wallet.getCurrency(),
                wallet.getBalanceCents(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(Long walletId) {
        return transactionRepository
                .findByWalletWalletIdOrderByCreatedAtDesc(walletId)
                .stream()
                .map(tx -> new TransactionResponse(
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
    public WalletResponse topup(Long walletId, @Valid TopupRequest request, Long currentUserId) {

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
    public WalletResponse payout(Long walletId, @Valid PayoutRequest request, Long currentUserId) {

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

