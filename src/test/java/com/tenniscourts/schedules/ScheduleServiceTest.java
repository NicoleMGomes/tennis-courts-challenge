package com.tenniscourts.schedules;

import static com.tenniscourts.commons.Fixture.make;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.of;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.tenniscourts.exceptions.EntityNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceTest {

    private static final Integer PLAY_TIME = 1;
    private static final Integer HALF_HOUR_IN_MINUTES = 30;

    @InjectMocks
    private ScheduleService service;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ScheduleMapper scheduleMapper;

    private Long id;
    private List<Schedule> schedules;
    private List<ScheduleDTO> schedulesDTO;
    private CreateScheduleRequestDTO createScheduleRequestDTO;

    @Before
    public void before() {
        id = nextLong();
        schedules = range(0, nextInt(2, 5))
            .mapToObj(i -> make(new Schedule()))
            .collect(Collectors.toList());
        final ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setStartDateTime(now().plusHours(nextInt(3, 4)));
        scheduleDTO.setEndDateTime(scheduleDTO.getStartDateTime().plusHours(PLAY_TIME));
        schedulesDTO = singletonList(scheduleDTO);
        createScheduleRequestDTO = new CreateScheduleRequestDTO();
        createScheduleRequestDTO.setTennisCourtId(id);
    }

    @Test
    public void addSchedule_scheduleBefore() {

        createScheduleRequestDTO.setStartDateTime(schedulesDTO.get(0).getStartDateTime().minusHours(PLAY_TIME));

        when(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(any())).thenReturn(schedules);
        when(scheduleMapper.map(any(List.class))).thenReturn(schedulesDTO);
        when(scheduleRepository.saveAndFlush(any())).thenReturn(schedules.get(0));
        when(scheduleMapper.map(any(CreateScheduleRequestDTO.class))).thenReturn(schedules.get(0));
        when(scheduleMapper.map(any(Schedule.class))).thenReturn(schedulesDTO.get(0));

        final ScheduleDTO actual = service.addSchedule(createScheduleRequestDTO);

        verify(scheduleRepository).findByTennisCourt_IdOrderByStartDateTime(id);
        verify(scheduleMapper).map(schedules);
        verify(scheduleRepository).saveAndFlush(any());
        verify(scheduleMapper).map(any(CreateScheduleRequestDTO.class));
        verify(scheduleMapper).map(any(Schedule.class));

        assertEquals(schedulesDTO.get(0), actual);
    }

    @Test
    public void addSchedule_scheduleAfter() {

        createScheduleRequestDTO.setStartDateTime(schedulesDTO.get(0).getEndDateTime());

        when(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(any())).thenReturn(schedules);
        when(scheduleMapper.map(any(List.class))).thenReturn(schedulesDTO);
        when(scheduleRepository.saveAndFlush(any())).thenReturn(schedules.get(0));
        when(scheduleMapper.map(any(CreateScheduleRequestDTO.class))).thenReturn(schedules.get(0));
        when(scheduleMapper.map(any(Schedule.class))).thenReturn(schedulesDTO.get(0));

        final ScheduleDTO actual = service.addSchedule(createScheduleRequestDTO);

        verify(scheduleRepository).findByTennisCourt_IdOrderByStartDateTime(id);
        verify(scheduleMapper).map(schedules);
        verify(scheduleRepository).saveAndFlush(any());
        verify(scheduleMapper).map(any(CreateScheduleRequestDTO.class));
        verify(scheduleMapper).map(any(Schedule.class));

        assertEquals(schedulesDTO.get(0), actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSchedule_startDateBeforeActualDate() {

        createScheduleRequestDTO.setStartDateTime(now().minusMinutes(HALF_HOUR_IN_MINUTES));

        try {
            service.addSchedule(createScheduleRequestDTO);
        } finally {
            verifyNoInteractions(scheduleRepository, scheduleMapper);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSchedule_scheduleEqual() {

        createScheduleRequestDTO.setStartDateTime(schedulesDTO.get(0).getStartDateTime());

        when(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(any())).thenReturn(schedules);
        when(scheduleMapper.map(any(List.class))).thenReturn(schedulesDTO);

        try {
            service.addSchedule(createScheduleRequestDTO);
        } finally {
            verify(scheduleRepository).findByTennisCourt_IdOrderByStartDateTime(id);
            verify(scheduleMapper).map(schedules);
            verifyNoMoreInteractions(scheduleRepository, scheduleMapper);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSchedule_startDateInvalid() {

        createScheduleRequestDTO
            .setStartDateTime(schedulesDTO.get(0).getStartDateTime().plusMinutes(HALF_HOUR_IN_MINUTES));

        when(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(any())).thenReturn(schedules);
        when(scheduleMapper.map(any(List.class))).thenReturn(schedulesDTO);

        try {
            service.addSchedule(createScheduleRequestDTO);
        } finally {
            verify(scheduleRepository).findByTennisCourt_IdOrderByStartDateTime(id);
            verify(scheduleMapper).map(schedules);
            verifyNoMoreInteractions(scheduleRepository, scheduleMapper);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSchedule_endDateInvalid() {

        createScheduleRequestDTO
            .setStartDateTime(schedulesDTO.get(0).getStartDateTime().minusMinutes(HALF_HOUR_IN_MINUTES));

        when(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(any())).thenReturn(schedules);
        when(scheduleMapper.map(any(List.class))).thenReturn(schedulesDTO);

        try {
            service.addSchedule(createScheduleRequestDTO);
        } finally {
            verify(scheduleRepository).findByTennisCourt_IdOrderByStartDateTime(id);
            verify(scheduleMapper).map(schedules);
            verifyNoMoreInteractions(scheduleRepository, scheduleMapper);
        }
    }

    @Test
    public void findSchedulesByDates() {

        final LocalDate startDate = LocalDate.MIN;
        final LocalDate endDate = LocalDate.MAX;
        final LocalDateTime startDateTime = of(startDate, LocalTime.of(0, 0));
        final LocalDateTime endDateTime = of(endDate, LocalTime.of(23, 59));

        when(scheduleRepository.findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(any(), any()))
            .thenReturn(schedules);
        when(scheduleMapper.map(any(List.class))).thenReturn(schedulesDTO);

        final List<ScheduleDTO> actual = service.findSchedulesByDates(startDate, endDate);

        verify(scheduleRepository)
            .findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(startDateTime, endDateTime);
        verify(scheduleMapper).map(schedules);

        assertEquals(schedulesDTO, actual);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findSchedule_scheduleNotFound() {

        when(scheduleRepository.findById(any())).thenReturn(empty());

        try {
            service.findSchedule(id);
        } finally {
            verify(scheduleRepository).findById(id);
            verifyNoInteractions(scheduleMapper);
        }
    }

    @Test
    public void findSchedule_scheduleFound() {

        when(scheduleRepository.findById(any())).thenReturn(Optional.of(schedules.get(0)));
        when(scheduleMapper.map(any(Schedule.class))).thenReturn(schedulesDTO.get(0));

        final ScheduleDTO actual = service.findSchedule(id);

        verify(scheduleRepository).findById(id);
        verify(scheduleMapper).map(schedules.get(0));

        assertEquals(schedulesDTO.get(0), actual);
    }

    @Test
    public void findSchedulesByTennisCourtId() {

        when(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(any()))
            .thenReturn(schedules);
        when(scheduleMapper.map(any(List.class))).thenReturn(schedulesDTO);

        final List<ScheduleDTO> actual = service.findSchedulesByTennisCourtId(id);

        verify(scheduleRepository).findByTennisCourt_IdOrderByStartDateTime(id);
        verify(scheduleMapper).map(schedules);

        assertEquals(schedulesDTO, actual);
    }

    @Test
    public void findAvailableSchedules() {

        when(scheduleRepository.findAvailableSchedules()).thenReturn(schedules);
        when(scheduleMapper.map(any(List.class))).thenReturn(schedulesDTO);

        final List<ScheduleDTO> actual = service.findAvailableSchedules();

        verify(scheduleRepository).findAvailableSchedules();
        verify(scheduleMapper).map(schedules);

        assertEquals(schedulesDTO, actual);
    }
}