package com.tenniscourts.schedules;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByTennisCourt_IdOrderByStartDateTime(Long id);

    List<Schedule> findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(LocalDateTime startDateTime,
        LocalDateTime endDateTime);

    @Query("SELECT s FROM Schedule s "
        + "WHERE s.startDateTime >= SYSDATE "
        + "AND s.id NOT IN ("
        + "SELECT r.schedule.id FROM Reservation r WHERE r.reservationStatus = 0) "
        + "ORDER BY s.startDateTime")
    List<Schedule> findAvailableSchedules();
}