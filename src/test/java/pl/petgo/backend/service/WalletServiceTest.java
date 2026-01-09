package pl.petgo.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.petgo.backend.domain.Transaction;
import pl.petgo.backend.domain.TransactionType;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.domain.Wallet;
import pl.petgo.backend.repository.TransactionRepository;
import pl.petgo.backend.repository.WalletRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void addFundsSystem_ShouldIncreaseBalance_AndCreateTopupTransaction() {
        // GIVEN
        Long userId = 1L;
        Long initialBalance = 1000L;
        Long amountToAdd = 500L;
        Long expectedBalance = 1500L;

        User user = User.builder().userId(userId).build();
        Wallet wallet = Wallet.builder()
                .walletId(10L)
                .user(user)
                .balanceCents(initialBalance)
                .build();

        when(walletRepository.findByUserUserId(userId)).thenReturn(Optional.of(wallet));

        // WHEN
        walletService.addFundsSystem(userId, amountToAdd, "Payment received");

        // THEN
        assertThat(wallet.getBalanceCents()).isEqualTo(expectedBalance);
        verify(walletRepository).save(wallet);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());

        Transaction savedTx = txCaptor.getValue();
        assertThat(savedTx.getAmountCents()).isEqualTo(amountToAdd);
        assertThat(savedTx.getBalanceAfterCents()).isEqualTo(expectedBalance);
        assertThat(savedTx.getType()).isEqualTo(TransactionType.TOPUP);
    }

    @Test
    void deductFundsSystem_ShouldDecreaseBalance_AndCreatePaymentTransaction() {
        // GIVEN
        Long userId = 2L;
        Long initialBalance = 800L;
        Long amountToDeduct = 125L;
        Long expectedBalance = 675L;

        User user = User.builder().userId(userId).build();
        Wallet wallet = Wallet.builder()
                .walletId(20L)
                .user(user)
                .balanceCents(initialBalance)
                .build();

        when(walletRepository.findByUserUserId(userId)).thenReturn(Optional.of(wallet));

        // WHEN
        walletService.deductFundsSystem(userId, amountToDeduct, "Payment sent");

        // THEN
        assertThat(wallet.getBalanceCents()).isEqualTo(expectedBalance);
        verify(walletRepository).save(wallet);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());

        Transaction savedTx = txCaptor.getValue();
        assertThat(savedTx.getAmountCents()).isEqualTo(amountToDeduct);
        assertThat(savedTx.getBalanceAfterCents()).isEqualTo(expectedBalance);
        assertThat(savedTx.getType()).isEqualTo(TransactionType.PAYMENT);
    }
}