package com.tenniscourts.guests;

import static com.tenniscourts.commons.Fixture.make;
import static java.lang.String.format;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
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
@WebMvcTest(GuestController.class)
public class GuestControllerTest {

    private static final String BASE_URL = "/guest";
    private static final String NAME = "name";

    @Autowired
    protected WebApplicationContext context;

    @MockBean
    private GuestService guestService;

    private ObjectMapper mapper;
    private MockMvc mockMvc;
    private Long guestId;

    @Before
    public void before() {
        guestId = nextLong();
        mockMvc = webAppContextSetup(context).build();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    @Test
    public void addGuest() throws Exception {

        final MockHttpServletRequestBuilder postBuilder = MockMvcRequestBuilders.post(BASE_URL);
        final CreateGuestRequestDTO dto = make(new CreateGuestRequestDTO());
        final GuestDTO guestDTO = make(new GuestDTO());

        when(guestService.addGuest(any())).thenReturn(guestDTO);

        mockMvc.perform(postBuilder
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());

        verify(guestService).addGuest(dto);
    }

    @Test
    public void findGuestById() throws Exception {

        final String url = format("%s/%s", BASE_URL, guestId);
        final MockHttpServletRequestBuilder getBuilder = MockMvcRequestBuilders.get(url);

        mockMvc.perform(getBuilder
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(guestService).findGuestById(guestId);
    }

    @Test
    public void findGuests() throws Exception {

        final MockHttpServletRequestBuilder getBuilder = MockMvcRequestBuilders.get(BASE_URL);

        mockMvc.perform(getBuilder
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(guestService).findGuests(null);
    }

    @Test
    public void deleteGuest() throws Exception {

        final String url = format("%s/%s", BASE_URL, guestId);
        final MockHttpServletRequestBuilder deleteBuilder = MockMvcRequestBuilders.delete(url);

        mockMvc.perform(deleteBuilder
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(guestService).removeGuest(guestId);
    }

    @Test
    public void updateGuest() throws Exception {

        final String name = randomAlphabetic(11);
        final String url = format("%s/%s", BASE_URL, guestId);
        final MockHttpServletRequestBuilder patchBuilder = MockMvcRequestBuilders.patch(url);

        mockMvc.perform(patchBuilder
            .param(NAME, name)
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(guestService).updateGuest(guestId, name);
    }
}