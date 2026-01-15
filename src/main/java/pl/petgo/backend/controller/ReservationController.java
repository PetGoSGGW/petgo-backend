package pl.petgo.backend.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.petgo.backend.dto.reservation.ReservationCreateRequest;
import pl.petgo.backend.dto.reservation.ReservationDto;
import pl.petgo.backend.security.AppUserDetails;
import pl.petgo.backend.service.ReservationService;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Reservation Module", description = "Endpoints for managing reservations.")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
            summary = "Create new reservation for specific offer"
    )
    @PostMapping("/reservations")
    public ResponseEntity<ReservationDto> createReservation(
            @RequestBody @Valid ReservationCreateRequest request,
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        ReservationDto result = reservationService.createReservation(request, userDetails);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Get all reservation for user (owner)"
    )
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationDto>> getAllReservationForUser(
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        List<ReservationDto> result = reservationService.getAllReservationsForUser(userDetails);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Get all reservation for walker"
    )
    @GetMapping("/reservations/walker")
    public ResponseEntity<List<ReservationDto>> getAllReservationForWalker(
            @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        List<ReservationDto> result = reservationService.getAllReservationsForWalker(userDetails);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Get all reservation for dog"
    )
    @GetMapping("/dog/{dogId}/reservations")
    public ResponseEntity<List<ReservationDto>> getAllReservationForDog(
            @AuthenticationPrincipal AppUserDetails userDetails,
            @PathVariable Long dogId
    ) {
        List<ReservationDto> result = reservationService.getAllReservationsForDog(userDetails, dogId);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Confirm reservation"
    )
    @PostMapping("/reservations/{reservationId}/confirm")
    public ResponseEntity<Void> confirmReservation(
            @AuthenticationPrincipal AppUserDetails userDetails,
            @PathVariable Long reservationId
    ) {
        reservationService.confirmReservation(userDetails, reservationId);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Cancel reservation"
    )
    @PostMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @AuthenticationPrincipal AppUserDetails userDetails,
            @PathVariable Long reservationId
    ) {
        reservationService.cancelReservation(userDetails, reservationId);

        return ResponseEntity.ok().build();
    }
}
