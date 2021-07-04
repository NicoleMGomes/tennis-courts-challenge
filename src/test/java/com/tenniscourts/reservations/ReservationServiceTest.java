package com.tenniscourts.reservations;

import static com.tenniscourts.commons.Fixture.make;
import static com.tenniscourts.reservations.ReservationStatus.CANCELLED;
import static com.tenniscourts.reservations.ReservationStatus.READY_TO_PLAY;
import static com.tenniscourts.reservations.ReservationStatus.RESCHEDULED;
import static java.math.BigDecimal.ZERO;
import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestService;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleService;

@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceTest {

    @InjectMocks
    private ReservationService service;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private GuestService guestService;

    private Long id;
    private ReservationDTO reservationDTO;
    private Reservation reservation;
    private CreateReservationRequestDTO createReservationRequestDTO;
    private List<ScheduleDTO> schedulesDTO;
    private Long scheduleId;

    @Before
    public void before() {
        id = nextLong();
        scheduleId = nextLong();
        reservationDTO = make(new ReservationDTO());
        reservation = make(new Reservation());
        reservation.setId(id);
        reservation.getSchedule().setId(nextLong());
        reservation.setGuest(new Guest() {{
            setId(nextLong());
        }});
        createReservationRequestDTO = make(new CreateReservationRequestDTO());
        schedulesDTO = range(0, nextInt(2, 5))
            .mapToObj(i -> make(new ScheduleDTO()))
            .collect(Collectors.toList());
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(createReservationRequestDTO.getScheduleId());
        schedulesDTO.add(scheduleDTO);
        scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(scheduleId);
        schedulesDTO.add(scheduleDTO);
    }

    @Test
    public void bookReservation_validGuestAndValidSchedule() {

        when(guestService.existsActiveGuestById(any(Long.class))).thenReturn(true);
        when(scheduleService.findAvailableSchedules()).thenReturn(schedulesDTO);
        when(reservationRepository.saveAndFlush(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.map(any(Reservation.class))).thenReturn(reservationDTO);
        when(reservationMapper.map(any(CreateReservationRequestDTO.class))).thenReturn(reservation);

        final ReservationDTO actual = service.bookReservation(createReservationRequestDTO);

        verify(guestService).existsActiveGuestById(createReservationRequestDTO.getGuestId());
        verify(scheduleService).findAvailableSchedules();
        verify(reservationRepository).saveAndFlush(reservation);
        verify(reservationMapper).map(reservation);
        verify(reservationMapper).map(createReservationRequestDTO);

        assertEquals(reservationDTO, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookReservation_invalidGuest() {

        when(guestService.existsActiveGuestById(any(Long.class))).thenReturn(false);

        try {
            service.bookReservation(createReservationRequestDTO);
        } finally {
            verify(guestService).existsActiveGuestById(createReservationRequestDTO.getGuestId());
            verifyNoInteractions(scheduleService, reservationRepository, reservationMapper);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void bookReservation_invalidSchedule() {

        schedulesDTO = new ArrayList<>();

        when(guestService.existsActiveGuestById(any(Long.class))).thenReturn(true);
        when(scheduleService.findAvailableSchedules()).thenReturn(schedulesDTO);

        try {
            service.bookReservation(createReservationRequestDTO);
        } finally {
            verify(guestService).existsActiveGuestById(createReservationRequestDTO.getGuestId());
            verify(scheduleService).findAvailableSchedules();
            verifyNoInteractions(reservationRepository, reservationMapper);
        }
    }

    @Test
    public void findReservation_reservationFound() {

        when(reservationRepository.findById(any(Long.class))).thenReturn(of(reservation));
        when(reservationMapper.map(any(Reservation.class))).thenReturn(reservationDTO);

        final ReservationDTO actual = service.findReservation(id);

        verify(reservationRepository).findById(id);
        verify(reservationMapper).map(reservation);
        verifyNoInteractions(scheduleService, guestService);

        assertEquals(reservationDTO, actual);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findReservation_reservationNotFound() {

        when(reservationRepository.findById(any(Long.class))).thenReturn(empty());

        try {
            service.findReservation(id);
        } finally {
            verify(reservationRepository).findById(id);
            verifyNoInteractions(reservationMapper, scheduleService, guestService);
        }
    }

    @Test
    public void cancelReservation_statusReadyToPlay() {

        reservation.setReservationStatus(READY_TO_PLAY);
        reservation.getSchedule().setStartDateTime(now().plusMinutes(nextInt(10, 30)));
        final BigDecimal value = reservation.getValue();

        when(reservationRepository.findById(any(Long.class))).thenReturn(of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.map(any(Reservation.class))).thenReturn(reservationDTO);

        final ReservationDTO actual = service.cancelReservation(id);

        verify(reservationRepository).findById(id);
        verify(reservationRepository).save(reservation);
        verify(reservationMapper).map(reservation);
        verifyNoInteractions(scheduleService, guestService);

        assertEquals(reservationDTO, actual);
        assertEquals(CANCELLED, reservation.getReservationStatus());
        assertEquals(ZERO, reservation.getRefundValue());
        assertEquals(value, reservation.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelReservation_statusNotReadyToPlay() {

        reservation.setReservationStatus(CANCELLED);
        reservation.getSchedule().setStartDateTime(now().plusMinutes(nextInt(10, 30)));

        when(reservationRepository.findById(any(Long.class))).thenReturn(of(reservation));

        try {
            service.cancelReservation(id);
        } finally {
            verify(reservationRepository).findById(id);
            verifyNoMoreInteractions(reservationRepository);
            verifyNoInteractions(scheduleService, guestService, reservationMapper);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelReservation_startDateBeforeNow() {

        reservation.setReservationStatus(READY_TO_PLAY);
        reservation.getSchedule().setStartDateTime(now().minusMinutes(nextInt(10, 30)));

        when(reservationRepository.findById(any(Long.class))).thenReturn(of(reservation));

        try {
            service.cancelReservation(id);
        } finally {
            verify(reservationRepository).findById(id);
            verifyNoMoreInteractions(reservationRepository);
            verifyNoInteractions(scheduleService, guestService, reservationMapper);
        }
    }

    @Test(expected = EntityNotFoundException.class)
    public void cancelReservation_reservationNotFound() {

        when(reservationRepository.findById(any(Long.class))).thenReturn(empty());

        try {
            service.cancelReservation(id);
        } finally {
            verify(reservationRepository).findById(id);
            verifyNoMoreInteractions(reservationRepository);
            verifyNoInteractions(scheduleService, guestService, reservationMapper);
        }
    }

    @Test
    public void rescheduleReservation_statusReadyToPlay() {

        reservation.setReservationStatus(READY_TO_PLAY);
        reservation.getSchedule().setStartDateTime(now().plusDays(nextInt(2, 3)));
        final BigDecimal value = reservation.getValue();
        final ArgumentCaptor<CreateReservationRequestDTO> dtoCaptor = forClass(CreateReservationRequestDTO.class);

        when(scheduleService.findAvailableSchedules()).thenReturn(schedulesDTO);
        when(reservationRepository.findById(any(Long.class))).thenReturn(of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationRepository.saveAndFlush(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.map(any(CreateReservationRequestDTO.class))).thenReturn(reservation);
        when(reservationMapper.map(any(Reservation.class))).thenReturn(reservationDTO);

        final ReservationDTO actual = service.rescheduleReservation(id, scheduleId);

        verify(reservationRepository).findById(id);
        verify(scheduleService).findAvailableSchedules();
        verify(reservationRepository).save(reservation);
        verify(reservationMapper).map(dtoCaptor.capture());
        verify(reservationMapper, times(2)).map(reservation);
        verify(reservationRepository).saveAndFlush(reservation);
        verifyNoInteractions(guestService);

        assertEquals(reservationDTO, actual);
        assertEquals(RESCHEDULED, reservation.getReservationStatus());
        assertEquals(value, reservation.getRefundValue());
        assertEquals(value.subtract(reservation.getRefundValue()), reservation.getValue());
        assertNotNull(dtoCaptor.getValue());
        assertEquals(reservation.getGuest().getId(), dtoCaptor.getValue().getGuestId());
        assertEquals(scheduleId, dtoCaptor.getValue().getScheduleId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void rescheduleReservation_reservationNotFound() {

        when(reservationRepository.findById(any(Long.class))).thenReturn(empty());

        try {
            service.rescheduleReservation(id, scheduleId);
        } finally {
            verify(reservationRepository).findById(id);
            verifyNoMoreInteractions(reservationRepository);
            verifyNoInteractions(guestService, scheduleService, reservationMapper);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rescheduleReservation_sameSlot() {

        when(reservationRepository.findById(any(Long.class))).thenReturn(of(reservation));

        try {
            service.rescheduleReservation(id, reservation.getSchedule().getId());
        } finally {
            verify(reservationRepository).findById(id);
            verifyNoMoreInteractions(reservationRepository);
            verifyNoInteractions(guestService, scheduleService, reservationMapper);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rescheduleReservation_statusNotReadyToPlay() {

        reservation.setReservationStatus(RESCHEDULED);
        reservation.getSchedule().setStartDateTime(now().plusMinutes(nextInt(10, 30)));

        when(reservationRepository.findById(any(Long.class))).thenReturn(of(reservation));

        try {
            service.rescheduleReservation(id, scheduleId);
        } finally {
            verify(reservationRepository).findById(id);
            verifyNoMoreInteractions(reservationRepository);
            verifyNoInteractions(guestService, scheduleService, reservationMapper);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rescheduleReservation_startDateBeforeNow() {

        reservation.setReservationStatus(READY_TO_PLAY);
        reservation.getSchedule().setStartDateTime(now().minusMinutes(nextInt(10, 30)));

        when(reservationRepository.findById(any(Long.class))).thenReturn(of(reservation));

        try {
            service.rescheduleReservation(id, scheduleId);
        } finally {
            verify(reservationRepository).findById(id);
            verifyNoMoreInteractions(reservationRepository);
            verifyNoInteractions(guestService, scheduleService, reservationMapper);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rescheduleReservation_invalidSchedule() {

        reservation.setReservationStatus(READY_TO_PLAY);
        reservation.getSchedule().setStartDateTime(now().plusMinutes(nextInt(10, 30)));
        schedulesDTO = new ArrayList<>();

        when(scheduleService.findAvailableSchedules()).thenReturn(schedulesDTO);
        when(reservationRepository.findById(any(Long.class))).thenReturn(of(reservation));

        try {
            service.rescheduleReservation(id, scheduleId);
        } finally {
            verify(reservationRepository).findById(id);
            verify(scheduleService).findAvailableSchedules();
            verifyNoMoreInteractions(reservationRepository);
            verifyNoInteractions(guestService, reservationMapper);
        }
    }
}