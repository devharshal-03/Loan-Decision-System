package com.harshal.loandecision.repository;

import com.harshal.loandecision.domain.LoanDecision;
import com.harshal.loandecision.domain.RejectionReason;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanDecisionRepositoryTest {

    private final LoanDecisionRepository repository = new LoanDecisionRepository();

    @Test
    void savedDecisionCanBeFoundById() {
        LoanDecision decision = LoanDecision.rejected(
                UUID.randomUUID(), List.of(RejectionReason.CREDIT_SCORE_BELOW_MINIMUM));

        repository.save(decision);

        assertEquals(Optional.of(decision), repository.findById(decision.applicationId()));
    }

    @Test
    void unknownIdReturnsEmpty() {
        assertEquals(Optional.empty(), repository.findById(UUID.randomUUID()));
    }
}