package com.attendance.Attendance.service;

import com.attendance.Attendance.Exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
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

    @Value("${SENDPULSE_CLIENT_ID:}")
    private String clientId;

    @Value("${SENDPULSE_CLIENT_SECRET:}")
    private String clientSecret;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private String accessToken;
    private long tokenExpiry = 0;

    public EmailService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    private synchronized String getAccessToken() throws IOException {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry) {
            return accessToken;
        }

        RequestBody body = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();

        Request request = new Request.Builder()
                .url("https://api.sendpulse.com/oauth/access_token")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get access token: " + response.code());
            }

            String responseBody = response.body().string();
            JsonNode json = objectMapper.readTree(responseBody);
            accessToken = json.get("access_token").asText();
            int expiresIn = json.get("expires_in").asInt();
            tokenExpiry = System.currentTimeMillis() + (expiresIn * 1000L) - 60000;

            System.out.println("SendPulse access token obtained successfully");
            return accessToken;
        }
    }

    public void sendQRMail(String to, String subject, String body, File qrFile) throws Exception {
        try {
            String token = getAccessToken();

            byte[] fileContent = Files.readAllBytes(qrFile.toPath());
            String base64Content = Base64.getEncoder().encodeToString(fileContent);

            String jsonPayload = objectMapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
                put("email", new java.util.HashMap<String, Object>() {{
                    put("subject", subject);
                    put("from", new java.util.HashMap<String, String>() {{
                        put("name", "Attendance System");
                        put("email", "noreply@sendpulse.com");
                    }});
                    put("to", new java.util.ArrayList<java.util.HashMap<String, String>>() {{
                        add(new java.util.HashMap<String, String>() {{
                            put("email", to);
                        }});
                    }});
                    put("html", "<p>" + body + "</p><p>Please find your QR code attached.</p>");
                    put("attachments_binary", new java.util.HashMap<String, String>() {{
                        put("qr-code.png", base64Content);
                    }});
                }});
            }});

            RequestBody requestBody = RequestBody.create(
                    jsonPayload,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url("https://api.sendpulse.com/smtp/emails")
                    .header("Authorization", "Bearer " + token)
                    .post(requestBody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string();

                if (!response.isSuccessful()) {
                    System.err.println("SendPulse email failed: " + responseBody);
                    throw new ResourceNotFoundException("Failed to send email: " + responseBody);
                }

                System.out.println("Email sent successfully via SendPulse: " + responseBody);
            }

        } catch (IOException ex) {
            System.err.println("Email sending failed: " + ex.getMessage());
            throw new ResourceNotFoundException("Failed to send email: " + ex.getMessage());
        }
    }
}