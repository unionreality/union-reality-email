package com.unionreality.emailservice.controller;

import com.unionreality.emailservice.dto.ReceiptViewModel;
import com.unionreality.emailservice.dto.SendReceiptEmailRequest;
import com.unionreality.emailservice.dto.SendReceiptEmailResponse;
import com.unionreality.emailservice.service.BookingEmailService;
import com.unionreality.emailservice.service.PdfReceiptService;
import com.unionreality.emailservice.service.ReceiptModelBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/receipt")
public class ReceiptEmailController {

    private final BookingEmailService bookingEmailService;
    private final PdfReceiptService pdfReceiptService;
    private final ReceiptModelBuilder modelBuilder;

    public ReceiptEmailController(
            BookingEmailService bookingEmailService,
            PdfReceiptService pdfReceiptService,
            ReceiptModelBuilder modelBuilder
    ) {
        this.bookingEmailService = bookingEmailService;
        this.pdfReceiptService = pdfReceiptService;
        this.modelBuilder = modelBuilder;
    }

    @PostMapping("/send-email")
    public SendReceiptEmailResponse sendEmail(@Valid @RequestBody SendReceiptEmailRequest request) throws Exception {
        ReceiptViewModel model = modelBuilder.build(request);
        bookingEmailService.sendBookingReceipt(request);
        return new SendReceiptEmailResponse(
                true,
                "Receipt email sent successfully.",
                model.receiptNo()
        );
    }

    @PostMapping("/pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody SendReceiptEmailRequest request) {
        ReceiptViewModel model = modelBuilder.build(request);
        byte[] pdf = pdfReceiptService.generatePdf(model);
        String fileName = (model.receiptNo() != null ? model.receiptNo() : "payment-receipt") + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
