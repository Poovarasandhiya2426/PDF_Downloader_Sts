package com.PDF.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.PDF.Service.PdfService;



@RestController
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/download-pdf")
    public ResponseEntity<ByteArrayResource> downloadPdf() {
        byte[] pdfBytes = pdfService.generatePdfFromApi();

        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("Employee Details_Stats.pdf")
                .build());

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}

