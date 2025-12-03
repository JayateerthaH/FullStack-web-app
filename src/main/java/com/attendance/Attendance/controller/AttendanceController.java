package com.attendance.Attendance.controller;
// com.attendance.Attendance.controller.AttendanceController.java
//package com.attendance.Attendance.controller;

import com.attendance.Attendance.dto.QRRequest;
import com.attendance.Attendance.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attendance")
@CrossOrigin(origins = "http://localhost:3000/login")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestBody QRRequest qrRequest) {

        try {
            if (qrRequest.getScannedData() == null || qrRequest.getScannedData().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid QR Data");
            }

            String email = qrRequest.getScannedData().trim();

            String result = attendanceService.markAttendance(email);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
