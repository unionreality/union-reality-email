package com.unionreality.emailservice.dto;

public record SendReceiptEmailResponse(
        boolean success,
        String message,
        String receiptNo
) {
}
