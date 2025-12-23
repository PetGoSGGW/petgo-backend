package pl.petgo.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.petgo.backend.dto.review.ReviewReminderDTO;
import pl.petgo.backend.events.NotificationEventName;
import pl.petgo.backend.repository.ReviewRepository;

@Service
@Slf4j
public class ReviewNotificationService {

    private final ReviewRepository reviewRepository;
    private final SseService sseService;

    public ReviewNotificationService(ReviewRepository reviewRepository, SseService sseService) {
        this.reviewRepository = reviewRepository;
        this.sseService = sseService;
    }

    public void sendReviewReminder(Long recipientUserId, ReviewReminderDTO reminder) {
        boolean alreadyHandled = reviewRepository.existsByReservationReservationIdAndReviewType(reminder.getReservationId(), reminder.getReviewType());
        if (alreadyHandled) {
            log.warn("Pominięto duplikat powiadomienia dla rezerwacji: {}", reminder.getReservationId());
            return;
        }

        sseService.sendNotification(recipientUserId, reminder, NotificationEventName.REVIEW_REMINDER);
    }
}