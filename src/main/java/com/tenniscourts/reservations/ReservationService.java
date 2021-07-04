package com.tenniscourts.reservations;

import static com.tenniscourts.reservations.ReservationStatus.CANCELLED;
import static com.tenniscourts.reservations.ReservationStatus.READY_TO_PLAY;
import static com.tenniscourts.reservations.ReservationStatus.RESCHEDULED;
import static java.math.BigDecimal.ZERO;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.GuestService;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReservationService {

    private static final Integer DAY_IN_HOURS = 24;

    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    private final ScheduleService scheduleService;

    private final GuestService guestService;

    public ReservationDTO bookReservation(final CreateReservationRequestDTO createReservationRequestDTO) {

        if (!guestService.existsActiveGuestById(createReservationRequestDTO.getGuestId())) {
            throw new IllegalArgumentException("Cannot create because guest is invalid.");
        }

        this.validateSchedule(createReservationRequestDTO.getScheduleId());

        return reservationMapper
            .map(reservationRepository.saveAndFlush(reservationMapper.map(createReservationRequestDTO)));
    }

    private void validateSchedule(final Long scheduleI) {

        final boolean isScheduleInvalid = scheduleService.findAvailableSchedules().stream()
            .map(ScheduleDTO::getId)
            .noneMatch(scheduleId -> scheduleId.equals(scheduleI));

        if (isScheduleInvalid) {
            throw new IllegalArgumentException("Cannot create because this schedule isn't available.");
        }
    }

    public ReservationDTO findReservation(final Long reservationId) {

        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() ->
            new EntityNotFoundException("Reservation not found.")
        );
    }

    public ReservationDTO cancelReservation(final Long reservationId) {

        return reservationRepository.findById(reservationId).map(reservation -> {
            this.validateUpdate(reservation);
            return reservationMapper.map(this.updateReservation(reservation, getRefundValue(reservation), CANCELLED));
        }).orElseThrow(() ->
            new EntityNotFoundException("Reservation not found.")
        );
    }

    private Reservation updateReservation(final Reservation reservation, final BigDecimal refundValue,
        final ReservationStatus status) {

        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        return reservationRepository.save(reservation);
    }

    private void validateUpdate(final Reservation reservation) {

        if (!READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    private BigDecimal getRefundValue(final Reservation reservation) {

        final long hours = HOURS.between(now(), reservation.getSchedule().getStartDateTime());

        return hours >= DAY_IN_HOURS ? reservation.getValue() : ZERO;
    }

    public ReservationDTO rescheduleReservation(final Long previousReservationId, final Long scheduleId) {

        Reservation previousReservation = reservationRepository.findById(previousReservationId).orElseThrow(() ->
            new EntityNotFoundException("Reservation not found.")
        );

        if (scheduleId.equals(previousReservation.getSchedule().getId())) {
            throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        this.validateUpdate(previousReservation);
        this.validateSchedule(scheduleId);

        final CreateReservationRequestDTO createReservationRequestDTO = CreateReservationRequestDTO.builder()
            .guestId(previousReservation.getGuest().getId())
            .scheduleId(scheduleId)
            .build();

        previousReservation = this
            .updateReservation(previousReservation, getRefundValue(previousReservation), RESCHEDULED);

        final ReservationDTO newReservation = reservationMapper
            .map(reservationRepository.saveAndFlush(reservationMapper.map(createReservationRequestDTO)));

        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));

        return newReservation;
    }
}
