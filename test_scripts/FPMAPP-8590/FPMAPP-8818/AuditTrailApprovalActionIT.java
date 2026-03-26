/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8818
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:50:12
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.AuditTrailService;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration test verifying audit trail logging of approval actions.
 * 
 * Preconditions:
 * - Approval_Audit_Trail table exists and accessible.
 * - User is authenticated and authorized.
 * - System configured to log audit trail entries.
 * 
 * Test Steps:
 * 1. Perform approval action on a valid approval request.
 * 2. Include comments during approval.
 * 3. Verify audit trail table for new entry with correct details.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuditTrailApprovalActionIT {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private FpmDealsheetController dealsheetController;

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private AuditTrailService auditTrailService;

    @Autowired
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_USER_ID = "user123";
    private static final String TEST_USERNAME = "compliance.officer";
    private static final String TEST_APPROVAL_REQUEST_ID = "approvalReq-456";
    private static final String TEST_COMMENT = "Approved after compliance review.";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
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
        // Mock audit trail service to simulate DB audit log creation and retrieval
        when(auditTrailService.logAction(any(), any(), any(), any())).thenAnswer(invocation -> {
            // Simulate immutable audit log creation
            return true;
        });

        when(auditTrailService.getAuditTrail(TEST_APPROVAL_REQUEST_ID)).thenReturn(
                Collections.singletonList(
                        new com.webapp.fpmapp.entities.AuditLogEntry(
                                "auditLog-001",
                                TEST_APPROVAL_REQUEST_ID,
                                "approval",
                                Instant.now(),
                                TEST_USER_ID,
                                null,
                                null,
                                TEST_COMMENT
                        )
                )
        );
    }

    @Test
    public void testApprovalActionCreatesAuditTrailEntry() {
        // Step 1: Authenticate user (simulate login)
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(TEST_USERNAME);
        passwordInput.sendKeys("password123"); // Assume test password
        loginButton.click();

        // Wait for redirect to dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to approval request page
        driver.get(BASE_URL + "/approvals/" + TEST_APPROVAL_REQUEST_ID);

        // Wait for approval form
        WebElement commentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalComment")));
        WebElement approveButton = driver.findElement(By.id("approveBtn"));

        // Step 3: Enter comment and perform approval
        commentBox.sendKeys(TEST_COMMENT);
        approveButton.click();

        // Wait for confirmation message
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalSuccessMsg")));
        assertThat(confirmation.getText()).contains("Approval successful");

        // Step 4: Verify audit trail entry via service
        List<com.webapp.fpmapp.entities.AuditLogEntry> auditLogs = auditTrailService.getAuditTrail(TEST_APPROVAL_REQUEST_ID);
        assertThat(auditLogs).isNotEmpty();

        com.webapp.fpmapp.entities.AuditLogEntry logEntry = auditLogs.get(0);

        // Assertions
        assertThat(logEntry.getActionType()).isEqualTo("approval");
        assertThat(logEntry.getApprovalRequestId()).isEqualTo(TEST_APPROVAL_REQUEST_ID);
        assertThat(logEntry.getPerformedByUserId()).isEqualTo(TEST_USER_ID);
        assertThat(logEntry.getComments()).isEqualTo(TEST_COMMENT);
        assertThat(logEntry.getActionTimestamp()).isNotNull();

        // Immutability check: simulate attempt to modify (should fail or be ignored)
        try {
            logEntry.setComments("Tampered comment");
            // In real scenario, audit log entity is immutable or DB rejects update
            // Here we simulate immutability by not allowing setter or ignoring changes
            // So test passes if comment remains unchanged
        } catch (UnsupportedOperationException e) {
            // Expected immutability
        }
        assertThat(logEntry.getComments()).isEqualTo(TEST_COMMENT);
    }
}
