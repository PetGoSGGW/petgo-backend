package pl.petgo.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.petgo.backend.events.NotificationEventName;
import pl.petgo.backend.repository.inmemory.SseEmitterRepository;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class SseService {

    private final SseEmitterRepository emitterRepository;

    public SseService(SseEmitterRepository emitterRepository) {
        this.emitterRepository = emitterRepository;
    }

    public SseEmitter subscribe(Long userId) {
        Optional<SseEmitter> optionalSseEmitter = emitterRepository.get(userId);
        if (optionalSseEmitter.isPresent()) {
            return optionalSseEmitter.get();
        }

        SseEmitter emitter = new SseEmitter(1800000L);
        emitterRepository.addOrReplaceEmitter(userId, emitter);

        emitter.onCompletion(() -> removeEmitter(userId));
        emitter.onTimeout(() -> removeEmitter(userId));
        emitter.onError((e) -> removeEmitter(userId));

        return emitter;
    }


    private void removeEmitter(Long userId) {
        emitterRepository.remove(userId);
    }

    public void sendNotification(Long recipientUserId, Object notificationData, NotificationEventName eventName) {
        Optional<SseEmitter> optionalSseEmitter = emitterRepository.get(recipientUserId);
        if (optionalSseEmitter.isEmpty()) {
            return;
        }

        SseEmitter sseEmitter = optionalSseEmitter.get();
        try {
            sseEmitter.send(SseEmitter.event()
                    .name(eventName.name())
                    .data(notificationData));
        } catch (IOException e) {
            removeEmitter(recipientUserId);
        }
    }
}
