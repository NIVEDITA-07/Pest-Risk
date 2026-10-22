package com.pestrisk.dto;

import com.pestrisk.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StatusUpdateRequestDto {

    @NotNull(message = "Status is required (REPORTED, VERIFIED, IN_PROGRESS, RESOLVED, REJECTED)")
    private ReportStatus status;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public StatusUpdateRequestDto() {
    }

    public StatusUpdateRequestDto(ReportStatus status) {
        this.status = status;
    }

    public StatusUpdateRequestDto(ReportStatus status, String remarks) {
        this.status = status;
        this.remarks = remarks;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
