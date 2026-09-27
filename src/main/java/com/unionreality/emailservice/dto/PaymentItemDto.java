package com.unionreality.emailservice.dto;

import java.math.BigDecimal;

public record PaymentItemDto(
        String description,
        BigDecimal amount
) {
}
