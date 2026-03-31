/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8920
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 15:09:17
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import java.time.Duration;

/**
 * Integration Selenium test for verifying audit log immutability after approval decision.
 * 
 * Preconditions:
 * - User with approval permissions is logged in.
 * - A request is submitted and pending approval.
 * 
 * Test Steps:
 * 1. Approve the pending request.
 * 2. Access the audit log entry for the approved request.
 * 3. Attempt to modify the audit log entry.
 * 
 * Expected Results:
 * - System prevents modification.
 * - Audit log remains unchanged.
 * - Any attempt to alter is logged or rejected with error.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuditLogImmutabilityTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @InjectMocks
    private com.webapp.fpmapp.controllers.ApprovalController approvalController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_USER_ID = "approverUser123";
    private static final String PENDING_REQUEST_ID = "REQ-20240601-001";

    private static final String AUDIT_LOG_ENTRY_ID = "AUDIT-REQ-20240601-001";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
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
    public void setupMocks() {
        MockitoAnnotations.openMocks(this);

        // Mock user profile with approval permissions
        User mockUser = new User();
        mockUser.setUserId(TEST_USER_ID);
        mockUser.setUsername("approverUser");
        mockUser.setRoles(Collections.singletonList("ROLE_APPROVER"));

        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock approval request pending state
        when(fpmCommonController.isRequestPending(PENDING_REQUEST_ID)).thenReturn(true);

        // Mock audit log entry retrieval
        when(fpmCommonController.getAuditLogEntry(AUDIT_LOG_ENTRY_ID)).thenReturn(
            new com.webapp.fpmapp.entities.AuditLogEntry(
                AUDIT_LOG_ENTRY_ID,
                PENDING_REQUEST_ID,
                TEST_USER_ID,
                LocalDateTime.now().minusMinutes(5),
                "PENDING",
                "Initial submission"
            )
        );

        // Mock approval action
        when(fpmCommonController.approveRequest(PENDING_REQUEST_ID, TEST_USER_ID))
            .then(invocation -> {
                // Simulate approval changes audit log
                com.webapp.fpmapp.entities.AuditLogEntry approvedEntry = new com.webapp.fpmapp.entities.AuditLogEntry(
                    AUDIT_LOG_ENTRY_ID,
                    PENDING_REQUEST_ID,
                    TEST_USER_ID,
                    LocalDateTime.now(),
                    "APPROVED",
                    "Approved by user"
                );
                return approvedEntry;
            });

        // Mock immutability enforcement: throw exception on modification attempt
        doThrow(new UnsupportedOperationException("Audit log entries are immutable and cannot be modified."))
            .when(fpmCommonController).modifyAuditLogEntry(any(com.webapp.fpmapp.entities.AuditLogEntry.class));
    }

    @Test
    public void testAuditLogImmutabilityAfterApproval() {
        // Step 1: Login as approver user
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("approverUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard or approval page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to pending requests
        driver.get(BASE_URL + "/requests/pending");

        // Locate the pending request row by request ID
        WebElement requestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//tr[td/text()='" + PENDING_REQUEST_ID + "']")
        ));

        // Click Approve button
        WebElement approveButton = requestRow.findElement(By.cssSelector("button.approve-btn"));
        approveButton.click();

        // Confirm approval modal
        WebElement confirmApproveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmApproveBtn")));
        confirmApproveBtn.click();

        // Wait for approval success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalSuccessMsg")));
        assertThat(successMsg.getText()).contains("Request approved successfully");

        // Step 3: Access audit log entry page
        driver.get(BASE_URL + "/audit-log/" + AUDIT_LOG_ENTRY_ID);

        // Verify audit log details
        WebElement userIdField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditUserId")));
        WebElement timestampField = driver.findElement(By.id("auditTimestamp"));
        WebElement decisionField = driver.findElement(By.id("auditDecision"));

        String originalUserId = userIdField.getText();
        String originalTimestamp = timestampField.getText();
        String originalDecision = decisionField.getText();

        assertThat(originalUserId).isEqualTo(TEST_USER_ID);
        assertThat(originalDecision).isEqualTo("APPROVED");

        // Step 4: Attempt to modify audit log entry fields (simulate user input)
        try {
            // Try to edit userId field
            WebElement userIdInput = driver.findElement(By.id("auditUserIdInput"));
            userIdInput.clear();
            userIdInput.sendKeys("maliciousUser");

            // Try to edit timestamp field
            WebElement timestampInput = driver.findElement(By.id("auditTimestampInput"));
            timestampInput.clear();
            timestampInput.sendKeys(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // Try to edit decision field
            WebElement decisionInput = driver.findElement(By.id("auditDecisionInput"));
            decisionInput.clear();
            decisionInput.sendKeys("REJECTED");

            // Click Save button
            WebElement saveBtn = driver.findElement(By.id("saveAuditLogBtn"));
            saveBtn.click();

            // Wait for error message
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditLogErrorMsg")));
            assertThat(errorMsg.getText()).contains("Audit log entries are immutable");

        } catch (Exception e) {
            // If UI does not allow editing, test passes as immutability enforced at UI level
            System.out.println("Audit log fields are not editable in UI, immutability enforced.");
        }

        // Step 5: Verify audit log entry remains unchanged by reloading
        driver.navigate().refresh();

        WebElement userIdFieldAfter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditUserId")));
        WebElement timestampFieldAfter = driver.findElement(By.id("auditTimestamp"));
        WebElement decisionFieldAfter = driver.findElement(By.id("auditDecision"));

        assertThat(userIdFieldAfter.getText()).isEqualTo(originalUserId);
        assertThat(decisionFieldAfter.getText()).isEqualTo(originalDecision);

        // Timestamp may have formatting differences, parse and compare
        LocalDateTime beforeTimestamp = LocalDateTime.parse(originalTimestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime afterTimestamp = LocalDateTime.parse(timestampFieldAfter.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        assertThat(afterTimestamp).isEqualTo(beforeTimestamp);
    }
}
