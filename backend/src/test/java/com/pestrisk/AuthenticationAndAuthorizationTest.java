package com.pestrisk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pestrisk.dto.CreateStaffRequestDto;
import com.pestrisk.dto.LoginRequestDto;
import com.pestrisk.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationAndAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    @DisplayName("Public endpoints should be accessible without authentication")
    void publicEndpointsAccessibleWithoutAuth() throws Exception {
        // GET /api/health
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));

        // POST /api/risk/preview
        String previewJson = """
                {
                    "wasteQuantity": "LOW",
                    "wasteDurationDays": 1,
                    "stagnantWater": false,
                    "mosquitoSightings": false,
                    "rodentSightings": false,
                    "strayAnimalSightings": false,
                    "unsegregatedWaste": false
                }
                """;
        mockMvc.perform(post("/api/risk/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(previewJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel", is("LOW")));

        // GET /api/community/alerts
        mockMvc.perform(get("/api/community/alerts"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Protected endpoints reject unauthenticated requests with 401 Unauthorized")
    void protectedEndpointsRejectUnauthenticated() throws Exception {
        // Staff summary
        mockMvc.perform(get("/api/staff/dashboard/summary"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Unauthorized")));

        // Priority reports
        mockMvc.perform(get("/api/reports/priority"))
                .andExpect(status().isUnauthorized());

        // Auth me
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());

        // Admin endpoint
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Valid login returns token and role without exposing password hash")
    void validLoginSucceeds() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto("admin", "TestAdminSecret2026!LongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", not(emptyOrNullString())))
                .andExpect(jsonPath("$.username", is("admin")))
                .andExpect(jsonPath("$.role", is("ADMIN")))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("Invalid login returns 401 or BadCredentials error")
    void invalidLoginFails() throws Exception {
        LoginRequestDto badRequest = new LoginRequestDto("admin", "WrongPassword123!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("STAFF role cannot access ADMIN user management endpoints")
    @WithMockUser(username = "staffUser", roles = "STAFF")
    void staffCannotAccessAdminEndpoints() throws Exception {
        CreateStaffRequestDto createStaff = new CreateStaffRequestDto("newStaff", "StaffPass123");

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStaff)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Forbidden")));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN role can create staff accounts and list accounts")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanManageStaffAccounts() throws Exception {
        String uniqueStaffName = "staff_" + System.currentTimeMillis();
        CreateStaffRequestDto createStaff = new CreateStaffRequestDto(uniqueStaffName, "SafePassword123");

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStaff)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(uniqueStaffName)))
                .andExpect(jsonPath("$.role", is("STAFF")))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Token-based request lifecycle: login -> get token -> access protected endpoints")
    void tokenLifecycleEndToEnd() throws Exception {
        // 1. Login
        LoginRequestDto loginRequest = new LoginRequestDto("admin", "TestAdminSecret2026!LongPassword");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseContent).get("token").asText();

        // 2. Access /api/auth/me with Bearer token
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("admin")))
                .andExpect(jsonPath("$.role", is("ADMIN")));

        // 3. Access /api/staff/dashboard/summary with Bearer token
        mockMvc.perform(get("/api/staff/dashboard/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReports", notNullValue()));
    }

    @Test
    @DisplayName("Secret rotation invalidates old tokens signed with different secret key")
    void rotatedSecretRejectsOldToken() throws Exception {
        // Create token provider with old/different secret
        com.pestrisk.security.JwtTokenProvider oldProvider = new com.pestrisk.security.JwtTokenProvider(
                "OldSecretKeyBeforeRotationThatWasAtLeast256BitsLong2026!",
                3600,
                objectMapper
        );
        String oldToken = oldProvider.generateToken("admin", com.pestrisk.enums.UserRole.ADMIN);

        // Attempting to access protected endpoint with old token must be rejected with 401
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + oldToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Unauthorized")));
    }
}
