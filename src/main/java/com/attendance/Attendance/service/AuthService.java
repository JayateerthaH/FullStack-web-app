package com.attendance.Attendance.service;

import com.attendance.Attendance.Exceptions.ResourceNotFoundException;
import com.attendance.Attendance.Repository.UserRepository;
import com.attendance.Attendance.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QRService qrService;

    @Autowired
    private EmailService emailService;

    public String login(String email, String password) throws Exception {

        Users user = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User with Email id "+email+" not registered"));;

        if (!user.getPassword().equals(password)) {
            throw new ResourceNotFoundException("Invalid Password");
        }

        if(user.isRegistered()){
            return "Already registered";
        }

        user.setRegistered(true);
        userRepository.save(user);
        // Generate QR file (email + id)
        File qrFile = qrService.generateQRForEmail(user.getEmail() + user.getId());

        // Send Email with QR
        emailService.sendQRMail(
                user.getEmail(),
                "Your Login QR Code",
                "Here is your QR code for attendance.",
                qrFile
        );

        return "Login success! QR sent to " + user.getEmail();
    }


}
