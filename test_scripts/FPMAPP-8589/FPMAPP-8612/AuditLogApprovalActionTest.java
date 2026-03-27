/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8612
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 08:05:54
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
import java.util.List;
import java.util.Optional;

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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test for verifying audit log creation on approval action.
 * 
 * Preconditions:
 * - User with auditor role is logged in.
 * - Approval action is available on a deal sheet.
 * - Audit logging framework and audit_logs table are integrated.
 * 
 * Test Steps:
 * 1. Perform approval action on a deal sheet.
 * 2. Verify audit log entry creation with correct metadata.
 * 
 * This test uses mocked services and real DB query via JdbcTemplate.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuditLogApprovalActionTest {

    private static WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String TEST_USER_ID = "auditor123";
    private static final String TEST_DEALSHEET_ID = "deal-456";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
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
        // Mock the deal sheet approval action to simulate success
        when(fpmDealsheetController.approveDealSheet(TEST_DEALSHEET_ID, TEST_USER_ID))
            .thenReturn(true);

        // Mock common controller to simulate audit log creation
        when(fpmCommonController.logAuditAction(any())).thenAnswer(invocation -> {
            // Simulate audit log insertion into DB
            AuditLogEntry entry = invocation.getArgument(0);
            jdbcTemplate.update("INSERT INTO audit_logs (action_type, user_id, timestamp, delegation_info, request_id) VALUES (?, ?, ?, ?, ?)",
                    entry.getActionType(), entry.getUserId(), entry.getTimestamp(), entry.getDelegationInfo(), entry.getRequestId());
            return true;
        });

        // Clean audit_logs table before each test
        jdbcTemplate.update("DELETE FROM audit_logs WHERE user_id = ?", TEST_USER_ID);
    }

    @Test
    public void testApprovalActionCreatesAuditLog() {
        // Step 1: Log in as auditor user
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(TEST_USER_ID);
        passwordInput.sendKeys("securePassword123");
        loginButton.click();

        // Wait for login to complete and dashboard to load
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to deal sheet approval page
        driver.get(BASE_URL + "/dealsheets/" + TEST_DEALSHEET_ID);

        // Wait for approval button to be visible
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("approveBtn")));

        // Step 3: Perform approval action
        approveButton.click();

        // Wait for confirmation message
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalSuccessMsg")));
        assertThat(confirmation.getText()).contains("Approval successful");

        // Step 4: Verify audit log entry in DB
        List<AuditLogEntry> auditLogs = jdbcTemplate.query(
                "SELECT action_type, user_id, timestamp, delegation_info, request_id FROM audit_logs WHERE user_id = ? ORDER BY timestamp DESC LIMIT 1",
                new Object[] { TEST_USER_ID },
                (rs, rowNum) -> new AuditLogEntry(
                        rs.getString("action_type"),
                        rs.getString("user_id"),
                        rs.getTimestamp("timestamp").toInstant(),
                        rs.getString("delegation_info"),
                        rs.getString("request_id")
                ));

        assertThat(auditLogs).isNotEmpty();
        AuditLogEntry logEntry = auditLogs.get(0);

        // Assertions on audit log fields
        assertThat(logEntry.getActionType()).isEqualTo("approval");
        assertThat(logEntry.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(logEntry.getTimestamp()).isBeforeOrEqualTo(Instant.now());
        assertThat(logEntry.getRequestId()).isEqualTo(TEST_DEALSHEET_ID);
        // delegation_info can be null or empty if no delegation
        assertThat(logEntry.getDelegationInfo()).isNotNull();

        // Additional data integrity checks
        assertThat(logEntry.getActionType()).isNotEmpty();
        assertThat(logEntry.getUserId()).isNotEmpty();
        assertThat(logEntry.getRequestId()).isNotEmpty();
    }

    /**
     * Simple DTO for audit log entry used in test.
     */
    private static class AuditLogEntry {
        private final String actionType;
        private final String userId;
        private final Instant timestamp;
        private final String delegationInfo;
        private final String requestId;

        public AuditLogEntry(String actionType, String userId, Instant timestamp, String delegationInfo, String requestId) {
            this.actionType = actionType;
            this.userId = userId;
            this.timestamp = timestamp;
            this.delegationInfo = delegationInfo == null ? "" : delegationInfo;
            this.requestId = requestId;
        }

        public String getActionType() {
            return actionType;
        }

        public String getUserId() {
            return userId;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public String getDelegationInfo() {
            return delegationInfo;
        }

        public String getRequestId() {
            return requestId;
        }
    }
}
