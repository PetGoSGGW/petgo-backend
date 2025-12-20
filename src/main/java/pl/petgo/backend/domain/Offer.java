package pl.petgo.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offer_id")
    private Long offerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walker_id", nullable = false)
    private User walker;

    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilitySlot> availabilitySlots;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}