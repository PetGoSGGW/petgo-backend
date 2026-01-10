package pl.petgo.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.petgo.backend.domain.AvailabilitySlot;
import pl.petgo.backend.domain.Reservation;
import pl.petgo.backend.domain.ReservationStatus;
import pl.petgo.backend.repository.AvailabilitySlotRepository;
import pl.petgo.backend.repository.ReservationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private AvailabilitySlotRepository slotRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void confirmReservationSystem_ShouldConfirmPendingReservation() {
        // GIVEN
        Long resId = 1L;
        Reservation reservation = Reservation.builder()
                .reservationId(resId)
                .status(ReservationStatus.PENDING)
                .build();

        when(reservationRepository.findById(resId)).thenReturn(Optional.of(reservation));

        // WHEN
        reservationService.confirmReservationSystem(resId);

        // THEN
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void confirmReservationSystem_ShouldThrowException_WhenStatusIsNotPending() {
        // GIVEN
        Long resId = 1L;
        Reservation reservation = Reservation.builder()
                .reservationId(resId)
                .status(ReservationStatus.CANCELLED)
                .build();

        when(reservationRepository.findById(resId)).thenReturn(Optional.of(reservation));

        // WHEN & THEN
        assertThatThrownBy(() -> reservationService.confirmReservationSystem(resId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot confirm reservation");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void cancelReservationSystem_ShouldCancelReservation_AndReleaseSlots() {
        // GIVEN
        Long resId = 1L;

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setSlotId(100L);

        Reservation reservation = Reservation.builder()
                .reservationId(resId)
                .status(ReservationStatus.PENDING)
                .bookedSlots(new ArrayList<>(List.of(slot)))
                .build();

        slot.setReservation(reservation);

        when(reservationRepository.findById(resId)).thenReturn(Optional.of(reservation));
        when(slotRepository.findAllByReservation_ReservationId(resId)).thenReturn(List.of(slot));

        // WHEN
        reservationService.cancelReservationSystem(resId);

        // THEN
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);

        assertThat(reservation.getBookedSlots()).isNull();

        assertThat(slot.getReservation()).isNull();

        verify(slotRepository).saveAll(anyList());
        verify(reservationRepository).save(reservation);
    }
}