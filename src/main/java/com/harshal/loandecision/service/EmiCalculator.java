package com.harshal.loandecision.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class EmiCalculator {

    private static final BigDecimal MONTHS_PER_YEAR = BigDecimal.valueOf(12);
    private static final BigDecimal PERCENT = BigDecimal.valueOf(100);
    private static final MathContext PRECISION = MathContext.DECIMAL128;

    public BigDecimal calculate(BigDecimal principal, BigDecimal annualRatePercent, int tenureMonths) {
        BigDecimal monthlyRate = annualRatePercent
                .divide(PERCENT, PRECISION)
                .divide(MONTHS_PER_YEAR, PRECISION);

        BigDecimal growthFactor = BigDecimal.ONE.add(monthlyRate).pow(tenureMonths, PRECISION);

        BigDecimal emi = principal
                .multiply(monthlyRate)
                .multiply(growthFactor)
                .divide(growthFactor.subtract(BigDecimal.ONE), PRECISION);

        return emi.setScale(2, RoundingMode.HALF_UP);
    }
}