package com.pestrisk.enums;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum ReportStatus {
    REPORTED,
    VERIFIED,
    IN_PROGRESS,
    RESOLVED,
    REJECTED;

    public boolean canTransitionTo(ReportStatus target) {
        if (target == null || target == this) {
            return false;
        }
        return getValidNextStatuses().contains(target);
    }

    public Set<ReportStatus> getValidNextStatuses() {
        return switch (this) {
            case REPORTED -> EnumSet.of(VERIFIED, REJECTED);
            case VERIFIED -> EnumSet.of(IN_PROGRESS, REJECTED);
            case IN_PROGRESS -> EnumSet.of(RESOLVED);
            case RESOLVED, REJECTED -> Collections.emptySet();
        };
    }
}
