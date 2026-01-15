package pl.petgo.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.petgo.backend.domain.GpsPoint;
import pl.petgo.backend.domain.GpsSession;
import pl.petgo.backend.domain.Reservation;
import pl.petgo.backend.domain.User;
import pl.petgo.backend.dto.GpsPointDto;
import pl.petgo.backend.exception.NotFoundException;
import pl.petgo.backend.repository.GpsPointRepository;
import pl.petgo.backend.repository.GpsSessionRepository;
import pl.petgo.backend.repository.ReservationRepository;
import pl.petgo.backend.security.AppUserDetails;

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
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.startSession(null, null));
    }

    @Test
    void startSession_whenReservationNotFound_thenThrowsNotFoundException() {
        Long reservationId = 42L;
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gpsTrackingService.startSession(reservationId, null));
    }

    @Test
    void startSession_whenReservationExists_thenReturnsSavedSession() {
        Long reservationId = 42L;

        User user = User.builder().userId(1L).build();

        Reservation reservation = Reservation.builder()
                .reservationId(reservationId)
                .walker(user)
                .build();

        GpsSession expectedSession = GpsSession.builder()
                .reservation(reservation)
                .startedAt(Instant.now())
                .build();

        AppUserDetails principal = new AppUserDetails(user);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(gpsSessionRepository.save(any(GpsSession.class))).thenReturn(expectedSession);

        GpsSession result = gpsTrackingService.startSession(reservationId, principal);

        assertNotNull(result);
        assertEquals(reservationId, result.getReservation().getReservationId());
    }

    @Test
    void recordPoint_whenAnyArgumentIsNull_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.recordPoint(null, 51.0, 21.0, null));
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.recordPoint(1L, null, 21.0, null));
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.recordPoint(1L, 51.0, null, null));
    }

    @Test
    void recordPoint_whenSessionNotFound_thenThrowsNotFoundException() {
        Long sessionId = 1L;
        when(gpsSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gpsTrackingService.recordPoint(sessionId, 51.0, 21.0, null));
    }

    @Test
    void recordPoint_whenSessionIsStopped_thenThrowsIllegalStateException() {
        Long sessionId = 1L;

        User user = User.builder().userId(1L).build();

        Reservation reservation = Reservation.builder()
                .walker(user)
                .build();

        GpsSession session = GpsSession.builder()
                .reservation(reservation)
                .stoppedAt(Instant.now())
                .build();

        AppUserDetails principal = new AppUserDetails(user);

        when(gpsSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(IllegalStateException.class, () -> gpsTrackingService.recordPoint(sessionId, 51.0, 21.0, principal));
    }

    @Test
    void recordPoint_whenSessionIsActive_thenSavesGpsPoint() {
        Long sessionId = 1L;

        Long reservationId = 42L;

        User user = User.builder().userId(1L).build();

        Reservation reservation = Reservation.builder()
                .reservationId(reservationId)
                .walker(user)
                .build();

        GpsSession session = GpsSession.builder()
                .sessionId(sessionId)
                .reservation(reservation)
                .build();

        AppUserDetails principal = new AppUserDetails(user);

        when(gpsSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        gpsTrackingService.recordPoint(sessionId, 51.0, 21.0, principal);

        verify(gpsPointRepository).save(argThat(point ->
                point.getSession().equals(session) &&
                        point.getLatitude().equals(51.0) &&
                        point.getLongitude().equals(21.0) &&
                        point.getRecordedAt() != null
        ));
    }

    @Test
    void stopSession_whenSessionIdIsNull_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.stopSession(null, null));
    }

    @Test
    void stopSession_whenSessionNotFound_thenThrowsNotFoundException() {
        Long sessionId = 1L;
        when(gpsSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gpsTrackingService.stopSession(sessionId, null));
    }

    @Test
    void stopSession_whenSessionExists_thenSetsStoppedAtAndSaves() {
        Long sessionId = 1L;

        Long reservationId = 42L;

        User user = User.builder().userId(1L).build();

        Reservation reservation = Reservation.builder()
                .reservationId(reservationId)
                .walker(user)
                .build();

        GpsSession session = GpsSession.builder()
                .sessionId(sessionId)
                .reservation(reservation)
                .build();

        AppUserDetails principal = new AppUserDetails(user);

        when(gpsSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        gpsTrackingService.stopSession(sessionId, principal);

        assertNotNull(session.getStoppedAt());
        verify(gpsSessionRepository).save(session);
    }


    @Test
    void getRouteDto_whenReservationIdIsNull_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> gpsTrackingService.getRouteDto(null, null));
    }

    @Test
    void getRouteDto_whenSessionNotFound_thenThrowsNotFoundException() {
        Long reservationId = 43L;

        when(gpsSessionRepository.findByReservation_ReservationId(reservationId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gpsTrackingService.getRouteDto(reservationId, null));
    }


    @Test
    void getRouteDto_whenSessionExists_thenReturnsMappedPoints() {
        Long sessionId = 1L;

        Long reservationId = 42L;

        User walker = User.builder().userId(1L).build();
        User owner = User.builder().userId(2L).build();

        Reservation reservation = Reservation.builder()
                .reservationId(reservationId)
                .walker(walker)
                .owner(owner)
                .build();

        GpsSession session = GpsSession.builder()
                .sessionId(sessionId)
                .reservation(reservation)
                .build();

        AppUserDetails principal = new AppUserDetails(owner);

        when(gpsSessionRepository.findByReservation_ReservationId(reservationId)).thenReturn(Optional.of(session));
        when(gpsPointRepository.findBySession_SessionIdOrderByRecordedAtAsc(sessionId)).thenReturn(List.of(
                new GpsPoint(null, session, 51.0, 21.0, Instant.now())
        ));

        List<GpsPointDto> result = gpsTrackingService.getRouteDto(reservationId, principal);

        assertEquals(1, result.size());
    }
}
