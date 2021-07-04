package com.tenniscourts.schedules;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    ScheduleDTO map(Schedule source);

    List<ScheduleDTO> map(List<Schedule> source);

    @Mapping(target = "tennisCourt.id", source = "tennisCourtId")
    @Mapping(target = "startDateTime", source = "startDateTime")
    @Mapping(target = "endDateTime", expression = "java(source.getStartDateTime().plusHours(1))")
    Schedule map(CreateScheduleRequestDTO source);
}
