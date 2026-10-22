package com.pestrisk.dto;

import com.pestrisk.enums.ReportStatus;
import com.pestrisk.enums.RiskLevel;

import java.time.Instant;

public class CommunityAlertDto {

    private Long alertId;
    private String ward;
    private RiskLevel riskLevel;
    private ReportStatus status;
    private String advisory;
    private Instant reportedAt;

    public CommunityAlertDto() {
    }

    public CommunityAlertDto(Long alertId, String ward, RiskLevel riskLevel, ReportStatus status, String advisory, Instant reportedAt) {
        this.alertId = alertId;
        this.ward = ward;
        this.riskLevel = riskLevel;
        this.status = status;
        this.advisory = advisory;
        this.reportedAt = reportedAt;
    }

    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getAdvisory() {
        return advisory;
    }

    public void setAdvisory(String advisory) {
        this.advisory = advisory;
    }

    public Instant getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(Instant reportedAt) {
        this.reportedAt = reportedAt;
    }
}
