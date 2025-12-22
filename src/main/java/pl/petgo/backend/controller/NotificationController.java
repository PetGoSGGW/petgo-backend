package pl.petgo.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.SseService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final SseService sseService;

    public NotificationController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@AuthenticationPrincipal AppUserDetails user) {
        return sseService.subscribe(user.getUser().getUserId());
    }
}
