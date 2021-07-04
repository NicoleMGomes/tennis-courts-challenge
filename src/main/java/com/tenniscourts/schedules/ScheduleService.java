package com.tenniscourts.schedules;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.of;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.tenniscourts.exceptions.EntityNotFoundException;

import lombok.AllArgsConstructor;

@Lazy
@Service
@AllArgsConstructor
public class ScheduleService {

    private static final Integer PLAY_TIME = 1;

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    public ScheduleDTO addSchedule(final CreateScheduleRequestDTO createScheduleRequestDTO) {

        final LocalDateTime startDate = createScheduleRequestDTO.getStartDateTime();

        if (startDate.isBefore(now())) {
            throw new IllegalArgumentException("Cannot create because startDateTime is before actual date.");
        }

        final LocalDateTime endDate = createScheduleRequestDTO.getStartDateTime().plusHours(PLAY_TIME);
        this.findSchedulesByTennisCourtId(createScheduleRequestDTO.getTennisCourtId())
            .forEach(s -> validateSchedule(s, startDate, endDate));

        return scheduleMapper.map(scheduleRepository.saveAndFlush(scheduleMapper.map(createScheduleRequestDTO)));
    }

    private void validateSchedule(final ScheduleDTO scheduleDTO, final LocalDateTime startDate,
        final LocalDateTime endDate) {

        final boolean isStartDateInvalid = startDate.isEqual(scheduleDTO.getStartDateTime())
            || (startDate.isAfter(scheduleDTO.getStartDateTime()) && startDate.isBefore(scheduleDTO.getEndDateTime()));

        final boolean isEndDateInvalid =
            endDate.isAfter(scheduleDTO.getStartDateTime()) && endDate.isBefore(scheduleDTO.getEndDateTime());

        if (isStartDateInvalid || isEndDateInvalid) {
            throw new IllegalArgumentException("Cannot create because this schedule already exists.");
        }
    }

    public List<ScheduleDTO> findSchedulesByDates(final LocalDate startDate, final LocalDate endDate) {

        final LocalDateTime startDateTime = of(startDate, LocalTime.of(0, 0));
        final LocalDateTime endDateTime = of(endDate, LocalTime.of(23, 59));

        return scheduleMapper.map(scheduleRepository
            .findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(startDateTime, endDateTime));
    }

    public ScheduleDTO findSchedule(final Long scheduleId) {

        return scheduleRepository.findById(scheduleId).map(scheduleMapper::map).orElseThrow(() ->
            new EntityNotFoundException("Schedule not found.")
        );
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(final Long tennisCourtId) {

        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }

    public List<ScheduleDTO> findAvailableSchedules() {

        return scheduleMapper.map(scheduleRepository.findAvailableSchedules());
    }
}
