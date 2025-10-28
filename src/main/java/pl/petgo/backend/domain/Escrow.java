package pl.petgo.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "escrows")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Escrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long escrowId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(nullable = false)
    private Long amountCents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EscrowStatus status = EscrowStatus.HELD;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant releasedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_transaction_id")
    private Transaction walletTransaction;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}