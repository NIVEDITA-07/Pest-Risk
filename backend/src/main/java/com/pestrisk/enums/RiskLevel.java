package com.pestrisk.enums;

public enum RiskLevel {
    LOW(0, 25),
    MODERATE(26, 50),
    HIGH(51, 75),
    CRITICAL(76, 100);

    private final int minScore;
    private final int maxScore;

    RiskLevel(int minScore, int maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public static RiskLevel fromScore(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Risk score must be between 0 and 100, received: " + score);
        }
        if (score <= 25) {
            return LOW;
        } else if (score <= 50) {
            return MODERATE;
        } else if (score <= 75) {
            return HIGH;
        } else {
            return CRITICAL;
        }
    }
}
