package com.tenniscourts.reservations;

import static org.springframework.http.HttpStatus.CREATED;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tenniscourts.config.BaseRestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/reservation")
public class ReservationController extends BaseRestController implements ReservationApi {

    private final ReservationService reservationService;

    @Override
    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Void> bookReservation(
        @RequestBody final CreateReservationRequestDTO createReservationRequestDTO) {

        return ResponseEntity
            .created(locationByEntity(reservationService.bookReservation(createReservationRequestDTO).getId())).build();
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> findReservation(
        @PathVariable("id") final Long reservationId) {

        return ResponseEntity.ok(reservationService.findReservation(reservationId));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<ReservationDTO> cancelReservation(
        @PathVariable("id") final Long reservationId) {

        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<ReservationDTO> rescheduleReservation(
        @PathVariable("id") final Long reservationId,
        @RequestParam final Long scheduleId) {

        return ResponseEntity.ok(reservationService.rescheduleReservation(reservationId, scheduleId));
    }
}
