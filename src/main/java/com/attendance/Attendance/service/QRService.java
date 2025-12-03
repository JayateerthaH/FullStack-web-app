package com.attendance.Attendance.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class QRService {

    public File generateQRCode(String data, String filename) throws Exception {
        BitMatrix matrix = new MultiFormatWriter()
                .encode(data, BarcodeFormat.QR_CODE, 300, 300);

        File file = new File(filename);

        MatrixToImageWriter.writeToPath(matrix, "PNG", file.toPath());

        return file;
    }

    public File generateQRForEmail(String email) throws Exception {
        return generateQRCode(email, "email-qr.png");
    }
}

