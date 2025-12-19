package pl.petgo.backend.domain;

public enum PaymentStatus {
    PENDING,
    PAID,
    REFUNDED,
    FAILED,
    // PGB-14_Payment_Module
    // Potrzebny nowy status "CANCELED" do platnosci Stripe
    CANCELED
}
