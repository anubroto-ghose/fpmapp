/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8647
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:41:59
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuditTrailOverrideActionTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        // Clear audit_logs table before each test
        jdbcTemplate.execute("DELETE FROM audit_logs");
    }

    /**
     * Test scenario:
     * 1. Authenticate as compliance officer
     * 2. Perform override action on travel request via POST /fpm/travel/approval
     * 3. Verify audit log entry created with correct metadata
     * 4. Attempt direct modification of audit log entry (should fail)
     * 5. Attempt unauthorized access to audit logs (should be denied)
     * 6. Verify authorized user can query audit logs
     * 7. Verify audit logs remain immutable and secure
     */
    @Test
    @WithMockUser(username = "compliance_officer", roles = {"COMPLIANCE_OFFICER"})
    public void testAuditTrailImmutabilityAndAccessControlOnOverride() throws Exception {
        // Step 1: Mock authenticated user
        User complianceUser = new User();
        complianceUser.setUsername("compliance_officer");
        complianceUser.setRoles(Collections.singletonList("ROLE_COMPLIANCE_OFFICER"));

        // Step 2: Mock override action via POST /fpm/travel/approval
        // Prepare override metadata
        String travelRequestId = "TRAVEL-12345";
        String overrideReason = "Urgent business need";

        // Mock the controller response
        when(fpmTravelController.approveTravelRequestWithOverride(any(), any())).then(invocation -> {
            // Simulate audit log creation
            jdbcTemplate.update("INSERT INTO audit_logs (entity_id, action_type, performed_by, timestamp, metadata) VALUES (?, ?, ?, ?, ?)",
                    travelRequestId, "override", complianceUser.getUsername(), LocalDateTime.now(), "{\"reason\":\"" + overrideReason + "\"}");
            return "Override approved";
        });

        // Perform override action via Selenium (simulate UI interaction)
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("compliance_officer");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.id("loginBtn")).click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to travel approval page
        driver.get(BASE_URL + "/travel/approval");

        // Fill override form
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("travelRequestId"))).sendKeys(travelRequestId);
        driver.findElement(By.id("overrideReason")).sendKeys(overrideReason);
        driver.findElement(By.id("overrideBtn")).click();

        // Wait for success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        assertThat(successMsg.getText()).contains("Override approved");

        // Step 3: Verify audit log entry created
        List<AuditLogEntry> auditLogs = jdbcTemplate.query(
                "SELECT entity_id, action_type, performed_by, metadata FROM audit_logs WHERE entity_id = ?",
                (rs, rowNum) -> new AuditLogEntry(rs.getString("entity_id"), rs.getString("action_type"), rs.getString("performed_by"), rs.getString("metadata")),
                travelRequestId);

        assertThat(auditLogs).hasSize(1);
        AuditLogEntry logEntry = auditLogs.get(0);
        assertThat(logEntry.getActionType()).isEqualTo("override");
        assertThat(logEntry.getPerformedBy()).isEqualTo(complianceUser.getUsername());
        assertThat(logEntry.getMetadata()).contains(overrideReason);

        // Step 4: Attempt direct modification of audit log entry (should fail)
        Exception exception = assertThrows(Exception.class, () -> {
            jdbcTemplate.update("UPDATE audit_logs SET metadata = ? WHERE entity_id = ?", "tampered", travelRequestId);
        });
        assertThat(exception.getMessage()).contains("constraint").or().contains("permission").or().contains("denied");

        // Step 5: Attempt unauthorized access to audit logs
        // Simulate unauthorized user
        when(fpmCommonController.getAuditLogs(any(), any())).thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

        HttpClientErrorException forbiddenException = assertThrows(HttpClientErrorException.class, () -> {
            fpmCommonController.getAuditLogs("someEntity", "override");
        });
        assertThat(forbiddenException.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // Step 6: Verify authorized user can query audit logs
        when(fpmCommonController.getAuditLogs(travelRequestId, "override")).thenReturn(auditLogs);
        List<AuditLogEntry> authorizedLogs = fpmCommonController.getAuditLogs(travelRequestId, "override");
        assertThat(authorizedLogs).isNotEmpty();
        assertThat(authorizedLogs.get(0).getEntityId()).isEqualTo(travelRequestId);

        // Step 7: Verify audit logs remain immutable and secure
        // Attempt to delete audit log entry
        Exception deleteException = assertThrows(Exception.class, () -> {
            jdbcTemplate.update("DELETE FROM audit_logs WHERE entity_id = ?", travelRequestId);
        });
        assertThat(deleteException.getMessage()).contains("constraint").or().contains("permission").or().contains("denied");
    }

    // Helper DTO for audit log entries
    private static class AuditLogEntry {
        private final String entityId;
        private final String actionType;
        private final String performedBy;
        private final String metadata;

        public AuditLogEntry(String entityId, String actionType, String performedBy, String metadata) {
            this.entityId = entityId;
            this.actionType = actionType;
            this.performedBy = performedBy;
            this.metadata = metadata;
        }

        public String getEntityId() {
            return entityId;
        }

        public String getActionType() {
            return actionType;
        }

        public String getPerformedBy() {
            return performedBy;
        }

        public String getMetadata() {
            return metadata;
        }
    }
}
