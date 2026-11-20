package com.harshal.loandecision.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmiCalculatorTest {

    private final EmiCalculator calculator = new EmiCalculator();

    @Test
    void calculatesEmiForThreeYearLoanAtTwelvePercent() {
        BigDecimal emi = calculator.calculate(new BigDecimal("500000"), new BigDecimal("12"), 36);

        assertEquals(new BigDecimal("16607.15"), emi);
    }

    @Test
    void calculatesEmiWhenRateHasDecimalPart() {
        BigDecimal emi = calculator.calculate(new BigDecimal("500000"), new BigDecimal("13.5"), 36);

        assertEquals(new BigDecimal("16967.64"), emi);
    }

    @Test
    void calculatesEmiForLongTenure() {
        BigDecimal emi = calculator.calculate(new BigDecimal("1000000"), new BigDecimal("12"), 120);

        assertEquals(new BigDecimal("14347.09"), emi);
    }

    @Test
    void calculatesEmiForShortestTenureAndSmallAmount() {
        BigDecimal emi = calculator.calculate(new BigDecimal("10000"), new BigDecimal("12"), 6);

        assertEquals(new BigDecimal("1725.48"), emi);
    }

    @Test
    void roundsHalfUpToTwoDecimalPlaces() {
        BigDecimal emi = calculator.calculate(new BigDecimal("100000"), new BigDecimal("12"), 12);

        assertEquals(new BigDecimal("8884.88"), emi);
        assertEquals(2, emi.scale());
    }
}