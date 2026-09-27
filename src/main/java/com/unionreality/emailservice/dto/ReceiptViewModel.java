package com.unionreality.emailservice.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReceiptViewModel(
        String receiptNo,
        String date,
        String project,
        String siteNo,
        String measurement,
        String facing,
        String location,
        String clientName,
        String salutation,
        String mobile,
        String customerEmail,
        String address,
        List<PaymentLineView> items,
        BigDecimal total,
        String totalFormatted,
        String amountInWords,
        String logoDataUri,
        String companyName,
        String companyAddress,
        String companyPhone,
        String companyEmail
) {
}
