package pl.petgo.backend.repository.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class SseEmitterRepository  {

    private final Map<Long, SseEmitter> userEmitterMap = new ConcurrentHashMap<>();

    public void addOrReplaceEmitter(Long userId, SseEmitter emitter) {
        userEmitterMap.put(userId, emitter);
    }

    public void remove(Long userId) {
        if (userId != null){
            userEmitterMap.remove(userId);
            log.debug("Removed emitter for user {}", userId);
        }
    }

    public Optional<SseEmitter> get(Long userId) {
        return Optional.ofNullable(userEmitterMap.get(userId));
    }
}
