package com.pestrisk.controller;

import com.pestrisk.dto.DashboardSummaryDto;
import com.pestrisk.service.WasteReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff/dashboard")
public class StaffDashboardController {

    private final WasteReportService wasteReportService;

    public StaffDashboardController(WasteReportService wasteReportService) {
        this.wasteReportService = wasteReportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary() {
        DashboardSummaryDto summary = wasteReportService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}
