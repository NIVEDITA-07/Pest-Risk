package com.pestrisk.dto;

import com.pestrisk.entity.WasteReport;
import com.pestrisk.enums.ReportStatus;
import com.pestrisk.enums.RiskLevel;
import com.pestrisk.enums.WasteQuantity;

import java.time.Instant;

public class WasteReportResponseDto {

    private Long id;
    private String ward;
    private String locationDescription;
    private WasteQuantity wasteQuantity;
    private Integer wasteDurationDays;
    private Boolean stagnantWater;
    private Boolean mosquitoSightings;
    private Boolean rodentSightings;
    private Boolean strayAnimalSightings;
    private Boolean unsegregatedWaste;
    private Integer riskScore;
    private RiskLevel riskLevel;
    private String riskExplanation;
    private ReportStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private java.util.List<ReportStatusHistoryDto> statusHistory = new java.util.ArrayList<>();

    public WasteReportResponseDto() {
    }

    public static WasteReportResponseDto fromEntity(WasteReport report) {
        WasteReportResponseDto dto = new WasteReportResponseDto();
        dto.setId(report.getId());
        dto.setWard(report.getWard());
        dto.setLocationDescription(report.getLocationDescription());
        dto.setWasteQuantity(report.getWasteQuantity());
        dto.setWasteDurationDays(report.getWasteDurationDays());
        dto.setStagnantWater(report.getStagnantWater());
        dto.setMosquitoSightings(report.getMosquitoSightings());
        dto.setRodentSightings(report.getRodentSightings());
        dto.setStrayAnimalSightings(report.getStrayAnimalSightings());
        dto.setUnsegregatedWaste(report.getUnsegregatedWaste());
        dto.setRiskScore(report.getRiskScore());
        dto.setRiskLevel(report.getRiskLevel());
        dto.setRiskExplanation(report.getRiskExplanation());
        dto.setStatus(report.getStatus());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setUpdatedAt(report.getUpdatedAt());
        if (report.getStatusHistory() != null) {
            dto.setStatusHistory(report.getStatusHistory().stream()
                    .map(ReportStatusHistoryDto::fromEntity)
                    .toList());
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public WasteQuantity getWasteQuantity() {
        return wasteQuantity;
    }

    public void setWasteQuantity(WasteQuantity wasteQuantity) {
        this.wasteQuantity = wasteQuantity;
    }

    public Integer getWasteDurationDays() {
        return wasteDurationDays;
    }

    public void setWasteDurationDays(Integer wasteDurationDays) {
        this.wasteDurationDays = wasteDurationDays;
    }

    public Boolean getStagnantWater() {
        return stagnantWater;
    }

    public void setStagnantWater(Boolean stagnantWater) {
        this.stagnantWater = stagnantWater;
    }

    public Boolean getMosquitoSightings() {
        return mosquitoSightings;
    }

    public void setMosquitoSightings(Boolean mosquitoSightings) {
        this.mosquitoSightings = mosquitoSightings;
    }

    public Boolean getRodentSightings() {
        return rodentSightings;
    }

    public void setRodentSightings(Boolean rodentSightings) {
        this.rodentSightings = rodentSightings;
    }

    public Boolean getStrayAnimalSightings() {
        return strayAnimalSightings;
    }

    public void setStrayAnimalSightings(Boolean strayAnimalSightings) {
        this.strayAnimalSightings = strayAnimalSightings;
    }

    public Boolean getUnsegregatedWaste() {
        return unsegregatedWaste;
    }

    public void setUnsegregatedWaste(Boolean unsegregatedWaste) {
        this.unsegregatedWaste = unsegregatedWaste;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskExplanation() {
        return riskExplanation;
    }

    public void setRiskExplanation(String riskExplanation) {
        this.riskExplanation = riskExplanation;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public java.util.List<ReportStatusHistoryDto> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(java.util.List<ReportStatusHistoryDto> statusHistory) {
        this.statusHistory = statusHistory;
    }
}
