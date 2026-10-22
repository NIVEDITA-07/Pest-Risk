package com.pestrisk.service;

import com.pestrisk.dto.DashboardSummaryDto;
import com.pestrisk.dto.RiskAssessmentResponseDto;
import com.pestrisk.dto.WasteReportCreateDto;
import com.pestrisk.dto.WasteReportResponseDto;
import com.pestrisk.entity.WasteReport;
import com.pestrisk.enums.ReportStatus;
import com.pestrisk.enums.RiskLevel;
import com.pestrisk.exception.ResourceNotFoundException;
import com.pestrisk.repository.WasteReportRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class WasteReportService {

    private final WasteReportRepository wasteReportRepository;
    private final RiskEngine riskEngine;

    public WasteReportService(WasteReportRepository wasteReportRepository, RiskEngine riskEngine) {
        this.wasteReportRepository = wasteReportRepository;
        this.riskEngine = riskEngine;
    }

    @Transactional
    public WasteReportResponseDto createReport(WasteReportCreateDto request) {
        // Calculate risk exclusively on the backend using RiskEngine
        RiskAssessmentResponseDto riskResult = riskEngine.calculateRisk(request);

        WasteReport report = new WasteReport();
        report.setWard(request.getWard().trim());
        report.setLocationDescription(request.getLocationDescription().trim());
        report.setWasteQuantity(request.getWasteQuantity());
        report.setWasteDurationDays(request.getWasteDurationDays());
        report.setStagnantWater(request.getStagnantWater());
        report.setMosquitoSightings(request.getMosquitoSightings());
        report.setRodentSightings(request.getRodentSightings());
        report.setStrayAnimalSightings(request.getStrayAnimalSightings());
        report.setUnsegregatedWaste(request.getUnsegregatedWaste());

        // Store Java-calculated risk metrics
        report.setRiskScore(riskResult.getScore());
        report.setRiskLevel(riskResult.getRiskLevel());
        report.setRiskExplanation(riskResult.getExplanation());
        report.setStatus(ReportStatus.REPORTED);

        WasteReport saved = wasteReportRepository.save(report);
        return WasteReportResponseDto.fromEntity(saved);
    }

    @Transactional
    public WasteReportResponseDto updateStatus(Long id, ReportStatus newStatus, String remarks) {
        WasteReport report = wasteReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste report not found with id: " + id));

        report.updateStatus(newStatus, remarks);
        WasteReport updated = wasteReportRepository.save(report);
        return WasteReportResponseDto.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public List<WasteReportResponseDto> getAllReports() {
        return wasteReportRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(WasteReportResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WasteReportResponseDto> getReports(ReportStatus status, RiskLevel riskLevel, String ward) {
        if (status == null && riskLevel == null && (ward == null || ward.isBlank())) {
            return getAllReports();
        }

        Specification<WasteReport> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (riskLevel != null) {
                predicates.add(cb.equal(root.get("riskLevel"), riskLevel));
            }
            if (ward != null && !ward.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("ward")), "%" + ward.trim().toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return wasteReportRepository.findAll(spec, sort)
                .stream()
                .map(WasteReportResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WasteReportResponseDto> getPriorityReports() {
        return wasteReportRepository.findUnresolvedReportsByPriority()
                .stream()
                .map(WasteReportResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public WasteReportResponseDto getReportById(Long id) {
        WasteReport report = wasteReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Waste report not found with id: " + id));
        return WasteReportResponseDto.fromEntity(report);
    }

    @Transactional(readOnly = true)
    public DashboardSummaryDto getDashboardSummary() {
        long total = wasteReportRepository.count();
        long pending = wasteReportRepository.countByStatus(ReportStatus.REPORTED);
        long inProgress = wasteReportRepository.countByStatus(ReportStatus.IN_PROGRESS);
        long resolved = wasteReportRepository.countByStatus(ReportStatus.RESOLVED);
        long verified = wasteReportRepository.countByStatus(ReportStatus.VERIFIED);
        long rejected = wasteReportRepository.countByStatus(ReportStatus.REJECTED);

        Map<RiskLevel, Long> countsByRiskLevel = new LinkedHashMap<>();
        countsByRiskLevel.put(RiskLevel.CRITICAL, wasteReportRepository.countByRiskLevel(RiskLevel.CRITICAL));
        countsByRiskLevel.put(RiskLevel.HIGH, wasteReportRepository.countByRiskLevel(RiskLevel.HIGH));
        countsByRiskLevel.put(RiskLevel.MODERATE, wasteReportRepository.countByRiskLevel(RiskLevel.MODERATE));
        countsByRiskLevel.put(RiskLevel.LOW, wasteReportRepository.countByRiskLevel(RiskLevel.LOW));

        long unresolvedHighCritical = wasteReportRepository.countByRiskLevelInAndStatusNotIn(
                List.of(RiskLevel.HIGH, RiskLevel.CRITICAL),
                List.of(ReportStatus.RESOLVED, ReportStatus.REJECTED)
        );

        String pendingDefinition = "Pending reports represent newly submitted citizen reports in REPORTED status awaiting staff verification.";

        return new DashboardSummaryDto(
                total,
                pending,
                inProgress,
                resolved,
                verified,
                rejected,
                countsByRiskLevel,
                unresolvedHighCritical,
                pendingDefinition
        );
    }

    @Transactional(readOnly = true)
    public List<com.pestrisk.dto.CommunityAlertDto> getActiveCommunityAlerts() {
        return wasteReportRepository.findActiveHighRiskHotspots()
                .stream()
                .map(r -> new com.pestrisk.dto.CommunityAlertDto(
                        r.getId(),
                        r.getWard(),
                        r.getRiskLevel(),
                        r.getStatus(),
                        String.format("Active %s pest & vector hotspot identified in %s. Status: %s. Avoid contact and report additional standing waste.", r.getRiskLevel(), r.getWard(), r.getStatus()),
                        r.getCreatedAt()
                ))
                .toList();
    }
}
