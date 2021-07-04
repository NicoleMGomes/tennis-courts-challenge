package com.tenniscourts.reservations;

import static com.tenniscourts.commons.Fixture.make;
import static java.lang.String.format;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

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
@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    private static final String BASE_URL = "/reservation";
    private static final String SCHEDULE_ID = "scheduleId";

    @Autowired
    protected WebApplicationContext context;

    @MockBean
    private ReservationService reservationService;

    private ObjectMapper mapper;
    private MockMvc mockMvc;
    private Long reservationId;

    @Before
    public void before() {
        reservationId = nextLong();
        mockMvc = webAppContextSetup(context).build();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    @Test
    public void bookReservation() throws Exception {

        final MockHttpServletRequestBuilder postBuilder = MockMvcRequestBuilders.post(BASE_URL);
        final CreateReservationRequestDTO dto = make(CreateReservationRequestDTO.builder().build());
        final ReservationDTO reservationDTO = make(ReservationDTO.builder().build());

        when(reservationService.bookReservation(any())).thenReturn(reservationDTO);

        mockMvc.perform(postBuilder
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());

        verify(reservationService).bookReservation(dto);
    }

    @Test
    public void findReservation() throws Exception {

        final String url = format("%s/%s", BASE_URL, reservationId);
        final MockHttpServletRequestBuilder getBuilder = MockMvcRequestBuilders.get(url);

        mockMvc.perform(getBuilder
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(reservationService).findReservation(reservationId);
    }

    @Test
    public void cancelReservation() throws Exception {

        final String url = format("%s/%s", BASE_URL, reservationId);
        final MockHttpServletRequestBuilder deleteBuilder = MockMvcRequestBuilders.delete(url);

        mockMvc.perform(deleteBuilder
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(reservationService).cancelReservation(reservationId);
    }

    @Test
    public void rescheduleReservation() throws Exception {

        final long scheduleId = nextLong();
        final String url = format("%s/%s", BASE_URL, reservationId);
        final MockHttpServletRequestBuilder patchBuilder = MockMvcRequestBuilders.patch(url);

        mockMvc.perform(patchBuilder
            .param(SCHEDULE_ID, Long.toString(scheduleId))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(reservationService).rescheduleReservation(reservationId, scheduleId);
    }
}