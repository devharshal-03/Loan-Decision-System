package com.harshal.loandecision.service;

import com.harshal.loandecision.domain.RejectionReason;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EligibilityEvaluator {

    private static final int MINIMUM_CREDIT_SCORE = 600;
    private static final int MONTHS_PER_YEAR = 12;
    private static final int MAX_AGE_PLUS_TENURE_YEARS = 65;
    private static final int MAX_AGE_PLUS_TENURE_MONTHS = MAX_AGE_PLUS_TENURE_YEARS * MONTHS_PER_YEAR;

    private static final BigDecimal EMI_REJECTION_RATIO = new BigDecimal("0.60");
    private static final BigDecimal EMI_OFFER_RATIO = new BigDecimal("0.50");

    public List<RejectionReason> evaluateApplicant(int creditScore, int age, int tenureMonths) {
        List<RejectionReason> reasons = new ArrayList<>();

        if (creditScore < MINIMUM_CREDIT_SCORE) {
            reasons.add(RejectionReason.CREDIT_SCORE_BELOW_MINIMUM);
        }
        if (age * MONTHS_PER_YEAR + tenureMonths > MAX_AGE_PLUS_TENURE_MONTHS) {
            reasons.add(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED);
        }
        return reasons;
    }

    public Optional<RejectionReason> evaluateEmi(BigDecimal emi, BigDecimal monthlyIncome) {
        if (emi.compareTo(monthlyIncome.multiply(EMI_REJECTION_RATIO)) > 0) {
            return Optional.of(RejectionReason.EMI_EXCEEDS_60_PERCENT);
        }
        if (emi.compareTo(monthlyIncome.multiply(EMI_OFFER_RATIO)) > 0) {
            return Optional.of(RejectionReason.EMI_EXCEEDS_50_PERCENT);
        }
        return Optional.empty();
    }
}