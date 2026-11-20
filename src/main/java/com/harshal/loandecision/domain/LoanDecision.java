package com.harshal.loandecision.domain;

import java.util.List;
import java.util.UUID;

public record LoanDecision(
        UUID applicationId,
        ApplicationStatus status,
        RiskBand riskBand,
        LoanOffer offer,
        List<RejectionReason> rejectionReasons
) {

    public static LoanDecision approved(UUID applicationId, RiskBand riskBand, LoanOffer offer) {
        return new LoanDecision(applicationId, ApplicationStatus.APPROVED, riskBand, offer, null);
    }

    public static LoanDecision rejected(UUID applicationId, List<RejectionReason> rejectionReasons) {
        return new LoanDecision(applicationId, ApplicationStatus.REJECTED, null, null, List.copyOf(rejectionReasons));
    }
}