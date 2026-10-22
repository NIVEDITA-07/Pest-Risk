package com.pestrisk.enums;

public enum WasteQuantity {
    LOW(5),
    MEDIUM(10),
    HIGH(15);

    private final int score;

    WasteQuantity(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
