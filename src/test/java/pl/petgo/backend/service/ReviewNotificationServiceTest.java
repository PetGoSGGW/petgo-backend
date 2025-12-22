package pl.petgo.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.petgo.backend.domain.ReviewType;
import pl.petgo.backend.dto.review.ReviewReminderDTO;
import pl.petgo.backend.events.NotificationEventName;
import pl.petgo.backend.repository.ReviewRepository;

import static org.mockito.Mockito.*;

public class ReviewNotificationServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private SseService sseService;

    @InjectMocks
    private ReviewNotificationService reviewNotificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendReviewReminder_AlreadyHandled() {
        // Given
        Long reservationId = 1L;
        Long recipientUserId = 2L;
        ReviewReminderDTO reminder = new ReviewReminderDTO(reservationId, "DogName", ReviewType.WALKER);

        when(reviewRepository.existsByReservationReservationIdAndReviewType(reservationId,  ReviewType.WALKER)).thenReturn(true);

        // When
        reviewNotificationService.sendReviewReminder(recipientUserId, reminder);

        // Then
        verify(sseService, never()).sendNotification(anyLong(), any(ReviewReminderDTO.class), eq(NotificationEventName.REVIEW_REMINDER));
    }

    @Test
    public void testSendReviewReminder_NotAlreadyHandled() {
        // Given
        Long reservationId = 1L;
        Long recipientUserId = 2L;
        ReviewReminderDTO reminder = new ReviewReminderDTO(reservationId, "DogName",  ReviewType.WALK);

        when(reviewRepository.existsByReservationReservationIdAndReviewType(reservationId, ReviewType.WALK)).thenReturn(false);

        // When
        reviewNotificationService.sendReviewReminder(recipientUserId, reminder);

        // Then
        verify(sseService).sendNotification(recipientUserId, reminder, NotificationEventName.REVIEW_REMINDER);
    }
}
