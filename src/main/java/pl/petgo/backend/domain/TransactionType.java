package pl.petgo.backend.domain;

public enum TransactionType {
    TOPUP,
    PAYMENT,
    REFUND,
    PAYOUT,
    ESCROW_HOLD,
    ESCROW_RELEASE,
    ADJUSTMENT
}