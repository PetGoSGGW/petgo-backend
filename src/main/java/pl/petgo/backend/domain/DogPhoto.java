package pl.petgo.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "dog_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DogPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt = Instant.now();

    @PrePersist
    protected void onUpload() {
        uploadedAt = Instant.now();
    }
}
