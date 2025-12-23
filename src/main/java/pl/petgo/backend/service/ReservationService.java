package pl.petgo.backend.service;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.petgo.backend.domain.*;
import pl.petgo.backend.dto.Reservation.ReservationCreateRequest;
import pl.petgo.backend.dto.Reservation.ReservationDto;
import pl.petgo.backend.repository.AvailabilitySlotRepository;
import pl.petgo.backend.repository.DogRepository;
import pl.petgo.backend.repository.OfferRepository;
import pl.petgo.backend.repository.ReservationRepository;
import pl.petgo.backend.security.AppUserDetails;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final AvailabilitySlotRepository slotRepository;
    private final OfferRepository offerRepository;
    private final DogRepository dogRepository;
    private final ReservationRepository reservationRepository;

    public ReservationDto createReservation(ReservationCreateRequest request, AppUserDetails userDetails) {
        User user = userDetails.getUser();
        Long userId = user.getUserId();

        Offer offer = offerRepository.findById(request.offerId())
                .orElseThrow(() -> new EntityNotFoundException("Offer with id: " + request.offerId() + " does not exist"));

        Dog dog = dogRepository.findById(request.dogId())
                .orElseThrow(() -> new EntityNotFoundException("Dog with id: " + request.dogId() + " does not exist"));

        if (!dog.getOwner().getUserId().equals(userId)) {
            throw new SecurityException("User with id: " + userId + " is not the owner of the dog with id: " + request.dogId());
        }

        List<AvailabilitySlot> slots = slotRepository.findAllById(request.availabilitySlotIds());

        if (slots.size() != request.availabilitySlotIds().size()) {
            throw new EntityNotFoundException("Not all availability slot IDs were found in the database");
        }

        boolean isAnyBooked = slots.stream().allMatch(x-> x.getReservation() != null);
        if (isAnyBooked) {
            throw new EntityNotFoundException("Some or all of the selected availability slots are already booked");
        }

        Instant scheduleStart = slots.stream().min(Comparator.comparing(AvailabilitySlot::getStartTime)).get().getStartTime();
        Instant scheduleEnd = slots.stream().max(Comparator.comparing(AvailabilitySlot::getEndTime)).get().getEndTime();

        Reservation reservation = Reservation.builder()
                .offer(offer)
                .dog(dog)
                .owner(user)
                .walker(offer.getWalker())
                .bookedSlots(slots)
                .status(ReservationStatus.PENDING)
                .scheduledStart(scheduleStart)
                .scheduledEnd(scheduleEnd)
                .build();

        return ReservationDto.fromEntity(reservationRepository.save(reservation));
    }

    public List<ReservationDto> getAllReservationsForUser(AppUserDetails userDetails) {
        User user = userDetails.getUser();

        List<ReservationDto> reservationDtos = reservationRepository.findAllByOwner_UserId(user.getUserId())
                .stream().map(ReservationDto::fromEntity).collect(Collectors.toList());

        return reservationDtos;
    }

    public List<ReservationDto> getAllReservationsForDog(AppUserDetails userDetails, Long dogId) {
        Long userId = userDetails.getUser().getUserId();

        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new EntityNotFoundException("Dog with id: " + dogId + " does not exist"));

        if (!dog.getOwner().getUserId().equals(userId)) {
            throw new SecurityException("User with id: " + userId + " is not the owner of the dog with id: " + dogId);
        }

        List<ReservationDto> reservationDtos = reservationRepository.findAllByDog_DogId(dogId)
                .stream().map(ReservationDto::fromEntity).collect(Collectors.toList());

        return reservationDtos;
    }

    public void confirmReservation(AppUserDetails userDetails, Long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id: " + reservationId + " does not exist"));

        boolean isWalker = reservation.getWalker().equals(userDetails.getUser());

        if(reservation.getStatus() == ReservationStatus.CONFIRMED){
            throw new IllegalArgumentException("Reservation with id: " + reservationId + " is already confirmed");
        }

        if(reservation.getStatus() != ReservationStatus.PENDING){
            throw new IllegalArgumentException("You cannot confirm reservation with status: " + reservation.getStatus());
        }

        if(!isWalker){
            throw new IllegalArgumentException("Reservation can be confirmed only by walker");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
    }

    public void cancelReservation(AppUserDetails userDetails, Long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id: " + reservationId + " does not exist"));

        boolean isWalker = reservation.getWalker().equals(userDetails.getUser());

        if(reservation.getStatus() == ReservationStatus.CANCELLED){
            throw new IllegalArgumentException("Reservation with id: " + reservationId + " is already cancelled");
        }

        if(reservation.getStatus() == ReservationStatus.COMPLETED){
            throw new IllegalArgumentException("Cannot cancel a reservation that is already completed");
        }

        if(reservation.getStatus() == ReservationStatus.CONFIRMED && !isWalker){
            throw new IllegalArgumentException("Cannot cancel a confirmed reservation");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setBookedSlots(null);

        List<AvailabilitySlot> bookedSlots = slotRepository.findAllByReservation_ReservationId(reservationId);
        bookedSlots.forEach(slot -> slot.setReservation(null));

        slotRepository.saveAll(bookedSlots);
        reservationRepository.save(reservation);
    }
}