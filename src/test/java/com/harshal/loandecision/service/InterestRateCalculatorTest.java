package com.harshal.loandecision.service;

import com.harshal.loandecision.domain.EmploymentType;
import com.harshal.loandecision.domain.RiskBand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterestRateCalculatorTest {

    private final InterestRateCalculator calculator = new InterestRateCalculator();

    @Test
    void lowRiskSalariedSmallLoanGetsBaseRateOnly() {
        BigDecimal rate = calculator.calculate(RiskBand.LOW, EmploymentType.SALARIED, new BigDecimal("500000"));

        assertEquals(0, new BigDecimal("12.0").compareTo(rate));
    }

    @Test
    void mediumRiskAddsOnePointFivePercent() {
        BigDecimal rate = calculator.calculate(RiskBand.MEDIUM, EmploymentType.SALARIED, new BigDecimal("500000"));

        assertEquals(0, new BigDecimal("13.5").compareTo(rate));
    }

    @Test
    void highRiskAddsThreePercent() {
        BigDecimal rate = calculator.calculate(RiskBand.HIGH, EmploymentType.SALARIED, new BigDecimal("500000"));

        assertEquals(0, new BigDecimal("15.0").compareTo(rate));
    }

    @Test
    void selfEmployedAddsOnePercent() {
        BigDecimal rate = calculator.calculate(RiskBand.LOW, EmploymentType.SELF_EMPLOYED, new BigDecimal("500000"));

        assertEquals(0, new BigDecimal("13.0").compareTo(rate));
    }

    @Test
    void loanAboveTenLakhAddsHalfPercent() {
        BigDecimal rate = calculator.calculate(RiskBand.LOW, EmploymentType.SALARIED, new BigDecimal("1000001"));

        assertEquals(0, new BigDecimal("12.5").compareTo(rate));
    }

    @Test
    void loanOfExactlyTenLakhGetsNoLoanSizePremium() {
        BigDecimal rate = calculator.calculate(RiskBand.LOW, EmploymentType.SALARIED, new BigDecimal("1000000"));

        assertEquals(0, new BigDecimal("12.0").compareTo(rate));
    }

    @Test
    void allPremiumsAreAddedTogether() {
        BigDecimal rate = calculator.calculate(RiskBand.HIGH, EmploymentType.SELF_EMPLOYED, new BigDecimal("2000000"));

        assertEquals(0, new BigDecimal("16.5").compareTo(rate));
    }
}