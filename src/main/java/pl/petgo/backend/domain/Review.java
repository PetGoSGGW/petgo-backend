package pl.petgo.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"reservation", "author", "subjectUser", "dog"})
@ToString(exclude = {"reservation", "author", "subjectUser", "dog"})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_user_id")
    private User subjectUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id")
    private Dog dog;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_type", nullable = false)
    private ReviewType reviewType;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment")
    private String comment;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public static Review createFromReservation(Reservation reservation, ReviewType type, Integer rating, String comment, Long authorId) {
        ReviewBuilder builder = Review.builder()
                .reservation(reservation)
                .reviewType(type)
                .rating(rating)
                .comment(comment);

        boolean isOwner = reservation.getOwner().getUserId().equals(authorId);
        if (isOwner) {
            builder.author(reservation.getOwner());
            builder.subjectUser(reservation.getWalker());
        } else {
            builder.author(reservation.getWalker());
            builder.dog(reservation.getDog());
        }

        return builder.build();
    }
}