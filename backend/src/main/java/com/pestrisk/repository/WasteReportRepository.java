package com.pestrisk.repository;

import com.pestrisk.entity.WasteReport;
import com.pestrisk.enums.ReportStatus;
import com.pestrisk.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface WasteReportRepository extends JpaRepository<WasteReport, Long>, JpaSpecificationExecutor<WasteReport> {

    List<WasteReport> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT r FROM WasteReport r
        WHERE r.status NOT IN (com.pestrisk.enums.ReportStatus.RESOLVED, com.pestrisk.enums.ReportStatus.REJECTED)
        ORDER BY
          CASE r.riskLevel
            WHEN com.pestrisk.enums.RiskLevel.CRITICAL THEN 1
            WHEN com.pestrisk.enums.RiskLevel.HIGH THEN 2
            WHEN com.pestrisk.enums.RiskLevel.MODERATE THEN 3
            WHEN com.pestrisk.enums.RiskLevel.LOW THEN 4
            ELSE 5
          END ASC,
          r.createdAt ASC
    """)
    List<WasteReport> findUnresolvedReportsByPriority();

    @Query("""
        SELECT r FROM WasteReport r
        WHERE r.status NOT IN (com.pestrisk.enums.ReportStatus.RESOLVED, com.pestrisk.enums.ReportStatus.REJECTED)
          AND r.riskLevel IN (com.pestrisk.enums.RiskLevel.HIGH, com.pestrisk.enums.RiskLevel.CRITICAL)
        ORDER BY r.createdAt DESC
    """)
    List<WasteReport> findActiveHighRiskHotspots();

    long countByStatus(ReportStatus status);

    long countByRiskLevel(RiskLevel riskLevel);

    long countByRiskLevelInAndStatusNotIn(Collection<RiskLevel> riskLevels, Collection<ReportStatus> statuses);
}
