package com.harshal.loandecision.service;

import com.harshal.loandecision.domain.RiskBand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RiskBandClassifierTest {

    private final RiskBandClassifier classifier = new RiskBandClassifier();

    @Test
    void scoreOf750OrAboveIsLowRisk() {
        assertEquals(RiskBand.LOW, classifier.classify(750));
        assertEquals(RiskBand.LOW, classifier.classify(900));
    }

    @Test
    void scoreBetween650And749IsMediumRisk() {
        assertEquals(RiskBand.MEDIUM, classifier.classify(749));
        assertEquals(RiskBand.MEDIUM, classifier.classify(650));
    }

    @Test
    void scoreBetween600And649IsHighRisk() {
        assertEquals(RiskBand.HIGH, classifier.classify(649));
        assertEquals(RiskBand.HIGH, classifier.classify(600));
    }

    @Test
    void scoreBelow600CannotBeClassified() {
        assertThrows(IllegalArgumentException.class, () -> classifier.classify(599));
    }
}