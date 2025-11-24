package pl.petgo.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GpsTrackingServiceTest {

    @Mock
    private GpsSessionRepository gpsSessionRepository;

    @Mock
    private GpsPointRepository gpsPointRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private GpsTrackingService gpsTrackingService;

    @Test
    void startSession_whenReservationIdIsNull_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.startSession(null));
    }

    @Test
    void startSession_whenReservationNotFound_thenThrowsNotFoundException() {
        Long reservationId = 42L;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gpsTrackingService.startSession(reservationId));
    }

    @Test
    void startSession_whenReservationExists_thenReturnsSavedSession() {
        Long reservationId = 42L;
        Reservation reservation = Reservation.builder()
                .reservationId(reservationId)
                .build();

        GpsSession expectedSession = GpsSession.builder()
                .reservation(reservation)
                .startedAt(Instant.now())
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(gpsSessionRepository.save(any(GpsSession.class))).thenReturn(expectedSession);

        GpsSession result = gpsTrackingService.startSession(reservationId);

        assertNotNull(result);
        assertEquals(reservationId, result.getReservation().getReservationId());
    }

    @Test
    void recordPoint_whenAnyArgumentIsNull_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.recordPoint(null, 51.0, 21.0));
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.recordPoint(1L, null, 21.0));
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.recordPoint(1L, 51.0, null));
    }

    @Test
    void recordPoint_whenSessionNotFound_thenThrowsNotFoundException() {
        Long sessionId = 1L;
        when(gpsSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gpsTrackingService.recordPoint(sessionId, 51.0, 21.0));
    }

    @Test
    void recordPoint_whenSessionIsStopped_thenThrowsIllegalStateException() {
        Long sessionId = 1L;
        GpsSession session = GpsSession.builder()
                .sessionId(sessionId)
                .stoppedAt(Instant.now())
                .build();

        when(gpsSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(IllegalStateException.class, () -> gpsTrackingService.recordPoint(sessionId, 51.0, 21.0));
    }

    @Test
    void recordPoint_whenSessionIsActive_thenSavesGpsPoint() {
        Long sessionId = 1L;
        GpsSession session = GpsSession.builder()
                .sessionId(sessionId)
                .build();

        when(gpsSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        gpsTrackingService.recordPoint(sessionId, 51.0, 21.0);

        verify(gpsPointRepository).save(argThat(point ->
                point.getSession().equals(session) &&
                        point.getLatitude().equals(51.0) &&
                        point.getLongitude().equals(21.0) &&
                        point.getRecordedAt() != null
        ));
    }

    @Test
    void stopSession_whenSessionIdIsNull_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.stopSession(null));
    }

    @Test
    void stopSession_whenSessionNotFound_thenThrowsNotFoundException() {
        Long sessionId = 1L;
        when(gpsSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gpsTrackingService.stopSession(sessionId));
    }

    @Test
    void stopSession_whenSessionExists_thenSetsStoppedAtAndSaves() {
        Long sessionId = 1L;
        GpsSession session = GpsSession.builder()
                .sessionId(sessionId)
                .build();

        when(gpsSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        gpsTrackingService.stopSession(sessionId);

        assertNotNull(session.getStoppedAt());
        verify(gpsSessionRepository).save(session);
    }


    @Test
    void getRouteDto_whenReservationIdIsNull_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.getRouteDto(null));
    }

    @Test
    void getRouteDto_whenSessionNotFound_thenThrowsNotFoundException() {
        Long reservationId = 43L;

        when(gpsSessionRepository.findByReservation_ReservationId(reservationId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gpsTrackingService.getRouteDto(reservationId));
    }


    @Test
    void getRouteDto_whenSessionExists_thenReturnsMappedPoints() {
        Long reservationId = 42L;
        Long sessionId = 1L;
        GpsSession session = GpsSession.builder().sessionId(sessionId).build();

        when(gpsSessionRepository.findByReservation_ReservationId(reservationId)).thenReturn(Optional.of(session));
        when(gpsPointRepository.findBySession_SessionIdOrderByRecordedAtAsc(sessionId)).thenReturn(List.of(
                new GpsPoint(null, session, 51.0, 21.0, Instant.now())
        ));

        List<GpsPointDto> result = gpsTrackingService.getRouteDto(reservationId);

        assertEquals(1, result.size());
    }
}
