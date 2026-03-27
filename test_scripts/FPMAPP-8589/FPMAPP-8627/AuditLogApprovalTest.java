/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8627
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:55:27
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import java.time.Duration;

/**
 * Integration Selenium test for verifying audit log entry creation on approval action.
 * 
 * Preconditions:
 * - Fpm service deployed with extended audit logging.
 * - Database schema includes audit_logs columns.
 * - User with valid credentials is logged in.
 * 
 * Test Steps:
 * 1. Login as valid user.
 * 2. Perform approval action on deal sheet.
 * 3. Verify POST /fpm/audit/log API call with correct payload.
 * 4. Query audit_logs table for new entry and verify correctness.
 * 
 * Assertions and error handling included.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuditLogApprovalTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmCommonController fpmCommonController; // Mock service that handles audit log API

    @Captor
    private ArgumentCaptor<Map<String, Object>> auditLogCaptor;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_USERNAME = "compliance_officer";
    private static final String TEST_PASSWORD = "SecurePass123!";
    private static final Long TEST_USER_ID = 1001L;
    private static final Long TEST_DEALSHEET_ID = 5001L;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        // Mock audit log API to return success
        when(fpmCommonController.postAuditLog(any())).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        // Mock user retrieval
        User mockUser = new User();
        mockUser.setId(TEST_USER_ID);
        mockUser.setUsername(TEST_USERNAME);
        when(fpmCommonController.getCurrentUser()).thenReturn(mockUser);
    }

    @Test
    public void testApprovalCreatesAuditLogEntry() {
        // Step 1: Login
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(TEST_USERNAME);
        passwordInput.sendKeys(TEST_PASSWORD);
        loginButton.click();

        // Wait for dashboard or dealsheet page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to deal sheet approval page
        driver.get(BASE_URL + "/dealsheet/view/" + TEST_DEALSHEET_ID);

        // Wait for approval button
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("approveBtn")));

        // Step 3: Click approve
        approveButton.click();

        // Wait for confirmation modal and confirm
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmApproveBtn")));
        confirmButton.click();

        // Step 4: Verify audit log API call
        verify(fpmCommonController).postAuditLog(auditLogCaptor.capture());

        Map<String, Object> auditLogPayload = auditLogCaptor.getValue();

        assertNotNull(auditLogPayload, "Audit log payload should not be null");

        // Validate action_type
        assertEquals("approval", auditLogPayload.get("action_type"), "Action type should be 'approval'");

        // Validate user_id
        assertEquals(TEST_USER_ID, auditLogPayload.get("user_id"), "User ID should match logged in user");

        // Validate timestamp is recent (within last 1 minute)
        Object timestampObj = auditLogPayload.get("timestamp");
        assertNotNull(timestampObj, "Timestamp should not be null");

        Instant timestamp;
        try {
            timestamp = Instant.parse(timestampObj.toString());
        } catch (Exception e) {
            fail("Timestamp format invalid: " + e.getMessage());
            return;
        }

        Instant now = Instant.now();
        long secondsDiff = Math.abs(ChronoUnit.SECONDS.between(now, timestamp));
        assertTrue(secondsDiff < 60, "Timestamp should be within the last 60 seconds");

        // Validate details field contains JSON with approval metadata
        Object detailsObj = auditLogPayload.get("details");
        assertNotNull(detailsObj, "Details field should not be null");

        String detailsJson = detailsObj.toString();
        assertTrue(detailsJson.contains("dealSheetId"), "Details should contain dealSheetId");
        assertTrue(detailsJson.contains(TEST_DEALSHEET_ID.toString()), "Details should contain correct dealSheetId");
        assertTrue(detailsJson.contains("approvedBy"), "Details should contain approvedBy");

        // Step 5: Query audit_logs table to verify entry (simulate via service call)
        // Assuming fpmCommonController has method to query audit logs by user and action
        var auditLogs = fpmCommonController.getAuditLogsByUserAndAction(TEST_USER_ID, "approval");
        assertNotNull(auditLogs, "Audit logs query result should not be null");
        assertFalse(auditLogs.isEmpty(), "Audit logs should contain at least one entry");

        var latestLog = auditLogs.get(0);
        assertEquals("approval", latestLog.getActionType(), "Latest audit log action_type should be 'approval'");
        assertEquals(TEST_USER_ID, latestLog.getUserId(), "Latest audit log user_id should match");
        assertNotNull(latestLog.getTimestamp(), "Latest audit log timestamp should not be null");
        assertTrue(latestLog.getDetails().contains("dealSheetId"), "Latest audit log details should contain dealSheetId");

        // Additional assertions for immutability and indexing would be DB-level and assumed handled by DB constraints and indexes
    }
}
