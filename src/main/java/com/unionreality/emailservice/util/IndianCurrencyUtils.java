package com.unionreality.emailservice.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class IndianCurrencyUtils {

    private static final String[] ONES = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
    };
    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private IndianCurrencyUtils() {
    }

    public static String formatIndian(BigDecimal value) {
        BigDecimal rounded = value == null ? BigDecimal.ZERO : value.setScale(0, RoundingMode.HALF_UP);
        String digits = rounded.abs().toPlainString();
        String sign = rounded.signum() < 0 ? "-" : "";

        if (digits.length() <= 3) {
            return sign + digits;
        }

        String lastThree = digits.substring(digits.length() - 3);
        String other = digits.substring(0, digits.length() - 3);
        String grouped = other.replaceAll("(\\d)(?=(\\d{2})+(?!\\d))", "$1,");
        return sign + grouped + "," + lastThree;
    }

    public static String amountInWords(BigDecimal value) {
        long num = value == null ? 0 : value.setScale(0, RoundingMode.HALF_UP).longValue();
        if (num == 0) {
            return "Zero Rupees Only";
        }

        long crore = num / 10_000_000L;
        num %= 10_000_000L;
        long lakh = num / 100_000L;
        num %= 100_000L;
        long thousand = num / 1_000L;
        num %= 1_000L;
        long hundred = num;

        StringBuilder sb = new StringBuilder();
        if (crore > 0) {
            sb.append(twoDigits((int) crore)).append(" Crore");
        }
        if (lakh > 0) {
            appendPart(sb, twoDigits((int) lakh) + " Lakh");
        }
        if (thousand > 0) {
            appendPart(sb, twoDigits((int) thousand) + " Thousand");
        }
        if (hundred > 0) {
            appendPart(sb, threeDigits((int) hundred));
        }
        return sb.toString().trim() + " Rupees Only";
    }

    public static String displayAmount(BigDecimal value) {
        return "₹" + formatIndian(value);
    }

    public static String displayAmountWithWords(BigDecimal value) {
        return displayAmount(value) + " (" + amountInWords(value) + ")";
    }

    private static void appendPart(StringBuilder sb, String part) {
        if (!sb.isEmpty()) {
            sb.append(' ');
        }
        sb.append(part);
    }

    private static String twoDigits(int n) {
        if (n < 20) {
            return ONES[n];
        }
        int tens = n / 10;
        int ones = n % 10;
        return TENS[tens] + (ones > 0 ? " " + ONES[ones] : "");
    }

    private static String threeDigits(int n) {
        int hundreds = n / 100;
        int rest = n % 100;
        StringBuilder sb = new StringBuilder();
        if (hundreds > 0) {
            sb.append(ONES[hundreds]).append(" Hundred");
        }
        if (rest > 0) {
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(twoDigits(rest));
        }
        return sb.toString();
    }
}
