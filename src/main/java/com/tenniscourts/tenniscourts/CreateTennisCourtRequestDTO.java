package com.tenniscourts.tenniscourts;

import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class CreateTennisCourtRequestDTO {

    @NotNull
    private String name;

}
