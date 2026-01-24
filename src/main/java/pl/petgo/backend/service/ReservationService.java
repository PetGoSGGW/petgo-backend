package pl.petgo.backend.service;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.petgo.backend.domain.*;
import pl.petgo.backend.dto.GpsPointDto;
import pl.petgo.backend.dto.reservation.GpsRouteDto;
import pl.petgo.backend.dto.reservation.ReservationCreateRequest;
import pl.petgo.backend.dto.reservation.ReservationDetailsDto;
import pl.petgo.backend.dto.reservation.ReservationDto;
import pl.petgo.backend.exception.NotFoundException;
import pl.petgo.backend.repository.AvailabilitySlotRepository;
import pl.petgo.backend.repository.DogRepository;
import pl.petgo.backend.repository.OfferRepository;
import pl.petgo.backend.repository.ReservationRepository;
import pl.petgo.backend.security.AppUserDetails;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final AvailabilitySlotRepository slotRepository;
    private final OfferRepository offerRepository;
    private final DogRepository dogRepository;
    private final ReservationRepository reservationRepository;
    private final GpsTrackingService gpsTrackingService;

    public ReservationDto createReservation(ReservationCreateRequest request, AppUserDetails userDetails) {
        User user = userDetails.getUser();
        Long userId = user.getUserId();

        Offer offer = offerRepository.findById(request.offerId())
                .orElseThrow(() -> new EntityNotFoundException("Offer not found"));

        Dog dog = dogRepository.findById(request.dogId())
                .orElseThrow(() -> new EntityNotFoundException("Dog not found"));

        if (!dog.getOwner().getUserId().equals(userId)) {
            throw new AccessDeniedException("User is not the owner of this dog");
        }

        List<AvailabilitySlot> slots = slotRepository.findAllById(request.availabilitySlotIds());

        if (slots.size() != request.availabilitySlotIds().size()) {
            throw new EntityNotFoundException("Some availability slots were not found");
        }

        if (slots.stream().anyMatch(x -> x.getReservation() != null)) {
            throw new IllegalStateException("Some slots are already booked");
        }

        Instant scheduleStart = slots.stream()
                .min(Comparator.comparing(AvailabilitySlot::getStartTime))
                .map(AvailabilitySlot::getStartTime)
                .orElseThrow(() -> new IllegalStateException("Start time error"));

        Instant scheduleEnd = slots.stream()
                .max(Comparator.comparing(AvailabilitySlot::getEndTime))
                .map(AvailabilitySlot::getEndTime)
                .orElseThrow(() -> new IllegalStateException("End time error"));

        boolean alreadyExists = reservationRepository.existsByOfferAndDogAndOwnerAndWalkerAndScheduledStartAndScheduledEndAndStatusNotIn(
                offer,
                dog,
                user,
                offer.getWalker(),
                scheduleStart,
                scheduleEnd,
                List.of(ReservationStatus.CANCELLED)
        );

        if (alreadyExists) {
            throw new IllegalStateException("This exact reservation already exists and is active.");
        }

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

        return reservationRepository.findAllByOwner_UserId(user.getUserId())
                .stream()
                .map(ReservationDto::fromEntity)
                .toList();
    }

    public List<ReservationDto> getAllReservationsForWalker(AppUserDetails userDetails) {
        User user = userDetails.getUser();

        return reservationRepository.findAllByWalker_UserId(user.getUserId())
                .stream()
                .map(ReservationDto::fromEntity)
                .toList();
    }

    public List<ReservationDto> getAllReservationsForDog(AppUserDetails userDetails, Long dogId) {
        Long userId = userDetails.getUser().getUserId();

        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new EntityNotFoundException("Dog with id: " + dogId + " does not exist"));

        if (!dog.getOwner().getUserId().equals(userId)) {
            throw new AccessDeniedException("User with id: " + userId + " is not the owner of the dog with id: " + dogId);
        }

        return reservationRepository.findAllByDog_DogId(dogId)
                .stream()
                .map(ReservationDto::fromEntity)
                .toList();
    }

    public void confirmReservation(AppUserDetails userDetails, Long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id: " + reservationId + " does not exist"));

        boolean isWalker = reservation.getWalker().getUserId().equals(userDetails.getUser().getUserId());

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

        boolean isWalker = reservation.getWalker().getUserId().equals(userDetails.getUser().getUserId());

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

        reservationRepository.save(reservation);
    }

    public void confirmReservationSystem(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id: " + reservationId + " does not exist"));

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            log.info("Reservation {} is already confirmed. Skipping.", reservationId);
            return;
        }

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Cannot confirm reservation via payment system because status is: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
        log.info("Reservation {} confirmed by system after successful payment.", reservationId);
    }

    public void cancelReservationSystem(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id: " + reservationId + " does not exist"));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return;
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot system-cancel a completed reservation");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        reservation.setBookedSlots(null);
        List<AvailabilitySlot> bookedSlots = slotRepository.findAllByReservation_ReservationId(reservationId);
        bookedSlots.forEach(slot -> slot.setReservation(null));
        slotRepository.saveAll(bookedSlots);

        reservationRepository.save(reservation);
        log.info("Reservation {} cancelled by system (payment failed/expired).", reservationId);
    }

    public ReservationDetailsDto getReservationDetails(
            AppUserDetails userDetails,
            Long reservationId
    ) {
        User user = userDetails.getUser();
        Long userId = user.getUserId();

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Reservation with id: " + reservationId + " does not exist"
                        )
                );

        boolean isOwner = reservation.getOwner().getUserId().equals(userId);
        boolean isWalker = reservation.getWalker().getUserId().equals(userId);
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isWalker && !isAdmin) {
            throw new AccessDeniedException(
                    "User with id: " + userId + " has no access to reservation with id: " + reservationId
            );
        }

        OffsetDateTime start = reservation.getScheduledStart().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = reservation.getScheduledEnd().atOffset(ZoneOffset.UTC);

        GpsRouteDto gpsRoute = null;

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            try {
                List<GpsPointDto> route =
                        gpsTrackingService.getRouteDto(reservationId, userDetails);

                double distance =
                        gpsTrackingService.getDistanceForReservation(reservationId, userDetails);

                gpsRoute = new GpsRouteDto(
                        true,
                        distance,
                        route
                );
            } catch (NotFoundException e) {
                gpsRoute = new GpsRouteDto(
                        false,
                        0.0,
                        List.of()
                );
            }
        }

        return new ReservationDetailsDto(
                reservation.getReservationId(),
                reservation.getStatus(),
                reservation.getDog().getName(),
                start,
                end,
                Duration.between(start, end).toMinutes(),
                gpsRoute
        );
    }
}
