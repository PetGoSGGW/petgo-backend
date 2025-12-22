package pl.petgo.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.petgo.backend.events.NotificationEventName;
import pl.petgo.backend.repository.inmemory.SseEmitterRepository;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

public class SseServiceTest {

    @Mock
    private SseEmitterRepository emitterRepository;

    @InjectMocks
    private SseService sseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSubscribe_UserExists() {
        // Given
        Long userId = 1L;
        SseEmitter existingEmitter = mock(SseEmitter.class);

        when(emitterRepository.get(userId)).thenReturn(Optional.of(existingEmitter));

        // When
        SseEmitter result = sseService.subscribe(userId);

        // Then
        assertSame(existingEmitter, result);
    }

    @Test
    public void testSubscribe_UserDoesNotExist() {
        // Given
        Long userId = 1L;
        when(emitterRepository.get(userId)).thenReturn(Optional.empty());

        // When
        SseEmitter result = sseService.subscribe(userId);

        // Then
        verify(emitterRepository).addOrReplaceEmitter(eq(userId), any(SseEmitter.class));
        assertNotNull(result);
    }

    @Test
    public void testSendNotification_UserExists() throws IOException {
        // Given
        Long recipientUserId = 1L;
        Object notificationData = new Object();
        NotificationEventName eventName = NotificationEventName.REVIEW_REMINDER;

        SseEmitter emitter = mock(SseEmitter.class);
        when(emitterRepository.get(recipientUserId)).thenReturn(Optional.of(emitter));

        // When
        sseService.sendNotification(recipientUserId, notificationData, eventName);

        // Then
        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    public void testSendNotification_UserDoesNotExist() {
        // Given
        Long recipientUserId = 1L;
        Object notificationData = new Object();
        NotificationEventName eventName = NotificationEventName.REVIEW_REMINDER;

        SseEmitter emitter = mock(SseEmitter.class);
        when(emitterRepository.get(recipientUserId)).thenReturn(Optional.empty());

        // When
        sseService.sendNotification(recipientUserId, notificationData, eventName);

        // Then
        verifyNoInteractions(emitter);
    }
}
