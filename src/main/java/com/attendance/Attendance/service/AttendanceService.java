// com.attendance.Attendance.service.AttendanceService.java

package com.attendance.Attendance.service;

import com.attendance.Attendance.Repository.AttendanceRepository;
import com.attendance.Attendance.Repository.UserRepository;
import com.attendance.Attendance.entity.Attendance;
import com.attendance.Attendance.entity.Users;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    public String markAttendance(String scannedEmail) throws Exception {
        System.out.println(scannedEmail);
        String email = scannedEmail.replaceAll("\\d+$", "");
        String digits = scannedEmail.replaceAll(".*?(\\d+)$", "$1");   // keep only digits

        Long id = Long.parseLong(digits);
        Optional<Users> user = userRepository.findByEmail(email);
        System.out.println(id);

        if (user.isEmpty() || !Objects.equals(user.get().getId(), id)) {
            throw new Exception("Email not registered: " + scannedEmail);
        }

        LocalDate today = LocalDate.now();

        // Check if attendance already exists today
        if (attendanceRepository.existsByEmailAndDate(scannedEmail, today)) {
            throw new Exception("Attendance already marked today!");
        }

        Attendance attendance = new Attendance(
                scannedEmail,
                today,
                LocalDateTime.now()
        );

        attendanceRepository.save(attendance);

        return "Attendance marked successfully for: " + scannedEmail;
    }
}
