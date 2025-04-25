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
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            InputStream responseStream = connection.getInputStream();

            // Step 2: Parse JSON to List of Employee objects (or Map if dynamic)
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> employeeList = objectMapper.readValue(responseStream, new TypeReference<>() {});
            responseStream.close();

            // Step 3: Create PDF Document
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(pdfOutputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Employee Details")
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER)
            );

            if (!employeeList.isEmpty()) {
                Map<String, Object> firstEmployee = employeeList.get(0);
                Table employeeTable = new Table(UnitValue.createPercentArray(firstEmployee.size())).useAllAvailableWidth();

                // Add table headers
                for (String fieldName : firstEmployee.keySet()) {
                    employeeTable.addHeaderCell(
                            new Cell().add(new Paragraph(fieldName).setBold()).setBorder(Border.NO_BORDER)
                    );
                }

                // Add employee data rows
                for (Map<String, Object> employee : employeeList) {
                    for (Object fieldValue : employee.values()) {
                        employeeTable.addCell(
                                new Cell().add(new Paragraph(fieldValue.toString())).setBorder(Border.NO_BORDER)
                        );
                    }
                }

                document.add(employeeTable);
            }

            document.close();
            return pdfOutputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF from employee data", e);
        }
    }
}
