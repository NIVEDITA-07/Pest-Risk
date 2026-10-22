package com.pestrisk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/risk/preview calculates risk correctly and returns 200 OK")
    void previewRiskSuccess() throws Exception {
        String jsonPayload = """
            {
                "wasteQuantity": "HIGH",
                "wasteDurationDays": 4,
                "stagnantWater": true,
                "mosquitoSightings": true,
                "rodentSightings": true,
                "strayAnimalSightings": true,
                "unsegregatedWaste": true
            }
            """;

        mockMvc.perform(post("/api/risk/preview")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(100))
                .andExpect(jsonPath("$.riskLevel").value("CRITICAL"))
                .andExpect(jsonPath("$.explanation").isNotEmpty())
                .andExpect(jsonPath("$.contributingFactors").isArray())
                .andExpect(jsonPath("$.contributingFactors.length()").value(7));
    }

    @Test
    @DisplayName("POST /api/risk/preview returns 400 Bad Request for negative waste duration")
    void previewRiskNegativeDurationRejected() throws Exception {
        String jsonPayload = """
            {
                "wasteQuantity": "MEDIUM",
                "wasteDurationDays": -2,
                "stagnantWater": false,
                "mosquitoSightings": false,
                "rodentSightings": false,
                "strayAnimalSightings": false,
                "unsegregatedWaste": false
            }
            """;

        mockMvc.perform(post("/api/risk/preview")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("POST /api/risk/preview returns 400 Bad Request for missing required fields")
    void previewRiskMissingFieldsRejected() throws Exception {
        String jsonPayload = """
            {
                "wasteQuantity": "LOW"
            }
            """;

        mockMvc.perform(post("/api/risk/preview")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("POST /api/risk/preview returns 400 Bad Request for invalid waste quantity enum value")
    void previewRiskInvalidEnumRejected() throws Exception {
        String jsonPayload = """
            {
                "wasteQuantity": "ENORMOUS",
                "wasteDurationDays": 2,
                "stagnantWater": false,
                "mosquitoSightings": false,
                "rodentSightings": false,
                "strayAnimalSightings": false,
                "unsegregatedWaste": false
            }
            """;

        mockMvc.perform(post("/api/risk/preview")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("GET /api/health remains operational")
    void healthEndpointRemainsUp() throws Exception {
        mockMvc.perform(get("/api/health")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("PestRisk"));
    }
}
