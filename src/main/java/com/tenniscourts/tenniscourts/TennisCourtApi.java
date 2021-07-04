package com.tenniscourts.tenniscourts;

import org.springframework.http.ResponseEntity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = "tennis-court-controller")
public interface TennisCourtApi {

    @ApiOperation(value = "Creates a tennis court",
        notes = "Creates a tennis court with schedules")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Tennis court created successfully")
    })
    ResponseEntity<Void> addTennisCourt(
        @ApiParam(value = "DTO to create a tennis court", required = true) final CreateTennisCourtRequestDTO createTennisCourtRequestDTO);

    @ApiOperation(value = "Finds a tennis court by id",
        notes = "Finds a tennis court by id")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Tennis court searched successfully")
    })
    ResponseEntity<TennisCourtDTO> findTennisCourtById(
        @ApiParam(value = "Tennis court id", required = true) final Long tennisCourtId);

    @ApiOperation(value = "Finds a tennis court by id with schedules",
        notes = "Finds a tennis court by id with schedules")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Tennis court and schedules searched successfully")
    })
    ResponseEntity<TennisCourtDTO> findTennisCourtWithSchedulesById(
        @ApiParam(value = "Tennis court id", required = true) final Long tennisCourtId);
}
