package com.tenniscourts.schedules;

import static com.tenniscourts.commons.Fixture.make;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@FixMethodOrder(NAME_ASCENDING)
@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    private static final String BASE_URL = "/schedule";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

    @Autowired
    protected WebApplicationContext context;

    @MockBean
    private ScheduleService scheduleService;

    private ObjectMapper mapper;
    private MockMvc mockMvc;
    private Long scheduleId;

    @Before
    public void before() {
        scheduleId = nextLong();
        mockMvc = webAppContextSetup(context).build();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    @Test
    public void addScheduleTennisCourt() throws Exception {

        final MockHttpServletRequestBuilder postBuilder = MockMvcRequestBuilders.post(BASE_URL);
        final CreateScheduleRequestDTO dto = make(new CreateScheduleRequestDTO());
        dto.setStartDateTime(now().withSecond(0).withNano(0));
        final ScheduleDTO scheduleDTO = make(new ScheduleDTO());

        when(scheduleService.addSchedule(any())).thenReturn(scheduleDTO);

        mockMvc.perform(postBuilder
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());

        verify(scheduleService).addSchedule(dto);
    }

    @Test
    public void findSchedulesByDates() throws Exception {

        final LocalDate startDate = LocalDate.MIN;
        final LocalDate endDate = LocalDate.MAX;
        final MockHttpServletRequestBuilder getBuilder = MockMvcRequestBuilders.get(BASE_URL);

        mockMvc.perform(getBuilder
            .param(START_DATE, startDate.toString())
            .param(END_DATE, endDate.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(scheduleService).findSchedulesByDates(startDate, endDate);
    }

    @Test
    public void findByScheduleId() throws Exception {

        final String url = format("%s/%s", BASE_URL, scheduleId);
        final MockHttpServletRequestBuilder getBuilder = MockMvcRequestBuilders.get(url);

        mockMvc.perform(getBuilder
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(scheduleService).findSchedule(scheduleId);
    }

    @Test
    public void findAvailableSchedules() throws Exception {

        final String url = format("%s/available", BASE_URL);
        final MockHttpServletRequestBuilder getBuilder = MockMvcRequestBuilders.get(url);

        mockMvc.perform(getBuilder
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(scheduleService).findAvailableSchedules();
    }
}