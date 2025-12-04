package com.attendance.Attendance.service;

import com.attendance.Attendance.Exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    @Value("${brevo.api.key:default}")
    private String brevoApiKey;

    @Value("${sender.email:shreyasnkulkarnicr7@gmail.com}")
    private String senderEmail;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EmailService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public void sendQRMail(String to, String subject, String bodyText, File qrFile) throws Exception {
        try {
            byte[] fileContent = Files.readAllBytes(qrFile.toPath());
            String base64Content = Base64.getEncoder().encodeToString(fileContent);

            String jsonPayload = "{" +
                "\"sender\": {" +
                    "\"name\": \"Attendance System\"," +
                    "\"email\": \"" + senderEmail + "\"" +
                "}," +
                "\"to\": [{" +
                    "\"email\": \"" + escapeJson(to) + "\"" +
                "}]," +
                "\"subject\": \"" + escapeJson(subject) + "\"," +
                "\"htmlContent\": \"<p>" + escapeJson(bodyText) + "</p><p>Please find your QR code attached.</p>\"," +
                "\"attachment\": [{" +
                    "\"name\": \"qr-code.png\"," +
                    "\"content\": \"" + base64Content + "\"" +
                "}]" +
            "}";

            RequestBody requestBody = RequestBody.create(
                    jsonPayload,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url("https://api.brevo.com/v3/smtp/email")
                    .header("api-key", brevoApiKey)
                    .header("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string();

                if (!response.isSuccessful()) {
                    System.err.println("Brevo email failed: " + responseBody);
                    throw new ResourceNotFoundException("Failed to send email: " + responseBody);
                }

                System.out.println("Email sent successfully via Brevo: " + responseBody);
            }

        } catch (IOException ex) {
            System.err.println("Email sending failed: " + ex.getMessage());
            throw new ResourceNotFoundException("Failed to send email: " + ex.getMessage());
        }
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}