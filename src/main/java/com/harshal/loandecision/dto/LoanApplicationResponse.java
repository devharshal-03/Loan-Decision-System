package com.harshal.loandecision.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.harshal.loandecision.domain.ApplicationStatus;
import com.harshal.loandecision.domain.LoanDecision;
import com.harshal.loandecision.domain.LoanOffer;
import com.harshal.loandecision.domain.RejectionReason;
import com.harshal.loandecision.domain.RiskBand;

import java.util.List;
import java.util.UUID;

public record LoanApplicationResponse(
        UUID applicationId,
        ApplicationStatus status,
        RiskBand riskBand,
        @JsonInclude(JsonInclude.Include.NON_NULL) LoanOffer offer,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<RejectionReason> rejectionReasons
) {

    public static LoanApplicationResponse from(LoanDecision decision) {
        return new LoanApplicationResponse(
                decision.applicationId(),
                decision.status(),
                decision.riskBand(),
                decision.offer(),
                decision.rejectionReasons());
    }
}