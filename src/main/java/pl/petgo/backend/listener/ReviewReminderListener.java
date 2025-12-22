package pl.petgo.backend.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.scheduling.annotation.Async;
import pl.petgo.backend.domain.ReviewType;
import pl.petgo.backend.dto.review.ReviewReminderDTO;
import pl.petgo.backend.events.ReservationCompletedEvent;
import pl.petgo.backend.service.ReviewNotificationService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReviewReminderListener {

    private final ReviewNotificationService notificationService;

    public ReviewReminderListener(ReviewNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationCompleted(ReservationCompletedEvent event) {
        ReviewReminderDTO ownerRecipientReviewReminderDTO = new ReviewReminderDTO(event.reservationId(), event.dogName(), ReviewType.WALKER);
        notificationService.sendReviewReminder(event.ownerId(), ownerRecipientReviewReminderDTO);

        ReviewReminderDTO walkerRecipientReviewReminderDTO = new ReviewReminderDTO(event.reservationId(), event.dogName(), ReviewType.WALK);
        notificationService.sendReviewReminder(event.walkerId(), walkerRecipientReviewReminderDTO);
    }
}
