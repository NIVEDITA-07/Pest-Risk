package com.pestrisk.dto;

import com.pestrisk.enums.RiskLevel;

import java.util.Map;

public class DashboardSummaryDto {

    private long totalReports;
    private long pendingReports;
    private long inProgressReports;
    private long resolvedReports;
    private long verifiedReports;
    private long rejectedReports;
    private Map<RiskLevel, Long> countsByRiskLevel;
    private long unresolvedHighCriticalReports;
    private String pendingDefinition;

    public DashboardSummaryDto() {
    }

    public DashboardSummaryDto(
            long totalReports,
            long pendingReports,
            long inProgressReports,
            long resolvedReports,
            long verifiedReports,
            long rejectedReports,
            Map<RiskLevel, Long> countsByRiskLevel,
            long unresolvedHighCriticalReports,
            String pendingDefinition) {
        this.totalReports = totalReports;
        this.pendingReports = pendingReports;
        this.inProgressReports = inProgressReports;
        this.resolvedReports = resolvedReports;
        this.verifiedReports = verifiedReports;
        this.rejectedReports = rejectedReports;
        this.countsByRiskLevel = countsByRiskLevel;
        this.unresolvedHighCriticalReports = unresolvedHighCriticalReports;
        this.pendingDefinition = pendingDefinition;
    }

    public long getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(long totalReports) {
        this.totalReports = totalReports;
    }

    public long getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(long pendingReports) {
        this.pendingReports = pendingReports;
    }

    public long getInProgressReports() {
        return inProgressReports;
    }

    public void setInProgressReports(long inProgressReports) {
        this.inProgressReports = inProgressReports;
    }

    public long getResolvedReports() {
        return resolvedReports;
    }

    public void setResolvedReports(long resolvedReports) {
        this.resolvedReports = resolvedReports;
    }

    public long getVerifiedReports() {
        return verifiedReports;
    }

    public void setVerifiedReports(long verifiedReports) {
        this.verifiedReports = verifiedReports;
    }

    public long getRejectedReports() {
        return rejectedReports;
    }

    public void setRejectedReports(long rejectedReports) {
        this.rejectedReports = rejectedReports;
    }

    public Map<RiskLevel, Long> getCountsByRiskLevel() {
        return countsByRiskLevel;
    }

    public void setCountsByRiskLevel(Map<RiskLevel, Long> countsByRiskLevel) {
        this.countsByRiskLevel = countsByRiskLevel;
    }

    public long getUnresolvedHighCriticalReports() {
        return unresolvedHighCriticalReports;
    }

    public void setUnresolvedHighCriticalReports(long unresolvedHighCriticalReports) {
        this.unresolvedHighCriticalReports = unresolvedHighCriticalReports;
    }

    public String getPendingDefinition() {
        return pendingDefinition;
    }

    public void setPendingDefinition(String pendingDefinition) {
        this.pendingDefinition = pendingDefinition;
    }
}
