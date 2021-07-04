package com.tenniscourts.reservations;

import org.springframework.http.ResponseEntity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = "reservation-controller")
public interface ReservationApi {

    @ApiOperation(value = "Creates a reservation",
        notes = "Creates a reservation in a schedule for a guest")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Reservation created successfully")
    })
    ResponseEntity<Void> bookReservation(
        @ApiParam(value = "DTO to create a reservation.", required = true) final CreateReservationRequestDTO createReservationRequestDTO);

    @ApiOperation(value = "Finds a reservation",
        notes = "Finds a reservation by id")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Reservation searched successfully")
    })
    ResponseEntity<ReservationDTO> findReservation(
        @ApiParam(value = "Reservation id", required = true) final Long reservationId);

    @ApiOperation(value = "Deletes a reservation",
        notes = "Deletes a reservation by id")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Reservation cancelled successfully")
    })
    ResponseEntity<ReservationDTO> cancelReservation(
        @ApiParam(value = "Reservation id", required = true) final Long reservationId);

    @ApiOperation(value = "Reschedules a reservation",
        notes = "Reschedules a reservation by reservation id and schedule id")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Reservation rescheduled successfully")
    })
    ResponseEntity<ReservationDTO> rescheduleReservation(
        @ApiParam(value = "Reservation id", required = true) final Long reservationId,
        @ApiParam(value = "Schedule id", required = true) final Long scheduleId);
}
