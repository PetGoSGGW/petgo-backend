package pl.petgo.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.GpsPointDto;
import pl.petgo.backend.dto.GpsPointRequest;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.GpsTrackingService;

import java.util.List;

@RestController
@RequestMapping("/api/gps")
@RequiredArgsConstructor
@Tag(name = "GPS Module", description = "Endpoints for managing real-time GPS tracking sessions during walks or pet-sitting.")
public class GpsController {

    private final GpsTrackingService gpsTrackingService;

    @Operation(
            summary = "Start a new tracking session",
            description = "Initiates a GPS tracking session for a specific reservation. Returns the unique Session ID needed for subsequent point recordings."
    )
    @PostMapping("/start/{reservationId}")
    public ResponseEntity<Long> start(@PathVariable Long reservationId, @AuthenticationPrincipal AppUserDetails principal) {
        Long sessionId = gpsTrackingService.startSession(reservationId, principal).getSessionId();
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionId);

    }

    @Operation(
            summary = "Record a GPS coordinate point",
            description = "Saves a single latitude/longitude pair to an active tracking session. This endpoint should be called periodically by the mobile client."
    )
    @PostMapping("/point")
    public ResponseEntity<Void> addGpsPoint(@RequestBody GpsPointRequest req, @AuthenticationPrincipal AppUserDetails principal) {
        gpsTrackingService.recordPoint(req.sessionId(), req.latitude(), req.longitude(), principal);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Stop an active tracking session and set reservation status as complete",
            description = "Finalizes the tracking session. No more points can be recorded for this session ID after it is stopped."
    )
    @PostMapping("/stop/{sessionId}")
    public ResponseEntity<Void> stop(@PathVariable Long sessionId, @AuthenticationPrincipal AppUserDetails principal) {
        gpsTrackingService.stopSession(sessionId, principal);
        return ResponseEntity.noContent().build();

    }

    @Operation(
            summary = "Retrieve the full route for a reservation",
            description = "Fetches all GPS points recorded for a specific reservation, typically used to display the walk path on a map for the pet owner."
    )
    @GetMapping("/route/{reservationId}")
    public ResponseEntity<List<GpsPointDto>> route(@PathVariable Long reservationId, @AuthenticationPrincipal AppUserDetails principal) {
        List<GpsPointDto> route = gpsTrackingService.getRouteDto(reservationId, principal);
        return ResponseEntity.ok(route);
    }
}
