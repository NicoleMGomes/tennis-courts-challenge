package com.tenniscourts.schedules;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = "schedule-controller")
public interface ScheduleApi {

    @ApiOperation(value = "Creates a schedule",
        notes = "Creates a schedule to a tennis court")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Schedule created successfully")
    })
    ResponseEntity<Void> addScheduleTennisCourt(
        @ApiParam(value = "DTO to create a schedule", required = true) final CreateScheduleRequestDTO createScheduleRequestDTO);

    @ApiOperation(value = "Finds schedules by dates",
        notes = "Finds schedules by start date and end date")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Schedules searched successfully")
    })
    ResponseEntity<List<ScheduleDTO>> findSchedulesByDates(
        @ApiParam(value = "Start date", required = true) final LocalDate startDate,
        @ApiParam(value = "End date", required = true) final LocalDate endDate);

    @ApiOperation(value = "Finds a schedule by id",
        notes = "Finds a schedule by id")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Schedule searched successfully")
    })
    ResponseEntity<ScheduleDTO> findByScheduleId(
        @ApiParam(value = "Schedule id", required = true) final Long scheduleId);

    @ApiOperation(value = "Finds available schedules",
        notes = "Finds available schedules")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Schedules searched successfully")
    })
    ResponseEntity<List<ScheduleDTO>> findAvailableSchedules();
}
