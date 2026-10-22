package com.pestrisk.entity;

import com.pestrisk.enums.ReportStatus;
import com.pestrisk.enums.RiskLevel;
import com.pestrisk.enums.WasteQuantity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "waste_reports")
public class WasteReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ward;

    @Column(nullable = false, length = 1000)
    private String locationDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WasteQuantity wasteQuantity;

    @Column(nullable = false)
    private Integer wasteDurationDays;

    @Column(nullable = false)
    private Boolean stagnantWater;

    @Column(nullable = false)
    private Boolean mosquitoSightings;

    @Column(nullable = false)
    private Boolean rodentSightings;

    @Column(nullable = false)
    private Boolean strayAnimalSightings;

    @Column(nullable = false)
    private Boolean unsegregatedWaste;

    @Column(nullable = false)
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(length = 2000)
    private String riskExplanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.REPORTED;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt ASC")
    private java.util.List<ReportStatusHistory> statusHistory = new java.util.ArrayList<>();

    public WasteReport() {
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = ReportStatus.REPORTED;
        }
        if (this.statusHistory.isEmpty()) {
            addStatusHistory(ReportStatus.REPORTED, ReportStatus.REPORTED, now, "Initial report submitted");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void updateStatus(ReportStatus newStatus, String remarks) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new com.pestrisk.exception.InvalidStatusTransitionException(String.format(
                    "Invalid status transition from %s to %s. Allowed transitions: %s",
                    this.status, newStatus, this.status.getValidNextStatuses()
            ));
        }
        ReportStatus oldStatus = this.status;
        this.status = newStatus;
        Instant now = Instant.now();
        this.updatedAt = now;
        addStatusHistory(oldStatus, newStatus, now, remarks);
    }

    public void addStatusHistory(ReportStatus fromStatus, ReportStatus toStatus, Instant changedAt, String remarks) {
        ReportStatusHistory history = new ReportStatusHistory(this, fromStatus, toStatus, changedAt, remarks);
        this.statusHistory.add(history);
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

    public java.util.List<ReportStatusHistory> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(java.util.List<ReportStatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }
}
