package pl.petgo.backend.listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.petgo.backend.domain.ReviewType;
import pl.petgo.backend.dto.review.ReviewReminderDTO;
import pl.petgo.backend.events.ReservationCompletedEvent;
import pl.petgo.backend.service.ReviewNotificationService;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class ReviewReminderListenerTest {

    @Mock
    private ReviewNotificationService notificationService;

    @InjectMocks
    private ReviewReminderListener reviewReminderListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleReservationCompleted() {
        // Given
        Long reservationId = 1L;
        ReservationCompletedEvent event = new ReservationCompletedEvent(reservationId, 2L, 3L, "DogName");
        ReviewReminderDTO ownerRecipientReviewReminderDTO = new ReviewReminderDTO(event.reservationId(), event.dogName(), ReviewType.WALKER);
        ReviewReminderDTO walkerRecipientReviewReminderDTO = new ReviewReminderDTO(event.reservationId(), event.dogName(), ReviewType.WALK);

        // When
        reviewReminderListener.handleReservationCompleted(event);

        // Then
        verify(notificationService).sendReviewReminder(eq(3L), eq(ownerRecipientReviewReminderDTO));
        verify(notificationService).sendReviewReminder(eq(2L), eq(walkerRecipientReviewReminderDTO));
    }
}
