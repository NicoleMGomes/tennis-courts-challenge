package com.tenniscourts.tenniscourts;

import org.springframework.stereotype.Service;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.ScheduleService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TennisCourtService {

    private final TennisCourtRepository tennisCourtRepository;

    private final TennisCourtMapper tennisCourtMapper;

    private final ScheduleService scheduleService;

    public TennisCourtDTO addTennisCourt(final CreateTennisCourtRequestDTO createTennisCourtRequestDTO) {

        return tennisCourtMapper
            .map(tennisCourtRepository.saveAndFlush(tennisCourtMapper.map(createTennisCourtRequestDTO)));
    }

    public TennisCourtDTO findTennisCourtById(final Long id) {

        return tennisCourtRepository.findById(id).map(tennisCourtMapper::map).orElseThrow(() ->
            new EntityNotFoundException("Tennis Court not found.")
        );
    }

    public TennisCourtDTO findTennisCourtWithSchedulesById(final Long tennisCourtId) {

        final TennisCourtDTO tennisCourtDTO = findTennisCourtById(tennisCourtId);
        tennisCourtDTO.setTennisCourtSchedules(scheduleService.findSchedulesByTennisCourtId(tennisCourtId));
        return tennisCourtDTO;
    }
}
