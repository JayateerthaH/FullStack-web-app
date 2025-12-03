package com.attendance.Attendance.service;

import com.attendance.Attendance.Exceptions.ResourceNotFoundException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendQRMail(String to, String subject, String body, File qrFile) throws Exception {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            helper.addAttachment("qr.png", qrFile);

            mailSender.send(message);
        }
        catch(Exception ex){
            System.err.println("Email sending failed: " + ex.getMessage());
            throw new ResourceNotFoundException("Failed to send email: " + ex.getMessage());
        }
    }
}
