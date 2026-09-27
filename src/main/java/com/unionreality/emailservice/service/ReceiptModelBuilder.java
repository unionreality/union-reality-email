package com.unionreality.emailservice.service;

import com.unionreality.emailservice.config.EmailServiceProperties;
import com.unionreality.emailservice.dto.PaymentItemDto;
import com.unionreality.emailservice.dto.PaymentLineView;
import com.unionreality.emailservice.dto.ReceiptViewModel;
import com.unionreality.emailservice.dto.SendReceiptEmailRequest;
import com.unionreality.emailservice.util.IndianCurrencyUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Component
public class ReceiptModelBuilder {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

    private final String logoDataUri;
    private final EmailServiceProperties.ReceiptCompanyProperties company;

    public ReceiptModelBuilder(EmailServiceProperties properties) throws IOException {
        ClassPathResource logo = new ClassPathResource("static/images/urc-logo.png");
        byte[] bytes = StreamUtils.copyToByteArray(logo.getInputStream());
        this.logoDataUri = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        this.company = properties.receipt();
    }

    public ReceiptViewModel build(SendReceiptEmailRequest request) {
        List<PaymentItemDto> rawItems = normalizeItems(request);
        List<PaymentLineView> items = rawItems.stream()
                .map(item -> new PaymentLineView(
                        item.description(),
                        IndianCurrencyUtils.formatIndian(item.amount())
                ))
                .toList();
        BigDecimal total = rawItems.stream()
                .map(PaymentItemDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String date = StringUtils.hasText(request.date())
                ? request.date().trim()
                : LocalDate.now().format(DATE_FMT);

        return new ReceiptViewModel(
                blankToNull(request.receiptNo()),
                date,
                blankToNull(request.project()),
                blankToNull(request.siteNo()),
                blankToNull(request.measurement()),
                blankToNull(request.facing()),
                blankToNull(request.location()),
                blankToNull(request.clientName()),
                defaultSalutation(request.salutation()),
                blankToNull(request.mobile()),
                blankToNull(request.customerEmail()),
                blankToNull(request.address()),
                items,
                total,
                IndianCurrencyUtils.formatIndian(total),
                IndianCurrencyUtils.amountInWords(total),
                logoDataUri,
                company.companyName(),
                company.companyAddress(),
                company.companyPhone(),
                company.companyEmail()
        );
    }

    public BigDecimal resolveBookingAmount(SendReceiptEmailRequest request, ReceiptViewModel model) {
        if (request.bookingAmount() != null && request.bookingAmount().signum() > 0) {
            return request.bookingAmount().setScale(0, java.math.RoundingMode.HALF_UP);
        }
        return model.total();
    }

    private List<PaymentItemDto> normalizeItems(SendReceiptEmailRequest request) {
        List<PaymentItemDto> items = new ArrayList<>();
        if (request.items() != null) {
            for (PaymentItemDto item : request.items()) {
                if (item == null) {
                    continue;
                }
                BigDecimal amount = item.amount() == null ? BigDecimal.ZERO : item.amount();
                if (!StringUtils.hasText(item.description()) && amount.signum() == 0) {
                    continue;
                }
                items.add(new PaymentItemDto(
                        StringUtils.hasText(item.description()) ? item.description().trim() : "Booking amount",
                        amount
                ));
            }
        }
        if (items.isEmpty() && request.bookingAmount() != null && request.bookingAmount().signum() > 0) {
            items.add(new PaymentItemDto("Booking amount", request.bookingAmount()));
        }
        if (items.isEmpty()) {
            items.add(new PaymentItemDto("Booking amount", BigDecimal.ZERO));
        }
        return items;
    }

    private static String defaultSalutation(String salutation) {
        return StringUtils.hasText(salutation) ? salutation.trim() : "Mr.";
    }

    private static String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
