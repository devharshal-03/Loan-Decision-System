package com.harshal.loandecision.service;

import com.harshal.loandecision.domain.RejectionReason;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EligibilityEvaluatorTest {

    private final EligibilityEvaluator evaluator = new EligibilityEvaluator();

    private static final BigDecimal INCOME = new BigDecimal("50000");

    @Test
    void creditScoreBelow600IsRejected() {
        assertEquals(List.of(RejectionReason.CREDIT_SCORE_BELOW_MINIMUM),
                evaluator.evaluateApplicant(599, 30, 36));
    }

    @Test
    void creditScoreOf600IsAccepted() {
        assertEquals(List.of(), evaluator.evaluateApplicant(600, 30, 36));
    }

    @Test
    void agePlusTenureExactlyAt65YearsIsAccepted() {
        assertEquals(List.of(), evaluator.evaluateApplicant(700, 40, 300));
    }

    @Test
    void agePlusTenureOver65YearsIsRejected() {
        assertEquals(List.of(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED),
                evaluator.evaluateApplicant(700, 40, 301));
    }

    @Test
    void oldestApplicantWithLongerThanFiveYearTenureIsRejected() {
        assertEquals(List.of(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED),
                evaluator.evaluateApplicant(700, 60, 61));
    }

    @Test
    void allFailedApplicantRulesAreReported() {
        assertEquals(List.of(RejectionReason.CREDIT_SCORE_BELOW_MINIMUM, RejectionReason.AGE_TENURE_LIMIT_EXCEEDED),
                evaluator.evaluateApplicant(500, 60, 120));
    }

    @Test
    void emiWithinFiftyPercentOfIncomeIsAccepted() {
        assertEquals(Optional.empty(), evaluator.evaluateEmi(new BigDecimal("25000.00"), INCOME));
    }

    @Test
    void emiAboveFiftyPercentIsRejected() {
        assertEquals(Optional.of(RejectionReason.EMI_EXCEEDS_50_PERCENT),
                evaluator.evaluateEmi(new BigDecimal("25000.01"), INCOME));
    }

    @Test
    void emiExactlySixtyPercentOnlyFailsTheFiftyPercentRule() {
        assertEquals(Optional.of(RejectionReason.EMI_EXCEEDS_50_PERCENT),
                evaluator.evaluateEmi(new BigDecimal("30000.00"), INCOME));
    }

    @Test
    void emiAboveSixtyPercentIsRejected() {
        assertEquals(Optional.of(RejectionReason.EMI_EXCEEDS_60_PERCENT),
                evaluator.evaluateEmi(new BigDecimal("30000.01"), INCOME));
    }
}