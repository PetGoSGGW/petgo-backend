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

        if (reservation.getOwner().getUserId().equals(authorId)) {
            builder.subjectUser(reservation.getWalker());
        } else {
            builder.dog(reservation.getDog());
        }

        if (ReviewType.WALKER.equals(type)) {
            builder.subjectUser(reservation.getWalker());
        } else if (ReviewType.DOG.equals(type) || ReviewType.WALK.equals(type)) {
            builder.dog(reservation.getDog());
        }

        return builder.build();
    }
}