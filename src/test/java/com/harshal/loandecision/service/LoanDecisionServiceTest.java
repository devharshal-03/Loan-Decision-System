package com.harshal.loandecision.service;

import com.harshal.loandecision.domain.ApplicationStatus;
import com.harshal.loandecision.domain.EmploymentType;
import com.harshal.loandecision.domain.LoanDecision;
import com.harshal.loandecision.domain.LoanPurpose;
import com.harshal.loandecision.domain.RejectionReason;
import com.harshal.loandecision.domain.RiskBand;
import com.harshal.loandecision.dto.ApplicantRequest;
import com.harshal.loandecision.dto.LoanApplicationRequest;
import com.harshal.loandecision.dto.LoanRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoanDecisionServiceTest {

    private final LoanDecisionService service = new LoanDecisionService(
            new RiskBandClassifier(),
            new EmiCalculator(),
            new InterestRateCalculator(),
            new EligibilityEvaluator());

    @Test
    void approvesEligibleApplicationWithSingleOffer() {
        LoanDecision decision = service.evaluate(request(30, "75000", 720, "500000", 36));

        assertNotNull(decision.applicationId());
        assertEquals(ApplicationStatus.APPROVED, decision.status());
        assertEquals(RiskBand.MEDIUM, decision.riskBand());
        assertEquals(0, new BigDecimal("13.5").compareTo(decision.offer().interestRate()));
        assertEquals(36, decision.offer().tenureMonths());
        assertEquals(new BigDecimal("16967.64"), decision.offer().emi());
        assertEquals(new BigDecimal("610835.04"), decision.offer().totalPayable());
        assertNull(decision.rejectionReasons());
    }

    @Test
    void rejectsLowCreditScoreWithoutRiskBandOrOffer() {
        LoanDecision decision = service.evaluate(request(30, "75000", 550, "500000", 36));

        assertEquals(ApplicationStatus.REJECTED, decision.status());
        assertNull(decision.riskBand());
        assertNull(decision.offer());
        assertEquals(List.of(RejectionReason.CREDIT_SCORE_BELOW_MINIMUM), decision.rejectionReasons());
    }

    @Test
    void rejectsWhenEmiExceedsSixtyPercentOfIncome() {
        LoanDecision decision = service.evaluate(request(30, "20000", 720, "500000", 36));

        assertEquals(ApplicationStatus.REJECTED, decision.status());
        assertEquals(List.of(RejectionReason.EMI_EXCEEDS_60_PERCENT), decision.rejectionReasons());
    }

    @Test
    void rejectsWhenEmiIsAboveFiftyButNotAboveSixtyPercentOfIncome() {
        LoanDecision decision = service.evaluate(request(30, "30000", 720, "500000", 36));

        assertEquals(ApplicationStatus.REJECTED, decision.status());
        assertEquals(List.of(RejectionReason.EMI_EXCEEDS_50_PERCENT), decision.rejectionReasons());
    }

    @Test
    void reportsAgeTenureAndEmiReasonsTogether() {
        LoanDecision decision = service.evaluate(request(60, "100000", 720, "5000000", 120));

        assertEquals(ApplicationStatus.REJECTED, decision.status());
        assertEquals(
                List.of(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED, RejectionReason.EMI_EXCEEDS_60_PERCENT),
                decision.rejectionReasons());
    }

    private LoanApplicationRequest request(int age, String income, int creditScore, String amount, int tenureMonths) {
        return new LoanApplicationRequest(
                new ApplicantRequest("Test Applicant", age, new BigDecimal(income), EmploymentType.SALARIED, creditScore),
                new LoanRequest(new BigDecimal(amount), tenureMonths, LoanPurpose.PERSONAL));
    }
}