package com.pestrisk;

import com.pestrisk.dto.RiskAssessmentRequestDto;
import com.pestrisk.dto.RiskAssessmentResponseDto;
import com.pestrisk.enums.RiskLevel;
import com.pestrisk.enums.WasteQuantity;
import com.pestrisk.service.RiskEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class RiskEngineTest {

    private RiskEngine riskEngine;

    @BeforeEach
    void setUp() {
        riskEngine = new RiskEngine();
    }

    @Test
    @DisplayName("Maximum possible score reaches exactly 100 with CRITICAL risk level")
    void testMaximumScore() {
        RiskAssessmentRequestDto maxRequest = new RiskAssessmentRequestDto(
                WasteQuantity.HIGH, // 15
                5,                  // 15 (>= 4 days)
                true,               // 20
                true,               // 15
                true,               // 15
                true,               // 10
                true                // 10
        );

        RiskAssessmentResponseDto response = riskEngine.calculateRisk(maxRequest);

        assertEquals(100, response.getScore());
        assertEquals(RiskLevel.CRITICAL, response.getRiskLevel());
        assertNotNull(response.getExplanation());
        assertEquals(7, response.getContributingFactors().size());
    }

    @Test
    @DisplayName("Minimum baseline score with LOW waste and no hazards produces 5 points and LOW risk")
    void testMinimumScore() {
        RiskAssessmentRequestDto minRequest = new RiskAssessmentRequestDto(
                WasteQuantity.LOW, // 5
                0,                 // 0
                false,             // 0
                false,             // 0
                false,             // 0
                false,             // 0
                false              // 0
        );

        RiskAssessmentResponseDto response = riskEngine.calculateRisk(minRequest);

        assertEquals(5, response.getScore());
        assertEquals(RiskLevel.LOW, response.getRiskLevel());
    }

    @ParameterizedTest
    @CsvSource({
            "LOW, 5",
            "MEDIUM, 10",
            "HIGH, 15"
    })
    @DisplayName("Waste quantity contributes correct points (LOW=5, MEDIUM=10, HIGH=15)")
    void testWasteQuantityScoring(WasteQuantity quantity, int expectedScore) {
        RiskAssessmentRequestDto req = new RiskAssessmentRequestDto(
                quantity, 0, false, false, false, false, false
        );
        RiskAssessmentResponseDto res = riskEngine.calculateRisk(req);
        assertEquals(expectedScore, res.getScore());
    }

    @ParameterizedTest
    @CsvSource({
            "0, 5",   // LOW (5) + 0 days (0) = 5
            "1, 5",   // LOW (5) + 1 days (0) = 5
            "2, 13",  // LOW (5) + 2 days (8) = 13
            "3, 13",  // LOW (5) + 3 days (8) = 13
            "4, 20",  // LOW (5) + 4 days (15) = 20
            "10, 20"  // LOW (5) + 10 days (15) = 20
    })
    @DisplayName("Waste duration tiers contribute correct points (0-1d=0, 2-3d=8, 4+d=15)")
    void testWasteDurationTiers(int durationDays, int expectedScore) {
        RiskAssessmentRequestDto req = new RiskAssessmentRequestDto(
                WasteQuantity.LOW, durationDays, false, false, false, false, false
        );
        RiskAssessmentResponseDto res = riskEngine.calculateRisk(req);
        assertEquals(expectedScore, res.getScore());
    }

    @Test
    @DisplayName("Stagnant water adds exactly 20 points")
    void testStagnantWaterPoints() {
        RiskAssessmentRequestDto req = new RiskAssessmentRequestDto(
                WasteQuantity.LOW, 0, true, false, false, false, false
        );
        RiskAssessmentResponseDto res = riskEngine.calculateRisk(req);
        assertEquals(25, res.getScore()); // 5 + 20
        assertEquals(RiskLevel.LOW, res.getRiskLevel()); // 25 is upper boundary of LOW
    }

    @Test
    @DisplayName("Mosquito sightings add exactly 15 points")
    void testMosquitoPoints() {
        RiskAssessmentRequestDto req = new RiskAssessmentRequestDto(
                WasteQuantity.LOW, 0, false, true, false, false, false
        );
        RiskAssessmentResponseDto res = riskEngine.calculateRisk(req);
        assertEquals(20, res.getScore()); // 5 + 15
    }

    @Test
    @DisplayName("Rodent sightings add exactly 15 points")
    void testRodentPoints() {
        RiskAssessmentRequestDto req = new RiskAssessmentRequestDto(
                WasteQuantity.LOW, 0, false, false, true, false, false
        );
        RiskAssessmentResponseDto res = riskEngine.calculateRisk(req);
        assertEquals(20, res.getScore()); // 5 + 15
    }

    @Test
    @DisplayName("Stray animal sightings add exactly 10 points")
    void testStrayAnimalsPoints() {
        RiskAssessmentRequestDto req = new RiskAssessmentRequestDto(
                WasteQuantity.LOW, 0, false, false, false, true, false
        );
        RiskAssessmentResponseDto res = riskEngine.calculateRisk(req);
        assertEquals(15, res.getScore()); // 5 + 10
    }

    @Test
    @DisplayName("Unsegregated waste adds exactly 10 points")
    void testUnsegregatedWastePoints() {
        RiskAssessmentRequestDto req = new RiskAssessmentRequestDto(
                WasteQuantity.LOW, 0, false, false, false, false, true
        );
        RiskAssessmentResponseDto res = riskEngine.calculateRisk(req);
        assertEquals(15, res.getScore()); // 5 + 10
    }

    @ParameterizedTest
    @CsvSource({
            "0, LOW",
            "25, LOW",
            "26, MODERATE",
            "50, MODERATE",
            "51, HIGH",
            "75, HIGH",
            "76, CRITICAL",
            "100, CRITICAL"
    })
    @DisplayName("RiskLevel boundary mapping: 0-25 LOW, 26-50 MODERATE, 51-75 HIGH, 76-100 CRITICAL")
    void testRiskLevelBoundaries(int score, RiskLevel expectedLevel) {
        assertEquals(expectedLevel, RiskLevel.fromScore(score));
    }

    @ParameterizedTest
    @ValueSource(ints = {-10, -1, 101, 150})
    @DisplayName("RiskLevel rejects out-of-range scores")
    void testRiskLevelInvalidScores(int invalidScore) {
        assertThrows(IllegalArgumentException.class, () -> RiskLevel.fromScore(invalidScore));
    }

    @Test
    @DisplayName("Negative waste duration is rejected with IllegalArgumentException")
    void testNegativeDurationRejected() {
        RiskAssessmentRequestDto req = new RiskAssessmentRequestDto(
                WasteQuantity.LOW, -1, false, false, false, false, false
        );
        assertThrows(IllegalArgumentException.class, () -> riskEngine.calculateRisk(req));
    }

    @Test
    @DisplayName("Null request is rejected with IllegalArgumentException")
    void testNullRequestRejected() {
        assertThrows(IllegalArgumentException.class, () -> riskEngine.calculateRisk(null));
    }

    @Test
    @DisplayName("Missing waste quantity is rejected with IllegalArgumentException")
    void testNullQuantityRejected() {
        RiskAssessmentRequestDto req = new RiskAssessmentRequestDto(
                null, 1, false, false, false, false, false
        );
        assertThrows(IllegalArgumentException.class, () -> riskEngine.calculateRisk(req));
    }

    @Test
    @DisplayName("Missing environmental flags are rejected with IllegalArgumentException")
    void testNullFlagsRejected() {
        RiskAssessmentRequestDto req = new RiskAssessmentRequestDto(
                WasteQuantity.MEDIUM, 2, null, true, true, false, false
        );
        assertThrows(IllegalArgumentException.class, () -> riskEngine.calculateRisk(req));
    }
}
