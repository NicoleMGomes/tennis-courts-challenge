package com.tenniscourts.guests;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = "guest-controller")
public interface GuestApi {

    @ApiOperation(value = "Creates a guest",
        notes = "Creates a guest with the informed name")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Guest created successfully")
    })
    ResponseEntity<Void> addGuest(
        @ApiParam(value = "DTO to create a guest.", required = true) final CreateGuestRequestDTO createGuestRequestDTO);

    @ApiOperation(value = "Finds a guest by id",
        notes = "Finds a guest by id")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Guest searched successfully")
    })
    ResponseEntity<GuestDTO> findGuestById(
        @ApiParam(value = "Guest id", required = true) final Long guestId);

    @ApiOperation(value = "Finds guests",
        notes = "Finds guests by name if informed, otherwise returns all guests")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Guest searched successfully")
    })
    ResponseEntity<List<GuestDTO>> findGuests(
        @ApiParam(value = "Guest name") final String name);

    @ApiOperation(value = "Removes a guest",
        notes = "Removes a guest by id")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Guest removed successfully")
    })
    ResponseEntity<GuestDTO> removeGuest(
        @ApiParam(value = "Guest id", required = true) final Long guestId);

    @ApiOperation(value = "Updates a guest",
        notes = "Updates a guest by id")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Guest updated successfully")
    })
    ResponseEntity<GuestDTO> updateGuest(
        @ApiParam(value = "Guest id", required = true) final Long guestId,
        @ApiParam(value = "Guest name", required = true) final String name);
}
