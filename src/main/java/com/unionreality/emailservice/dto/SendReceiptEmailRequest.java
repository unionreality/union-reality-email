package com.unionreality.emailservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

public record SendReceiptEmailRequest(
        @NotEmpty(message = "At least one recipient email is required")
        List<@Email String> toEmails,
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
        List<PaymentItemDto> items,
        BigDecimal bookingAmount
) {
}
