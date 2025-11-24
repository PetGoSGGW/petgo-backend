package pl.petgo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.petgo.backend.domain.GpsPoint;
import pl.petgo.backend.domain.GpsSession;
import pl.petgo.backend.domain.Reservation;
import pl.petgo.backend.dto.GpsPointDto;
import pl.petgo.backend.exception.NotFoundException;
import pl.petgo.backend.repository.GpsPointRepository;
import pl.petgo.backend.repository.GpsSessionRepository;
import pl.petgo.backend.repository.ReservationRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GpsTrackingService {

    private final GpsSessionRepository gpsSessionRepository;
    private final GpsPointRepository gpsPointRepository;
    private final ReservationRepository reservationRepository;

    public GpsSession startSession(Long reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        GpsSession session = GpsSession.builder()
                .reservation(reservation)
                .startedAt(Instant.now())
                .build();

        return gpsSessionRepository.save(session);
    }

    public void recordPoint(Long sessionId, Double latitude, Double longitude) {
        if (sessionId == null || latitude == null || longitude == null) {
            throw new IllegalArgumentException("Session ID and coordinates must not be null");
        }

        GpsSession session = gpsSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("GPS session not found"));

        if (session.getStoppedAt() != null) {
            throw new IllegalStateException("Cannot record point: session has already been stopped");
        }

        GpsPoint point = GpsPoint.builder()
                .session(session)
                .latitude(latitude)
                .longitude(longitude)
                .recordedAt(Instant.now())
                .build();

        gpsPointRepository.save(point);
    }

    public void stopSession(Long sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }

        GpsSession session = gpsSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("GPS session not found"));

        session.setStoppedAt(Instant.now());
        gpsSessionRepository.save(session);
    }

    public List<GpsPointDto> getRouteDto(Long reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }


        GpsSession session = gpsSessionRepository.findByReservation_ReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException("GPS session not found for reservation " + reservationId));

        return gpsPointRepository.findBySession_SessionIdOrderByRecordedAtAsc(session.getSessionId()).stream()
                .map(GpsTrackingService::convertToDto)
                .toList();
    }

    private static GpsPointDto convertToDto(GpsPoint p) {
        return new GpsPointDto(p.getLatitude(), p.getLongitude(), p.getRecordedAt());
    }
}
