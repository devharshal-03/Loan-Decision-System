package com.harshal.loandecision.domain;

import java.math.BigDecimal;

public record LoanOffer(
        BigDecimal interestRate,
        int tenureMonths,
        BigDecimal emi,
        BigDecimal totalPayable
) {
}