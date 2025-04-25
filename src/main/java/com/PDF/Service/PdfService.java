package com.PDF.Service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.borders.Border;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
public class PdfService {

    public byte[] generatePdfFromApi() {
        try {
            // Step 1: Call API
            String apiUrl = "http://localhost:8056/getListObj";
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            InputStream inputStream = conn.getInputStream();

            // Step 2: Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> players = mapper.readValue(inputStream, new TypeReference<>() {});
            inputStream.close();

            // Step 3: Generate PDF
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Employee Details")
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER)
            );

            if (!players.isEmpty()) {
                Map<String, Object> firstPlayer = players.get(0);
                Table table = new Table(UnitValue.createPercentArray(firstPlayer.size())).useAllAvailableWidth();

                // Header
                for (String key : firstPlayer.keySet()) {
                    table.addHeaderCell(new Cell().add(new Paragraph(key).setBold()).setBorder(Border.NO_BORDER));
                }

                // Data
                for (Map<String, Object> player : players) {
                    for (Object value : player.values()) {
                        table.addCell(new Cell().add(new Paragraph(value.toString())).setBorder(Border.NO_BORDER));
                    }
                }

                document.add(table);
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
