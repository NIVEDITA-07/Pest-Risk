package com.pestrisk.entity;

import com.pestrisk.enums.ReportStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "report_status_history")
public class ReportStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private WasteReport report;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus toStatus;

    @Column(nullable = false)
    private Instant changedAt;

    @Column(length = 500)
    private String remarks;

    public ReportStatusHistory() {
    }

    public ReportStatusHistory(WasteReport report, ReportStatus fromStatus, ReportStatus toStatus, Instant changedAt, String remarks) {
        this.report = report;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedAt = changedAt;
        this.remarks = remarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WasteReport getReport() {
        return report;
    }

    public void setReport(WasteReport report) {
        this.report = report;
    }

    public ReportStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(ReportStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public ReportStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(ReportStatus toStatus) {
        this.toStatus = toStatus;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
