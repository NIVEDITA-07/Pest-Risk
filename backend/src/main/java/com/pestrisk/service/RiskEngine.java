package com.pestrisk.service;

import com.pestrisk.dto.RiskAssessmentRequestDto;
import com.pestrisk.dto.RiskAssessmentResponseDto;
import com.pestrisk.enums.RiskLevel;
import com.pestrisk.enums.WasteQuantity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiskEngine {

    public RiskAssessmentResponseDto calculateRisk(RiskAssessmentRequestDto request) {
        validateRequest(request);

        int totalScore = 0;
        List<String> factors = new ArrayList<>();

        // 1. Waste Quantity (LOW = 5, MEDIUM = 10, HIGH = 15)
        WasteQuantity quantity = request.getWasteQuantity();
        int quantityPoints = switch (quantity) {
            case LOW -> 5;
            case MEDIUM -> 10;
            case HIGH -> 15;
        };
        totalScore += quantityPoints;
        factors.add(String.format("Waste Quantity (%s): +%d points", quantity, quantityPoints));

        // 2. Waste Duration (0-1 days = 0, 2-3 days = 8, 4+ days = 15)
        int duration = request.getWasteDurationDays();
        int durationPoints;
        if (duration <= 1) {
            durationPoints = 0;
            factors.add(String.format("Waste Duration (%d days): +0 points (minimal accumulation)", duration));
        } else if (duration <= 3) {
            durationPoints = 8;
            factors.add(String.format("Waste Duration (%d days): +8 points (moderate accumulation)", duration));
        } else {
            durationPoints = 15;
            factors.add(String.format("Waste Duration (%d days): +15 points (prolonged accumulation)", duration));
        }
        totalScore += durationPoints;

        // 3. Stagnant Water (20 points)
        if (Boolean.TRUE.equals(request.getStagnantWater())) {
            totalScore += 20;
            factors.add("Stagnant Water: +20 points (high vector breeding risk)");
        }

        // 4. Mosquito Sightings (15 points)
        if (Boolean.TRUE.equals(request.getMosquitoSightings())) {
            totalScore += 15;
            factors.add("Mosquito Sightings: +15 points (active disease vector risk)");
        }

        // 5. Rodent Sightings (15 points)
        if (Boolean.TRUE.equals(request.getRodentSightings())) {
            totalScore += 15;
            factors.add("Rodent Sightings: +15 points (direct pest contamination risk)");
        }

        // 6. Stray Animal Sightings (10 points)
        if (Boolean.TRUE.equals(request.getStrayAnimalSightings())) {
            totalScore += 10;
            factors.add("Stray Animal Sightings: +10 points (waste dispersal risk)");
        }

        // 7. Unsegregated Waste (10 points)
        if (Boolean.TRUE.equals(request.getUnsegregatedWaste())) {
            totalScore += 10;
            factors.add("Unsegregated Waste: +10 points (decomposition hazard)");
        }

        // Score boundary clamping (guarantees 0-100 range)
        totalScore = Math.max(0, Math.min(100, totalScore));
        RiskLevel level = RiskLevel.fromScore(totalScore);

        String explanation = String.format(
                "Risk level evaluated as %s (%d/100) based on environmental waste and pest hazard factors.",
                level, totalScore
        );

        return new RiskAssessmentResponseDto(totalScore, level, explanation, factors);
    }

    private void validateRequest(RiskAssessmentRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Risk assessment request cannot be null");
        }
        if (request.getWasteQuantity() == null) {
            throw new IllegalArgumentException("Waste quantity is required (LOW, MEDIUM, HIGH)");
        }
        if (request.getWasteDurationDays() == null) {
            throw new IllegalArgumentException("Waste duration is required");
        }
        if (request.getWasteDurationDays() < 0) {
            throw new IllegalArgumentException("Waste duration cannot be negative: " + request.getWasteDurationDays());
        }
        if (request.getStagnantWater() == null) {
            throw new IllegalArgumentException("Stagnant water flag is required");
        }
        if (request.getMosquitoSightings() == null) {
            throw new IllegalArgumentException("Mosquito sightings flag is required");
        }
        if (request.getRodentSightings() == null) {
            throw new IllegalArgumentException("Rodent sightings flag is required");
        }
        if (request.getStrayAnimalSightings() == null) {
            throw new IllegalArgumentException("Stray animal sightings flag is required");
        }
        if (request.getUnsegregatedWaste() == null) {
            throw new IllegalArgumentException("Unsegregated waste flag is required");
        }
    }
}
