package com.pestrisk.controller;

import com.pestrisk.dto.CommunityAlertDto;
import com.pestrisk.service.WasteReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityAlertController {

    private final WasteReportService wasteReportService;

    public CommunityAlertController(WasteReportService wasteReportService) {
        this.wasteReportService = wasteReportService;
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<CommunityAlertDto>> getCommunityAlerts() {
        List<CommunityAlertDto> alerts = wasteReportService.getActiveCommunityAlerts();
        return ResponseEntity.ok(alerts);
    }
}
