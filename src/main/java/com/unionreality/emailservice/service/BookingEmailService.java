package com.unionreality.emailservice.service;

import com.unionreality.emailservice.config.EmailServiceProperties;
import com.unionreality.emailservice.dto.ReceiptViewModel;
import com.unionreality.emailservice.dto.SendReceiptEmailRequest;
import com.unionreality.emailservice.util.IndianCurrencyUtils;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookingEmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailServiceProperties properties;
    private final PdfReceiptService pdfReceiptService;
    private final ReceiptModelBuilder modelBuilder;

    public BookingEmailService(
            JavaMailSender mailSender,
            TemplateEngine templateEngine,
            EmailServiceProperties properties,
            PdfReceiptService pdfReceiptService,
            ReceiptModelBuilder modelBuilder
    ) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.properties = properties;
        this.pdfReceiptService = pdfReceiptService;
        this.modelBuilder = modelBuilder;
    }

    public void sendBookingReceipt(SendReceiptEmailRequest request) throws Exception {
        ReceiptViewModel model = modelBuilder.build(request);
        BigDecimal bookingAmount = modelBuilder.resolveBookingAmount(request, model);
        byte[] pdfBytes = pdfReceiptService.generatePdf(model);

        String subject = buildSubject(model);
        String htmlBody = renderEmailBody(request, model, bookingAmount);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setFrom(properties.fromAddress(), properties.fromName());
        helper.setTo(distinctEmails(request.toEmails()).toArray(String[]::new));
        helper.setSubject(subject);

        if (!properties.ccList().isEmpty()) {
            helper.setCc(properties.ccList().toArray(String[]::new));
        }
        helper.setBcc(properties.bccChaitra());

        helper.setText(htmlBody, true);

        String attachmentName = (StringUtils.hasText(model.receiptNo()) ? model.receiptNo().trim() : "payment-receipt")
                + ".pdf";
        helper.addAttachment(
                attachmentName,
                () -> new ByteArrayInputStream(pdfBytes),
                "application/pdf"
        );

        mailSender.send(message);
    }

    private String renderEmailBody(
            SendReceiptEmailRequest request,
            ReceiptViewModel model,
            BigDecimal bookingAmount
    ) {
        Context context = new Context();
        context.setVariable("clientName", displayName(model));
        context.setVariable("project", model.project());
        context.setVariable("siteNo", model.siteNo());
        context.setVariable("measurement", model.measurement());
        context.setVariable("facing", model.facing());
        context.setVariable("bookingAmountDisplay", IndianCurrencyUtils.displayAmountWithWords(bookingAmount));

        EmailServiceProperties.ContactProperties contact = properties.contact();
        context.setVariable("whatsappUrl", contact.whatsappUrl());
        context.setVariable("websiteUrl", contact.websiteUrl());
        context.setVariable("mapsUrl", contact.mapsUrl());
        context.setVariable("salesEmail", contact.salesEmail());
        context.setVariable("phoneDisplay", contact.phoneDisplay());
        context.setVariable("postalLine1", contact.postalLine1());
        context.setVariable("postalLine2", contact.postalLine2());
        context.setVariable("postalLine3", contact.postalLine3());
        context.setVariable("postalLine4", contact.postalLine4());
        context.setVariable("companyName", properties.receipt().companyName());
        return templateEngine.process("booking-email", context);
    }

    private String buildSubject(ReceiptViewModel model) {
        String prefix = StringUtils.hasText(properties.emailSubjectPrefix())
                ? properties.emailSubjectPrefix().trim()
                : "Booking confirmation";
        if (StringUtils.hasText(model.project())) {
            return prefix + " – " + model.project().trim();
        }
        return prefix + " – " + properties.receipt().companyName();
    }

    private static String displayName(ReceiptViewModel model) {
        if (!StringUtils.hasText(model.clientName())) {
            return "Customer";
        }
        if (StringUtils.hasText(model.salutation())) {
            return model.clientName().trim();
        }
        return model.clientName().trim();
    }

    private static Set<String> distinctEmails(List<String> emails) {
        Set<String> set = new LinkedHashSet<>();
        for (String email : emails) {
            if (StringUtils.hasText(email)) {
                set.add(email.trim());
            }
        }
        return set;
    }
}
