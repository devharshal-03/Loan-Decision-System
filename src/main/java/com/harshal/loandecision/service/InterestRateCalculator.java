package com.harshal.loandecision.service;

import com.harshal.loandecision.domain.EmploymentType;
import com.harshal.loandecision.domain.RiskBand;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InterestRateCalculator {

    private static final BigDecimal BASE_RATE = new BigDecimal("12.0");

    private static final BigDecimal MEDIUM_RISK_PREMIUM = new BigDecimal("1.5");
    private static final BigDecimal HIGH_RISK_PREMIUM = new BigDecimal("3.0");

    private static final BigDecimal SELF_EMPLOYED_PREMIUM = new BigDecimal("1.0");

    private static final BigDecimal LARGE_LOAN_THRESHOLD = new BigDecimal("1000000");
    private static final BigDecimal LARGE_LOAN_PREMIUM = new BigDecimal("0.5");

    public BigDecimal calculate(RiskBand riskBand, EmploymentType employmentType, BigDecimal loanAmount) {
        return BASE_RATE
                .add(riskPremium(riskBand))
                .add(employmentPremium(employmentType))
                .add(loanSizePremium(loanAmount));
    }

    private BigDecimal riskPremium(RiskBand riskBand) {
        return switch (riskBand) {
            case LOW -> BigDecimal.ZERO;
            case MEDIUM -> MEDIUM_RISK_PREMIUM;
            case HIGH -> HIGH_RISK_PREMIUM;
        };
    }

    private BigDecimal employmentPremium(EmploymentType employmentType) {
        return switch (employmentType) {
            case SALARIED -> BigDecimal.ZERO;
            case SELF_EMPLOYED -> SELF_EMPLOYED_PREMIUM;
        };
    }

    private BigDecimal loanSizePremium(BigDecimal loanAmount) {
        if (loanAmount.compareTo(LARGE_LOAN_THRESHOLD) > 0) {
            return LARGE_LOAN_PREMIUM;
        }
        return BigDecimal.ZERO;
    }
}