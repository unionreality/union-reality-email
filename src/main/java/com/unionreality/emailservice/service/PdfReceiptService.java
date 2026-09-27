package com.unionreality.emailservice.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.unionreality.emailservice.dto.ReceiptViewModel;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Service
public class PdfReceiptService {

    private final TemplateEngine templateEngine;

    public PdfReceiptService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generatePdf(ReceiptViewModel model) {
        Context context = new Context();
        context.setVariable("receipt", model);
        String html = templateEngine.process("receipt-pdf", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate receipt PDF", ex);
        }
    }
}
