package pl.petgo.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "domain_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DomainEventType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_dog_id")
    private Dog targetDog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_reservation_id")
    private Reservation targetReservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_wallet_id")
    private Wallet targetWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_transaction_id")
    private Transaction targetTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_payment_id")
    private Payment targetPayment;

    @Column(length = 1000)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @Column(nullable = false, updatable = false)
    private Instant occurredAt = Instant.now();

    @PrePersist
    protected void onCreate() {
        occurredAt = Instant.now();
    }
}
