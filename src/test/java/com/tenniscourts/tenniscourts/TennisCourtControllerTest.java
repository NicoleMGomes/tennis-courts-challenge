package com.tenniscourts.tenniscourts;

import static com.tenniscourts.commons.Fixture.make;
import static java.lang.String.format;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@FixMethodOrder(NAME_ASCENDING)
@WebMvcTest(TennisCourtController.class)
public class TennisCourtControllerTest {

    private static final String BASE_URL = "/court";

    @Autowired
    protected WebApplicationContext context;

    @MockBean
    private TennisCourtService tennisCourtService;

    private ObjectMapper mapper;
    private MockMvc mockMvc;
    private Long tennisCourtId;

    @Before
    public void before() {
        tennisCourtId = nextLong();
        mockMvc = webAppContextSetup(context).build();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    @Test
    public void addTennisCourt() throws Exception {

        final MockHttpServletRequestBuilder postBuilder = MockMvcRequestBuilders.post(BASE_URL);
        final CreateTennisCourtRequestDTO dto = make(new CreateTennisCourtRequestDTO());
        final TennisCourtDTO tennisCourtDTO = make(new TennisCourtDTO());

        when(tennisCourtService.addTennisCourt(any())).thenReturn(tennisCourtDTO);

        mockMvc.perform(postBuilder
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());

        verify(tennisCourtService).addTennisCourt(dto);
    }

    @Test
    public void findTennisCourtById() throws Exception {

        final String url = format("%s/%s", BASE_URL, tennisCourtId);
        final MockHttpServletRequestBuilder getBuilder = MockMvcRequestBuilders.get(url);

        mockMvc.perform(getBuilder
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(tennisCourtService).findTennisCourtById(tennisCourtId);
    }

    @Test
    public void findTennisCourtWithSchedulesById() throws Exception {

        final String url = format("%s/schedules/%s", BASE_URL, tennisCourtId);
        final MockHttpServletRequestBuilder getBuilder = MockMvcRequestBuilders.get(url);

        mockMvc.perform(getBuilder
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(tennisCourtService).findTennisCourtWithSchedulesById(tennisCourtId);
    }
}