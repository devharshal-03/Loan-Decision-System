package com.harshal.loandecision.service;

import com.harshal.loandecision.domain.LoanDecision;
import com.harshal.loandecision.domain.LoanOffer;
import com.harshal.loandecision.domain.RejectionReason;
import com.harshal.loandecision.domain.RiskBand;
import com.harshal.loandecision.dto.ApplicantRequest;
import com.harshal.loandecision.dto.LoanApplicationRequest;
import com.harshal.loandecision.dto.LoanRequest;
import com.harshal.loandecision.repository.LoanDecisionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LoanDecisionService {

    private final RiskBandClassifier riskBandClassifier;
    private final EmiCalculator emiCalculator;
    private final InterestRateCalculator interestRateCalculator;
    private final EligibilityEvaluator eligibilityEvaluator;
    private final LoanDecisionRepository loanDecisionRepository;

    public LoanDecisionService(RiskBandClassifier riskBandClassifier,
                               EmiCalculator emiCalculator,
                               InterestRateCalculator interestRateCalculator,
                               EligibilityEvaluator eligibilityEvaluator,
                               LoanDecisionRepository loanDecisionRepository) {
        this.riskBandClassifier = riskBandClassifier;
        this.emiCalculator = emiCalculator;
        this.interestRateCalculator = interestRateCalculator;
        this.eligibilityEvaluator = eligibilityEvaluator;
        this.loanDecisionRepository = loanDecisionRepository;
    }

    public LoanDecision evaluate(LoanApplicationRequest request) {
        LoanDecision decision = decide(request);
        loanDecisionRepository.save(decision);
        return decision;
    }

    private LoanDecision decide(LoanApplicationRequest request) {
        ApplicantRequest applicant = request.applicant();
        LoanRequest loan = request.loan();
        UUID applicationId = UUID.randomUUID();

        List<RejectionReason> reasons = new ArrayList<>(
                eligibilityEvaluator.evaluateApplicant(applicant.creditScore(), applicant.age(), loan.tenureMonths()));

        // Without the minimum credit score there is no risk band, so no rate or EMI can be derived.
        if (reasons.contains(RejectionReason.CREDIT_SCORE_BELOW_MINIMUM)) {
            return LoanDecision.rejected(applicationId, reasons);
        }

        RiskBand riskBand = riskBandClassifier.classify(applicant.creditScore());
        BigDecimal interestRate = interestRateCalculator.calculate(riskBand, applicant.employmentType(), loan.amount());
        BigDecimal emi = emiCalculator.calculate(loan.amount(), interestRate, loan.tenureMonths());

        eligibilityEvaluator.evaluateEmi(emi, applicant.monthlyIncome()).ifPresent(reasons::add);

        if (!reasons.isEmpty()) {
            return LoanDecision.rejected(applicationId, reasons);
        }

        BigDecimal totalPayable = emi
                .multiply(BigDecimal.valueOf(loan.tenureMonths()))
                .setScale(2, RoundingMode.HALF_UP);

        LoanOffer offer = new LoanOffer(interestRate, loan.tenureMonths(), emi, totalPayable);
        return LoanDecision.approved(applicationId, riskBand, offer);
    }
}