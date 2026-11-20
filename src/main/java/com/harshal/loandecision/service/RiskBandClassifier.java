package com.harshal.loandecision.service;

import com.harshal.loandecision.domain.RiskBand;
import org.springframework.stereotype.Service;

@Service
public class RiskBandClassifier {

    private static final int LOW_RISK_MIN_SCORE = 750;
    private static final int MEDIUM_RISK_MIN_SCORE = 650;
    private static final int HIGH_RISK_MIN_SCORE = 600;

    public RiskBand classify(int creditScore) {
        if (creditScore >= LOW_RISK_MIN_SCORE) {
            return RiskBand.LOW;
        }
        if (creditScore >= MEDIUM_RISK_MIN_SCORE) {
            return RiskBand.MEDIUM;
        }
        if (creditScore >= HIGH_RISK_MIN_SCORE) {
            return RiskBand.HIGH;
        }
        throw new IllegalArgumentException(
                "Credit score below 600 cannot be assigned a risk band: " + creditScore);
    }
}