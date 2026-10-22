package com.pestrisk.controller;

import com.pestrisk.dto.RiskAssessmentRequestDto;
import com.pestrisk.dto.RiskAssessmentResponseDto;
import com.pestrisk.service.RiskEngine;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/risk")
public class RiskController {

    private final RiskEngine riskEngine;

    public RiskController(RiskEngine riskEngine) {
        this.riskEngine = riskEngine;
    }

    @PostMapping("/preview")
    public ResponseEntity<RiskAssessmentResponseDto> previewRisk(
            @Valid @RequestBody RiskAssessmentRequestDto request) {
        RiskAssessmentResponseDto response = riskEngine.calculateRisk(request);
        return ResponseEntity.ok(response);
    }
}
