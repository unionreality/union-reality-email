package com.unionreality.emailservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "email-service")
public record EmailServiceProperties(
        String fromAddress,
        String fromName,
        String ccAddresses,
        String bccChaitra,
        String corsAllowedOrigins,
        String emailSubjectPrefix,
        ContactProperties contact,
        ReceiptCompanyProperties receipt
) {
    public List<String> ccList() {
        if (ccAddresses == null || ccAddresses.isBlank()) {
            return List.of();
        }
        return Arrays.stream(ccAddresses.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public record ContactProperties(
            String websiteUrl,
            String whatsappUrl,
            String mapsUrl,
            String phoneDisplay,
            String salesEmail,
            String postalLine1,
            String postalLine2,
            String postalLine3,
            String postalLine4
    ) {}

    public record ReceiptCompanyProperties(
            String companyName,
            String companyAddress,
            String companyPhone,
            String companyEmail,
            String companyGstin
    ) {}
}
