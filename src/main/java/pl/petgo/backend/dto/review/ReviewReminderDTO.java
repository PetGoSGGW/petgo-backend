package pl.petgo.backend.dto.review;

import pl.petgo.backend.domain.ReviewType;

import java.util.Objects;

public class ReviewReminderDTO {

    private final Long reservationId;
    private final String dogName;
    private final ReviewType reviewType;
    private final String message;


    public ReviewReminderDTO(Long reservationId, String dogName, ReviewType reviewType) {
        this.reservationId = reservationId;
        this.dogName = dogName;
        this.reviewType = reviewType;
        this.message = createMessage();
    }

    private String createMessage() {
        return String.format("Właśnie zakończył się spacer z psem %s. Podziel się swoją opinią na temat %s", dogName, reviewType);
    }

    public Long getReservationId() {
        return reservationId;
    }

    public ReviewType getReviewType() {
        return reviewType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReviewReminderDTO that = (ReviewReminderDTO) o;
        return Objects.equals(reservationId, that.reservationId) && Objects.equals(dogName, that.dogName) && reviewType == that.reviewType && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId, dogName, reviewType, message);
    }
}
