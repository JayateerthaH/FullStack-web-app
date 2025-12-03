package com.attendance.Attendance.Repository;

import com.attendance.Attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByEmailAndDate(String email, LocalDate date);
}
