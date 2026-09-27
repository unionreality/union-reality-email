package com.unionreality.emailservice.dto;

public record PaymentLineView(
        String description,
        String amountFormatted
) {
}
