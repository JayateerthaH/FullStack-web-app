package com.attendance.Attendance.service;

import com.attendance.Attendance.Exceptions.ResourceNotFoundException;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY:re_LLauJrZE_P2Xi2hQEizthHxzUe3NJGPiX}")
    private String resendApiKey;

    public void sendQRMail(String to, String subject, String body, File qrFile) throws Exception {
        try {
            Resend resend = new Resend(resendApiKey);

            byte[] fileContent = Files.readAllBytes(qrFile.toPath());
            String base64Content = Base64.getEncoder().encodeToString(fileContent);

            Attachment attachment = Attachment.builder()
                    .fileName("qr-code.png")
                    .content(base64Content)
                    .build();

            CreateEmailOptions emailOptions = CreateEmailOptions.builder()
                    .from("Attendance System <onboarding@resend.dev>")
                    .to(to)
                    .subject(subject)
                    .html("<p>" + body + "</p><p>Please find your QR code attached.</p>")
                    .attachments(List.of(attachment))
                    .build();

            CreateEmailResponse response = resend.emails().send(emailOptions);
            System.out.println("Email sent successfully! ID: " + response.getId());

        } catch (ResendException ex) {
            System.err.println("Resend email failed: " + ex.getMessage());
            throw new ResourceNotFoundException("Failed to send email: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Email sending failed: " + ex.getMessage());
            throw new ResourceNotFoundException("Failed to send email: " + ex.getMessage());
        }
    }
}