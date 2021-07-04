package com.tenniscourts.tenniscourts;

import com.tenniscourts.schedules.ScheduleDTO;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TennisCourtDTO {

    private Long id;

    @NotNull
    private String name;

    private List<ScheduleDTO> tennisCourtSchedules;

}
