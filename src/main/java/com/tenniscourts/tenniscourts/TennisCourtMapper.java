package com.tenniscourts.tenniscourts;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TennisCourtMapper {

    TennisCourtDTO map(TennisCourt source);

    TennisCourt map(CreateTennisCourtRequestDTO source);
}
