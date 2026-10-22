package com.pestrisk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pestrisk.dto.StatusUpdateRequestDto;
import com.pestrisk.dto.WasteReportCreateDto;
import com.pestrisk.entity.WasteReport;
import com.pestrisk.enums.ReportStatus;
import com.pestrisk.enums.RiskLevel;
import com.pestrisk.enums.WasteQuantity;
import com.pestrisk.repository.WasteReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser(roles = "STAFF")
class WasteReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WasteReportRepository wasteReportRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        wasteReportRepository.deleteAll();
    }

    private WasteReport createAndSaveReport(String ward, RiskLevel riskLevel, ReportStatus status, Instant createdAt) {
        WasteReport r = new WasteReport();
        r.setWard(ward);
        r.setLocationDescription("Test description for " + ward);
        r.setWasteQuantity(WasteQuantity.MEDIUM);
        r.setWasteDurationDays(2);
        r.setStagnantWater(false);
        r.setMosquitoSightings(false);
        r.setRodentSightings(false);
        r.setStrayAnimalSightings(false);
        r.setUnsegregatedWaste(false);
        r.setRiskScore(riskLevel == RiskLevel.CRITICAL ? 90 : (riskLevel == RiskLevel.HIGH ? 60 : (riskLevel == RiskLevel.MODERATE ? 35 : 10)));
        r.setRiskLevel(riskLevel);
        r.setStatus(status);
        if (createdAt != null) {
            r.setCreatedAt(createdAt);
            r.setUpdatedAt(createdAt);
        }
        return wasteReportRepository.save(r);
    }

    @Test
    @DisplayName("POST /api/reports calculates risk via Java engine, persists report, and returns 201 Created")
    void createReportSuccess() throws Exception {
        WasteReportCreateDto request = new WasteReportCreateDto(
                "Ward 4 - East Village",
                "Near drainage canal culvert",
                WasteQuantity.HIGH,
                5,
                true,
                true,
                true,
                true,
                true
        );

        mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.ward").value("Ward 4 - East Village"))
                .andExpect(jsonPath("$.locationDescription").value("Near drainage canal culvert"))
                .andExpect(jsonPath("$.wasteQuantity").value("HIGH"))
                .andExpect(jsonPath("$.wasteDurationDays").value(5))
                .andExpect(jsonPath("$.stagnantWater").value(true))
                .andExpect(jsonPath("$.mosquitoSightings").value(true))
                .andExpect(jsonPath("$.rodentSightings").value(true))
                .andExpect(jsonPath("$.strayAnimalSightings").value(true))
                .andExpect(jsonPath("$.unsegregatedWaste").value(true))
                .andExpect(jsonPath("$.riskScore").value(100))
                .andExpect(jsonPath("$.riskLevel").value("CRITICAL"))
                .andExpect(jsonPath("$.status").value("REPORTED"))
                .andExpect(jsonPath("$.statusHistory", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        assertEquals(1, wasteReportRepository.count());
        WasteReport saved = wasteReportRepository.findAll().get(0);
        assertEquals(100, saved.getRiskScore());
        assertEquals(RiskLevel.CRITICAL, saved.getRiskLevel());
        assertEquals(ReportStatus.REPORTED, saved.getStatus());
    }

    @Test
    @DisplayName("GET /api/reports returns all reports sorted newest first")
    void getAllReportsSorted() throws Exception {
        createAndSaveReport("Ward 1", RiskLevel.LOW, ReportStatus.REPORTED, Instant.now().minus(1, ChronoUnit.HOURS));
        createAndSaveReport("Ward 2", RiskLevel.HIGH, ReportStatus.REPORTED, Instant.now());

        mockMvc.perform(get("/api/reports")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ward").value("Ward 2"))
                .andExpect(jsonPath("$[1].ward").value("Ward 1"));
    }

    @Test
    @DisplayName("GET /api/reports with status, riskLevel, and ward filters")
    void getReportsWithFilters() throws Exception {
        createAndSaveReport("Ward 1", RiskLevel.LOW, ReportStatus.REPORTED, Instant.now().minus(2, ChronoUnit.HOURS));
        createAndSaveReport("Ward 2", RiskLevel.HIGH, ReportStatus.VERIFIED, Instant.now().minus(1, ChronoUnit.HOURS));
        createAndSaveReport("Ward 2", RiskLevel.CRITICAL, ReportStatus.IN_PROGRESS, Instant.now());

        // Filter by status
        mockMvc.perform(get("/api/reports?status=VERIFIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ward").value("Ward 2"))
                .andExpect(jsonPath("$[0].status").value("VERIFIED"));

        // Filter by riskLevel
        mockMvc.perform(get("/api/reports?riskLevel=CRITICAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].riskLevel").value("CRITICAL"));

        // Filter by ward
        mockMvc.perform(get("/api/reports?ward=Ward 2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Combined filter
        mockMvc.perform(get("/api/reports?ward=Ward 2&status=IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].riskLevel").value("CRITICAL"));
    }

    @Test
    @DisplayName("GET /api/reports/priority returns unresolved reports sorted by risk severity then oldest first")
    void getPriorityReports() throws Exception {
        Instant now = Instant.now();
        // r1: LOW, created 10 hours ago
        createAndSaveReport("Ward 1", RiskLevel.LOW, ReportStatus.REPORTED, now.minus(10, ChronoUnit.HOURS));
        // r2: HIGH, created 5 hours ago
        createAndSaveReport("Ward 2", RiskLevel.HIGH, ReportStatus.VERIFIED, now.minus(5, ChronoUnit.HOURS));
        // r3: CRITICAL, created 8 hours ago (older)
        createAndSaveReport("Ward 3", RiskLevel.CRITICAL, ReportStatus.REPORTED, now.minus(8, ChronoUnit.HOURS));
        // r4: CRITICAL, created 2 hours ago (newer)
        createAndSaveReport("Ward 4", RiskLevel.CRITICAL, ReportStatus.IN_PROGRESS, now.minus(2, ChronoUnit.HOURS));
        // r5: RESOLVED (CRITICAL) - should be excluded
        createAndSaveReport("Ward 5", RiskLevel.CRITICAL, ReportStatus.RESOLVED, now.minus(20, ChronoUnit.HOURS));
        // r6: REJECTED (HIGH) - should be excluded
        createAndSaveReport("Ward 6", RiskLevel.HIGH, ReportStatus.REJECTED, now.minus(15, ChronoUnit.HOURS));

        mockMvc.perform(get("/api/reports/priority")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                // 1st: CRITICAL, older (Ward 3)
                .andExpect(jsonPath("$[0].ward").value("Ward 3"))
                .andExpect(jsonPath("$[0].riskLevel").value("CRITICAL"))
                // 2nd: CRITICAL, newer (Ward 4)
                .andExpect(jsonPath("$[1].ward").value("Ward 4"))
                .andExpect(jsonPath("$[1].riskLevel").value("CRITICAL"))
                // 3rd: HIGH (Ward 2)
                .andExpect(jsonPath("$[2].ward").value("Ward 2"))
                .andExpect(jsonPath("$[2].riskLevel").value("HIGH"))
                // 4th: LOW (Ward 1)
                .andExpect(jsonPath("$[3].ward").value("Ward 1"))
                .andExpect(jsonPath("$[3].riskLevel").value("LOW"));
    }

    @Test
    @DisplayName("PATCH /api/reports/{id}/status executes valid workflow transitions and records history")
    void testValidStatusTransitions() throws Exception {
        WasteReport report = createAndSaveReport("Ward 1", RiskLevel.HIGH, ReportStatus.REPORTED, Instant.now());

        // REPORTED -> VERIFIED
        StatusUpdateRequestDto verifyReq = new StatusUpdateRequestDto(ReportStatus.VERIFIED, "Verified by officer");
        mockMvc.perform(patch("/api/reports/" + report.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VERIFIED"))
                .andExpect(jsonPath("$.statusHistory", hasSize(greaterThanOrEqualTo(2))));

        // VERIFIED -> IN_PROGRESS
        StatusUpdateRequestDto progressReq = new StatusUpdateRequestDto(ReportStatus.IN_PROGRESS, "Sanitation team dispatched");
        mockMvc.perform(patch("/api/reports/" + report.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(progressReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // IN_PROGRESS -> RESOLVED
        StatusUpdateRequestDto resolveReq = new StatusUpdateRequestDto(ReportStatus.RESOLVED, "Waste removed and sanitized");
        mockMvc.perform(patch("/api/reports/" + report.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resolveReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }

    @Test
    @DisplayName("PATCH /api/reports/{id}/status allows REJECTED from REPORTED or VERIFIED")
    void testRejectionTransitions() throws Exception {
        WasteReport r1 = createAndSaveReport("Ward 1", RiskLevel.LOW, ReportStatus.REPORTED, Instant.now());
        mockMvc.perform(patch("/api/reports/" + r1.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new StatusUpdateRequestDto(ReportStatus.REJECTED, "Duplicate report"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        WasteReport r2 = createAndSaveReport("Ward 2", RiskLevel.LOW, ReportStatus.VERIFIED, Instant.now());
        mockMvc.perform(patch("/api/reports/" + r2.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new StatusUpdateRequestDto(ReportStatus.REJECTED, "Private land - outside municipal jurisdiction"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @DisplayName("PATCH /api/reports/{id}/status rejects invalid status transitions with 400 Bad Request")
    void testInvalidStatusTransitions() throws Exception {
        WasteReport report = createAndSaveReport("Ward 1", RiskLevel.HIGH, ReportStatus.REPORTED, Instant.now());

        // REPORTED -> RESOLVED (invalid, must be verified first)
        mockMvc.perform(patch("/api/reports/" + report.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new StatusUpdateRequestDto(ReportStatus.RESOLVED))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("Invalid status transition")));

        // REPORTED -> IN_PROGRESS (invalid)
        mockMvc.perform(patch("/api/reports/" + report.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new StatusUpdateRequestDto(ReportStatus.IN_PROGRESS))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/reports/{id}/status returns 404 NOT_FOUND for non-existent ID")
    void updateStatusNotFound() throws Exception {
        mockMvc.perform(patch("/api/reports/999999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new StatusUpdateRequestDto(ReportStatus.VERIFIED))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/staff/dashboard/summary returns accurate counts and pending definition")
    void getStaffDashboardSummary() throws Exception {
        Instant now = Instant.now();
        createAndSaveReport("Ward 1", RiskLevel.CRITICAL, ReportStatus.REPORTED, now);
        createAndSaveReport("Ward 2", RiskLevel.HIGH, ReportStatus.VERIFIED, now);
        createAndSaveReport("Ward 3", RiskLevel.MODERATE, ReportStatus.IN_PROGRESS, now);
        createAndSaveReport("Ward 4", RiskLevel.LOW, ReportStatus.RESOLVED, now);
        createAndSaveReport("Ward 5", RiskLevel.HIGH, ReportStatus.REJECTED, now);

        mockMvc.perform(get("/api/staff/dashboard/summary")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReports").value(5))
                .andExpect(jsonPath("$.pendingReports").value(1))
                .andExpect(jsonPath("$.inProgressReports").value(1))
                .andExpect(jsonPath("$.resolvedReports").value(1))
                .andExpect(jsonPath("$.verifiedReports").value(1))
                .andExpect(jsonPath("$.rejectedReports").value(1))
                .andExpect(jsonPath("$.countsByRiskLevel.CRITICAL").value(1))
                .andExpect(jsonPath("$.countsByRiskLevel.HIGH").value(2))
                .andExpect(jsonPath("$.countsByRiskLevel.MODERATE").value(1))
                .andExpect(jsonPath("$.countsByRiskLevel.LOW").value(1))
                // Unresolved HIGH/CRITICAL: Ward 1 (CRITICAL, REPORTED) + Ward 2 (HIGH, VERIFIED) = 2. (Ward 5 is REJECTED so excluded)
                .andExpect(jsonPath("$.unresolvedHighCriticalReports").value(2))
                .andExpect(jsonPath("$.pendingDefinition").isNotEmpty());
    }

    @Test
    @DisplayName("GET /api/reports/{id} returns 404 NOT_FOUND for non-existent ID")
    void getReportByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/reports/999999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("POST /api/reports returns 400 Bad Request when ward is blank")
    void createReportBlankWardRejected() throws Exception {
        WasteReportCreateDto request = new WasteReportCreateDto(
                "", "Some location", WasteQuantity.LOW, 0, false, false, false, false, false
        );

        mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("GET /api/community/alerts returns high risk alerts without exposing sensitive location details")
    void getCommunityAlertsPrivacySafe() throws Exception {
        Instant now = Instant.now();
        createAndSaveReport("Ward 9", RiskLevel.CRITICAL, ReportStatus.REPORTED, now);
        createAndSaveReport("Ward 10", RiskLevel.HIGH, ReportStatus.VERIFIED, now.minus(1, ChronoUnit.HOURS));
        createAndSaveReport("Ward 11", RiskLevel.LOW, ReportStatus.REPORTED, now.minus(2, ChronoUnit.HOURS)); // Excluded: LOW
        createAndSaveReport("Ward 12", RiskLevel.HIGH, ReportStatus.RESOLVED, now.minus(3, ChronoUnit.HOURS)); // Excluded: RESOLVED

        mockMvc.perform(get("/api/community/alerts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].ward", containsInAnyOrder("Ward 9", "Ward 10")))
                .andExpect(jsonPath("$[0].advisory").value(containsString("pest & vector hotspot")))
                .andExpect(jsonPath("$[0].locationDescription").doesNotExist()); // Privacy-safe check!
    }
}
