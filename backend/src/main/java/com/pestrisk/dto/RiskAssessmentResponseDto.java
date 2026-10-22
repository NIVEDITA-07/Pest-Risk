package com.pestrisk.dto;

import com.pestrisk.enums.RiskLevel;

import java.util.ArrayList;
import java.util.List;

public class RiskAssessmentResponseDto {

    private int score;
    private RiskLevel riskLevel;
    private String explanation;
    private List<String> contributingFactors;

    public RiskAssessmentResponseDto() {
        this.contributingFactors = new ArrayList<>();
    }

    public RiskAssessmentResponseDto(int score, RiskLevel riskLevel, String explanation, List<String> contributingFactors) {
        this.score = score;
        this.riskLevel = riskLevel;
        this.explanation = explanation;
        this.contributingFactors = contributingFactors;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<String> getContributingFactors() {
        return contributingFactors;
    }

    public void setContributingFactors(List<String> contributingFactors) {
        this.contributingFactors = contributingFactors;
    }

    public int getRiskScore() {
        return score;
    }

    public String getRiskExplanation() {
        return explanation;
    }
}
