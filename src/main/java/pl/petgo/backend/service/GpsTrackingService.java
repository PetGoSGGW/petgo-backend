package pl.petgo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.petgo.backend.domain.GpsPoint;
import pl.petgo.backend.domain.GpsSession;
import pl.petgo.backend.domain.Reservation;
import pl.petgo.backend.domain.ReservationStatus;
import pl.petgo.backend.dto.GpsPointDto;
import pl.petgo.backend.exception.NotFoundException;
import pl.petgo.backend.repository.GpsPointRepository;
import pl.petgo.backend.repository.GpsSessionRepository;
import pl.petgo.backend.repository.ReservationRepository;
import pl.petgo.backend.security.AppUserDetails;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GpsTrackingService {

    private final GpsSessionRepository gpsSessionRepository;
    private final GpsPointRepository gpsPointRepository;
    private final ReservationRepository reservationRepository;

    public GpsSession startSession(Long reservationId, AppUserDetails principal) {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        Long currentUserId = principal.getUser().getUserId();

        boolean isWalker = reservation.getWalker().getUserId().equals(currentUserId);

        if (!isWalker) {
            throw new AccessDeniedException("Only the assigned walker can start a GPS session.");
        }

        gpsSessionRepository.findByReservation_ReservationId(reservationId)
                .ifPresent(s -> {
                    throw new IllegalStateException("GPS session for this reservation already exists.");
                });

        GpsSession session = GpsSession.builder()
                .reservation(reservation)
                .startedAt(Instant.now())
                .build();

        return gpsSessionRepository.save(session);
    }

    public void recordPoint(Long sessionId, Double latitude, Double longitude, AppUserDetails principal) {
        if (sessionId == null || latitude == null || longitude == null) {
            throw new IllegalArgumentException("Session ID and coordinates must not be null");
        }

        GpsSession session = gpsSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("GPS session not found"));

        Long currentUserId = principal.getUser().getUserId();
        Long walkerId = session.getReservation().getWalker().getUserId();

        if (!currentUserId.equals(walkerId)) {
            throw new AccessDeniedException("You are not authorized to record points for this session.");
        }

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

    public void stopSession(Long sessionId, AppUserDetails principal) {
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }

        GpsSession session = gpsSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("GPS session not found"));

        Long currentUserId = principal.getUser().getUserId();
        Long walkerId = session.getReservation().getWalker().getUserId();

        if (!currentUserId.equals(walkerId)) {
            throw new AccessDeniedException("Only the assigned walker can stop this session.");
        }

        if (session.getStoppedAt() != null) {
            throw new IllegalStateException("Session is already stopped.");
        }

        session.setStoppedAt(Instant.now());
        gpsSessionRepository.save(session);

        Reservation reservation = session.getReservation();
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
    }

    public List<GpsPointDto> getRouteDto(Long reservationId, AppUserDetails principal) {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }

        GpsSession session = gpsSessionRepository.findByReservation_ReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException("GPS session not found for reservation " + reservationId));

        Long currentUserId = principal.getUser().getUserId();
        boolean isOwner = session.getReservation().getOwner().getUserId().equals(currentUserId);
        boolean isWalker = session.getReservation().getWalker().getUserId().equals(currentUserId);

        if (!isOwner && !isWalker) {
            throw new AccessDeniedException("You are not authorized to view the route for this reservation.");
        }

        return gpsPointRepository.findBySession_SessionIdOrderByRecordedAtAsc(session.getSessionId()).stream()
                .map(GpsTrackingService::convertToDto)
                .toList();
    }

    private static GpsPointDto convertToDto(GpsPoint p) {
        return new GpsPointDto(p.getLatitude(), p.getLongitude(), p.getRecordedAt());
    }

    public double getDistanceForReservation(Long reservationId, AppUserDetails principal) {
        List<GpsPointDto> route = getRouteDto(reservationId, principal);
        return calculateDistanceMeters(route);
    }

    private double calculateDistanceMeters(List<GpsPointDto> points) {
        if (points == null || points.size() < 2) {
            return 0.0;
        }

        final double EARTH_RADIUS_METERS = 6_371_000;
        double totalDistance = 0.0;

        for (int i = 1; i < points.size(); i++) {
            GpsPointDto p1 = points.get(i - 1);
            GpsPointDto p2 = points.get(i);

            double lat1 = Math.toRadians(p1.latitude());
            double lon1 = Math.toRadians(p1.longitude());
            double lat2 = Math.toRadians(p2.latitude());
            double lon2 = Math.toRadians(p2.longitude());

            double dLat = lat2 - lat1;
            double dLon = lon2 - lon1;

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(lat1) * Math.cos(lat2)
                    * Math.sin(dLon / 2) * Math.sin(dLon / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            totalDistance += EARTH_RADIUS_METERS * c;
        }

        return totalDistance;
    }
}
