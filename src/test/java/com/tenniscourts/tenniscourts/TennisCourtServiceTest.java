package com.tenniscourts.tenniscourts;

import static com.tenniscourts.commons.Fixture.make;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleService;

@RunWith(MockitoJUnitRunner.class)
public class TennisCourtServiceTest {

    @InjectMocks
    private TennisCourtService service;

    @Mock
    private TennisCourtRepository tennisCourtRepository;

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private TennisCourtMapper tennisCourtMapper;

    private TennisCourt tennisCourt;
    private TennisCourtDTO tennisCourtDTO;
    private Long id;

    @Before
    public void before() {
        tennisCourt = make(new TennisCourt());
        tennisCourtDTO = make(new TennisCourtDTO());
        id = nextLong();
    }

    @Test
    public void addTennisCourt() {

        final CreateTennisCourtRequestDTO dto = make(new CreateTennisCourtRequestDTO());

        when(tennisCourtMapper.map(any(CreateTennisCourtRequestDTO.class))).thenReturn(tennisCourt);
        when(tennisCourtRepository.saveAndFlush(any())).thenReturn(tennisCourt);
        when(tennisCourtMapper.map(any(TennisCourt.class))).thenReturn(tennisCourtDTO);

        final TennisCourtDTO actual = service.addTennisCourt(dto);

        verify(tennisCourtMapper).map(dto);
        verify(tennisCourtRepository).saveAndFlush(tennisCourt);
        verify(tennisCourtMapper).map(tennisCourt);
        verifyNoInteractions(scheduleService);

        assertEquals(tennisCourtDTO, actual);
    }

    @Test
    public void findTennisCourtById_tennisCourtFound() {

        when(tennisCourtRepository.findById(any(Long.class))).thenReturn(of(tennisCourt));
        when(tennisCourtMapper.map(any(TennisCourt.class))).thenReturn(tennisCourtDTO);

        final TennisCourtDTO actual = service.findTennisCourtById(id);

        verify(tennisCourtRepository).findById(id);
        verify(tennisCourtMapper).map(tennisCourt);
        verifyNoInteractions(scheduleService);

        assertEquals(tennisCourtDTO, actual);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findTennisCourtById_tennisCourtNotFound() {

        when(tennisCourtRepository.findById(any(Long.class))).thenReturn(empty());

        try {
            service.findTennisCourtById(id);
        } finally {
            verify(tennisCourtRepository).findById(id);
            verifyNoInteractions(scheduleService, tennisCourtMapper);
        }
    }

    @Test
    public void findTennisCourtWithSchedulesById_tennisCourtFound() {

        final List<ScheduleDTO> schedules = range(0, nextInt(2, 5))
            .mapToObj(i -> make(new ScheduleDTO()))
            .collect(Collectors.toList());

        when(tennisCourtRepository.findById(any(Long.class))).thenReturn(of(tennisCourt));
        when(tennisCourtMapper.map(any(TennisCourt.class))).thenReturn(tennisCourtDTO);
        when(scheduleService.findSchedulesByTennisCourtId(any(Long.class))).thenReturn(schedules);

        final TennisCourtDTO actual = service.findTennisCourtWithSchedulesById(id);

        verify(tennisCourtRepository).findById(id);
        verify(tennisCourtMapper).map(tennisCourt);
        verify(scheduleService).findSchedulesByTennisCourtId(id);

        assertEquals(tennisCourtDTO, actual);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findTennisCourtWithSchedulesById_tennisCourtNotFound() {

        when(tennisCourtRepository.findById(any(Long.class))).thenReturn(empty());

        try {
            service.findTennisCourtWithSchedulesById(id);
        } finally {
            verify(tennisCourtRepository).findById(id);
            verifyNoInteractions(scheduleService, tennisCourtMapper);
        }
    }
}