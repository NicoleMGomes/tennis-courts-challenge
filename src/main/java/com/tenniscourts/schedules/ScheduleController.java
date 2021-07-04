package com.tenniscourts.schedules;

import static org.springframework.http.HttpStatus.CREATED;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/schedule")
public class ScheduleController extends BaseRestController implements ScheduleApi {

    private final ScheduleService scheduleService;

    @Override
    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Void> addScheduleTennisCourt(
        @RequestBody final CreateScheduleRequestDTO createScheduleRequestDTO) {

        return ResponseEntity.created(locationByEntity(scheduleService.addSchedule(createScheduleRequestDTO).getId()))
            .build();
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> findSchedulesByDates(
        @RequestParam @DateTimeFormat(iso = ISO.DATE) final LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) final LocalDate endDate) {

        return ResponseEntity.ok(scheduleService.findSchedulesByDates(startDate, endDate));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> findByScheduleId(
        @PathVariable("id") final Long scheduleId) {

        return ResponseEntity.ok(scheduleService.findSchedule(scheduleId));
    }

    @Override
    @GetMapping("/available")
    public ResponseEntity<List<ScheduleDTO>> findAvailableSchedules() {

        return ResponseEntity.ok(scheduleService.findAvailableSchedules());
    }
}
