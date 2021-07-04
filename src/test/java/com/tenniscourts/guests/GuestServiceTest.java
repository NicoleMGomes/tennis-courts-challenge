package com.tenniscourts.guests;

import static com.tenniscourts.commons.Fixture.make;
import static com.tenniscourts.guests.GuestStatus.ACTIVE;
import static com.tenniscourts.guests.GuestStatus.INACTIVE;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.tenniscourts.exceptions.EntityNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class GuestServiceTest {

    @InjectMocks
    private GuestService service;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private GuestMapper guestMapper;

    private Guest guest;
    private GuestDTO guestDTO;
    private Long id;
    private String name;

    @Before
    public void before() {
        guest = make(new Guest());
        guestDTO = make(new GuestDTO());
        id = nextLong();
        name = randomAlphabetic(10);
    }

    @Test
    public void addGuest() {

        final CreateGuestRequestDTO dto = make(new CreateGuestRequestDTO());

        when(guestMapper.map(any(CreateGuestRequestDTO.class))).thenReturn(guest);
        when(guestRepository.saveAndFlush(any(Guest.class))).thenReturn(guest);
        when(guestMapper.map(any(Guest.class))).thenReturn(guestDTO);

        final GuestDTO actual = service.addGuest(dto);

        verify(guestMapper).map(dto);
        verify(guestRepository).saveAndFlush(guest);
        verify(guestMapper).map(guest);

        assertEquals(guestDTO, actual);
    }

    @Test
    public void findGuestById_guestFound() {

        when(guestRepository.findById(any(Long.class))).thenReturn(of(guest));
        when(guestMapper.map(any(Guest.class))).thenReturn(guestDTO);

        final GuestDTO actual = service.findGuestById(id);

        verify(guestRepository).findById(id);
        verify(guestMapper).map(guest);

        assertEquals(guestDTO, actual);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findGuestById_guestNotFound() {

        when(guestRepository.findById(any(Long.class))).thenReturn(empty());

        try {
            service.findGuestById(id);
        } finally {
            verify(guestRepository).findById(id);
            verifyNoInteractions(guestMapper);
        }
    }

    @Test
    public void findGuests_byName() {

        final List<Guest> guests = singletonList(guest);
        final List<GuestDTO> guestsDTO = singletonList(guestDTO);

        when(guestRepository.findByName(any(String.class))).thenReturn(guests);
        when(guestMapper.map(any(List.class))).thenReturn(guestsDTO);

        final List<GuestDTO> actual = service.findGuests(name);

        verify(guestRepository).findByName(name);
        verify(guestMapper).map(guests);

        assertEquals(guestsDTO, actual);
    }

    @Test
    public void findGuests_all() {

        final List<Guest> guests = singletonList(guest);
        final List<GuestDTO> guestsDTO = singletonList(guestDTO);

        when(guestRepository.findAll()).thenReturn(guests);
        when(guestMapper.map(any(List.class))).thenReturn(guestsDTO);

        final List<GuestDTO> actual = service.findGuests(null);

        verify(guestRepository).findAll();
        verify(guestMapper).map(guests);

        assertEquals(guestsDTO, actual);
    }

    @Test
    public void removeGuest_active() {

        guestDTO.setStatus(ACTIVE);

        when(guestRepository.findById(id)).thenReturn(of(guest));
        when(guestRepository.save(guest)).thenReturn(guest);
        when(guestMapper.map(any(Guest.class))).thenReturn(guestDTO);
        when(guestMapper.map(any(GuestDTO.class))).thenReturn(guest);

        final GuestDTO actual = service.removeGuest(id);

        verify(guestRepository).findById(id);
        verify(guestRepository).save(guest);
        verify(guestMapper, times(2)).map(guest);
        verify(guestMapper).map(guestDTO);

        assertEquals(guestDTO, actual);
        assertEquals(INACTIVE, actual.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeGuest_inactive() {

        guestDTO.setStatus(INACTIVE);

        when(guestRepository.findById(id)).thenReturn(of(guest));
        when(guestMapper.map(any(Guest.class))).thenReturn(guestDTO);

        try {
            service.removeGuest(id);
        } finally {
            verify(guestRepository).findById(id);
            verify(guestMapper).map(guest);
            verifyNoMoreInteractions(guestRepository, guestMapper);
        }
    }

    @Test
    public void updateGuest_active() {

        guestDTO.setStatus(ACTIVE);

        when(guestRepository.findById(id)).thenReturn(of(guest));
        when(guestRepository.save(guest)).thenReturn(guest);
        when(guestMapper.map(any(Guest.class))).thenReturn(guestDTO);
        when(guestMapper.map(any(GuestDTO.class))).thenReturn(guest);

        final GuestDTO actual = service.updateGuest(id, name);

        verify(guestRepository).findById(id);
        verify(guestRepository).save(guest);
        verify(guestMapper, times(2)).map(guest);
        verify(guestMapper).map(guestDTO);

        assertEquals(guestDTO, actual);
        assertEquals(name, actual.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuest_inactive() {

        guestDTO.setStatus(INACTIVE);

        when(guestRepository.findById(id)).thenReturn(of(guest));
        when(guestMapper.map(any(Guest.class))).thenReturn(guestDTO);

        try {
            service.updateGuest(id, name);
        } finally {
            verify(guestRepository).findById(id);
            verify(guestMapper).map(guest);
            verifyNoMoreInteractions(guestRepository, guestMapper);
        }
    }

    @Test
    public void existsActiveGuestById() {

        final boolean exists = nextBoolean();

        when(guestRepository.existsByIdAndStatusEquals(id, ACTIVE)).thenReturn(exists);

        final boolean actual = service.existsActiveGuestById(id);

        verify(guestRepository).existsByIdAndStatusEquals(id, ACTIVE);
        verifyNoMoreInteractions(guestRepository);
        verifyNoInteractions(guestMapper);

        assertEquals(exists, actual);
    }
}