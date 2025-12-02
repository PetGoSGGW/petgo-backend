package pl.petgo.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.GpsPointDto;
import pl.petgo.backend.dto.GpsPointRequest;
import pl.petgo.backend.service.GpsTrackingService;

import java.util.List;

@RestController
@RequestMapping("/api/gps")
@RequiredArgsConstructor
public class GpsController {
    private final GpsTrackingService gpsTrackingService;

    // TODO: consider removing later
    @GetMapping
    public ResponseEntity<String> gpsInfo() {
        return ResponseEntity.ok("GPS Service is running");
    }


    @PostMapping("/start/{reservationId}")
    public ResponseEntity<Long> start(@PathVariable Long reservationId) {
        Long sessionId = gpsTrackingService.startSession(reservationId).getSessionId();
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionId);

    }

    @PostMapping("/point")
    public ResponseEntity<Void> addGpsPoint(@RequestBody GpsPointRequest req) {
        gpsTrackingService.recordPoint(req.sessionId(), req.latitude(), req.longitude());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/stop/{sessionId}")
    public ResponseEntity<Void> stop(@PathVariable Long sessionId) {
        gpsTrackingService.stopSession(sessionId);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/route/{reservationId}")
    public ResponseEntity<List<GpsPointDto>> route(@PathVariable Long reservationId) {
        List<GpsPointDto> route = gpsTrackingService.getRouteDto(reservationId);
        return ResponseEntity.ok(route);

    }
}
