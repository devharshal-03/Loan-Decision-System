package com.harshal.loandecision.repository;

import com.harshal.loandecision.domain.LoanDecision;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LoanDecisionRepository {

    private final Map<UUID, LoanDecision> decisions = new ConcurrentHashMap<>();

    public LoanDecision save(LoanDecision decision) {
        decisions.put(decision.applicationId(), decision);
        return decision;
    }

    public Optional<LoanDecision> findById(UUID applicationId) {
        return Optional.ofNullable(decisions.get(applicationId));
    }
}