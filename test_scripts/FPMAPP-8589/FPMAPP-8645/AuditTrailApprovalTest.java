/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8645
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:43:08
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test for verifying audit trail logging on approval action.
 * 
 * Preconditions:
 * - User authenticated as compliance officer
 * - POST /fpm/dealsheets/approval API deployed
 * - Audit_Logs table schema extended
 * 
 * This test mocks the approval API response and verifies audit log entry in DB.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuditTrailApprovalTest {

    private static WebDriver driver;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String APPROVAL_API_ENDPOINT = "/fpm/dealsheets/approval";

    private static final String AUDIT_LOGS_TABLE = "Audit_Logs";

    private static final String TEST_USER_ID = "compliance_officer_123";
    private static final String TEST_DEALSHEET_ID = "DS-20240601-001";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        // Clear audit logs for test deal sheet before each test
        jdbcTemplate.update("DELETE FROM " + AUDIT_LOGS_TABLE + " WHERE entity_id = ?", TEST_DEALSHEET_ID);
    }

    /**
     * Test approval action triggers audit trail logging with correct data.
     * 
     * Steps:
     * 1. Mock approval API to return success with audit trail references.
     * 2. Perform approval action via Selenium-driven UI.
     * 3. Verify API response includes audit trail references.
     * 4. Query Audit_Logs table for new entry matching approval action.
     * 5. Assert audit log correctness and immutability.
     */
    @Test
    public void testApprovalActionCreatesAuditTrail() throws Exception {
        // Mock approval metadata and response
        Map<String, Object> approvalRequest = Map.of(
                "dealsheetId", TEST_DEALSHEET_ID,
                "approvedBy", TEST_USER_ID,
                "approvalStatus", "APPROVED",
                "comments", "Approved after compliance review"
        );

        Map<String, Object> auditTrailReference = Map.of(
                "auditId", "audit-987654321",
                "timestamp", Instant.now().toString()
        );

        Map<String, Object> approvalResponse = Map.of(
                "status", "success",
                "auditTrail", auditTrailReference
        );

        // Mock the controller method to simulate approval API
        when(fpmDealsheetController.approveDealsheet(any())).thenReturn(
                ResponseEntity.ok(approvalResponse)
        );

        // Navigate to approval page (simulate login as compliance officer)
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(TEST_USER_ID);
        passwordInput.sendKeys("securePassword123");
        loginButton.click();

        // Wait for redirect to dashboard
        Thread.sleep(1500);

        // Navigate to dealsheet approval page
        driver.get(BASE_URL + "/dealsheets/" + TEST_DEALSHEET_ID + "/approve");

        // Fill approval form
        WebElement approvalStatusSelect = driver.findElement(By.id("approvalStatus"));
        approvalStatusSelect.sendKeys("APPROVED");

        WebElement commentsInput = driver.findElement(By.id("comments"));
        commentsInput.sendKeys("Approved after compliance review");

        WebElement submitButton = driver.findElement(By.id("submitApproval"));
        submitButton.click();

        // Wait for API call and response
        Thread.sleep(2000);

        // Verify success message displayed
        WebElement successMsg = driver.findElement(By.id("approvalSuccessMsg"));
        assertThat(successMsg.getText()).contains("Approval successful");

        // Verify audit trail reference displayed
        WebElement auditRefElem = driver.findElement(By.id("auditTrailRef"));
        assertThat(auditRefElem.getText()).contains("audit-987654321");

        // Query Audit_Logs table for new entry
        String sql = "SELECT action_type, entity_type, entity_id, performed_by_user_id, previous_value, new_value, timestamp FROM "
                + AUDIT_LOGS_TABLE + " WHERE entity_id = ? ORDER BY timestamp DESC LIMIT 1";

        Map<String, Object> auditLogEntry = jdbcTemplate.queryForMap(sql, TEST_DEALSHEET_ID);

        // Assertions on audit log entry
        assertThat(auditLogEntry).isNotNull();
        assertThat(auditLogEntry.get("action_type")).isEqualTo("approval");
        assertThat(auditLogEntry.get("entity_type")).isEqualTo("dealsheet");
        assertThat(auditLogEntry.get("entity_id")).isEqualTo(TEST_DEALSHEET_ID);
        assertThat(auditLogEntry.get("performed_by_user_id")).isEqualTo(TEST_USER_ID);

        // previous_value and new_value should reflect state change (non-null JSON strings)
        assertThat(auditLogEntry.get("previous_value")).isInstanceOf(String.class);
        assertThat(auditLogEntry.get("new_value")).isInstanceOf(String.class);
        assertThat(((String) auditLogEntry.get("previous_value")).length()).isGreaterThan(0);
        assertThat(((String) auditLogEntry.get("new_value")).length()).isGreaterThan(0);

        // timestamp should be recent (within last 5 minutes)
        Instant timestamp = Instant.parse((String) auditLogEntry.get("timestamp"));
        Instant now = Instant.now();
        assertThat(timestamp).isBeforeOrEqualTo(now);
        assertThat(timestamp).isAfter(now.minusSeconds(300));

        // Verify immutability and access controls - simulate by attempting update (should fail)
        int updatedRows = jdbcTemplate.update("UPDATE " + AUDIT_LOGS_TABLE + " SET action_type = ? WHERE entity_id = ?",
                "tampered", TEST_DEALSHEET_ID);
        assertThat(updatedRows).isEqualTo(0);

        // Verify indexes exist for efficient querying (simple check)
        Integer indexCount = jdbcTemplate.queryForObject(
                "SHOW INDEX FROM " + AUDIT_LOGS_TABLE + " WHERE Column_name = 'entity_id'", Integer.class);
        assertThat(indexCount).isNotNull().isGreaterThan(0);
    }
}
