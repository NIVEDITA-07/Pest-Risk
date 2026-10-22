package com.pestrisk.dto;

import com.pestrisk.entity.ReportStatusHistory;
import com.pestrisk.enums.ReportStatus;

import java.time.Instant;

public class ReportStatusHistoryDto {

    private Long id;
    private ReportStatus fromStatus;
    private ReportStatus toStatus;
    private Instant changedAt;
    private String remarks;

    public ReportStatusHistoryDto() {
    }

    public static ReportStatusHistoryDto fromEntity(ReportStatusHistory entity) {
        if (entity == null) {
            return null;
        }
        ReportStatusHistoryDto dto = new ReportStatusHistoryDto();
        dto.setId(entity.getId());
        dto.setFromStatus(entity.getFromStatus());
        dto.setToStatus(entity.getToStatus());
        dto.setChangedAt(entity.getChangedAt());
        dto.setRemarks(entity.getRemarks());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
