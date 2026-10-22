package com.pestrisk.controller;

import com.pestrisk.dto.StatusUpdateRequestDto;
import com.pestrisk.dto.WasteReportCreateDto;
import com.pestrisk.dto.WasteReportResponseDto;
import com.pestrisk.enums.ReportStatus;
import com.pestrisk.enums.RiskLevel;
import com.pestrisk.service.WasteReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class WasteReportController {

    private final WasteReportService wasteReportService;

    public WasteReportController(WasteReportService wasteReportService) {
        this.wasteReportService = wasteReportService;
    }

    @PostMapping
    public ResponseEntity<WasteReportResponseDto> createReport(
            @Valid @RequestBody WasteReportCreateDto request) {
        WasteReportResponseDto createdReport = wasteReportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
    }

    @GetMapping
    public ResponseEntity<List<WasteReportResponseDto>> getReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) RiskLevel riskLevel,
            @RequestParam(required = false) String ward) {
        List<WasteReportResponseDto> reports = wasteReportService.getReports(status, riskLevel, ward);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/priority")
    public ResponseEntity<List<WasteReportResponseDto>> getPriorityReports() {
        List<WasteReportResponseDto> priorityReports = wasteReportService.getPriorityReports();
        return ResponseEntity.ok(priorityReports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WasteReportResponseDto> getReportById(@PathVariable Long id) {
        WasteReportResponseDto report = wasteReportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WasteReportResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequestDto request) {
        WasteReportResponseDto updated = wasteReportService.updateStatus(id, request.getStatus(), request.getRemarks());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<com.pestrisk.dto.CommunityAlertDto>> getCommunityAlerts() {
        List<com.pestrisk.dto.CommunityAlertDto> alerts = wasteReportService.getActiveCommunityAlerts();
        return ResponseEntity.ok(alerts);
    }
}
