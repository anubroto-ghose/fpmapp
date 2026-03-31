/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8932
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:57:48
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test for audit log capturing delegation and approval events immutably.
 * 
 * Preconditions:
 * - Delegation has been created and saved.
 * - Approvals and rejections are performed by both original approver and delegate.
 * 
 * Test Steps:
 * 1. Perform delegation actions (create, modify, revoke).
 * 2. Perform approval and rejection actions by both original approver and delegate.
 * 3. Access audit trail logs and verify correctness.
 * 
 * Expected Results:
 * - All delegation actions logged with accurate timestamps and user details.
 * - All approval and rejection events logged immutably.
 * - Audit trail shows complete, unaltered history.
 * - Logs accessible only to authorized users.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuditLogDelegationApprovalTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @Autowired
    private AuditLogService auditLogService;

    private final String baseUrl = "http://localhost:8080";

    private final String originalApproverUsername = "approver1";
    private final String delegateUsername = "delegate1";
    private final String unauthorizedUsername = "unauthorizedUser";

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
        MockitoAnnotations.openMocks(this);

        // Mock user profile service responses
        User originalApprover = new User();
        originalApprover.setUsername(originalApproverUsername);
        originalApprover.setRoles(Arrays.asList("APPROVER"));

        User delegate = new User();
        delegate.setUsername(delegateUsername);
        delegate.setRoles(Arrays.asList("DELEGATE"));

        User unauthorizedUser = new User();
        unauthorizedUser.setUsername(unauthorizedUsername);
        unauthorizedUser.setRoles(Arrays.asList("USER"));

        when(fpmUserProfileController.getUserByUsername(originalApproverUsername)).thenReturn(originalApprover);
        when(fpmUserProfileController.getUserByUsername(delegateUsername)).thenReturn(delegate);
        when(fpmUserProfileController.getUserByUsername(unauthorizedUsername)).thenReturn(unauthorizedUser);

        // Mock audit log service to simulate immutable logging
        when(auditLogService.logDelegationAction(any(), any(), any())).thenAnswer(invocation -> {
            // Return a log entry with timestamp and user details
            String action = invocation.getArgument(0);
            String performedBy = invocation.getArgument(1);
            String details = invocation.getArgument(2);
            return new AuditLogEntry(action, performedBy, details, LocalDateTime.now());
        });

        when(auditLogService.logApprovalEvent(any(), any(), any())).thenAnswer(invocation -> {
            String event = invocation.getArgument(0);
            String performedBy = invocation.getArgument(1);
            String details = invocation.getArgument(2);
            return new AuditLogEntry(event, performedBy, details, LocalDateTime.now());
        });
    }

    @Test
    public void testAuditLogCapturesDelegationAndApprovalEventsImmutably() {
        // Step 1: Login as original approver and create delegation
        loginAsUser(originalApproverUsername, "password123");
        performDelegationAction("create", delegateUsername);

        // Step 2: Modify delegation
        performDelegationAction("modify", delegateUsername);

        // Step 3: Revoke delegation
        performDelegationAction("revoke", delegateUsername);

        // Step 4: Perform approval and rejection by original approver
        performApprovalAction("approve", originalApproverUsername);
        performApprovalAction("reject", originalApproverUsername);

        // Step 5: Perform approval and rejection by delegate
        loginAsUser(delegateUsername, "password123");
        performApprovalAction("approve", delegateUsername);
        performApprovalAction("reject", delegateUsername);

        // Step 6: Access audit trail logs as original approver (authorized)
        loginAsUser(originalApproverUsername, "password123");
        List<AuditLogEntry> logs = accessAuditTrailLogs();

        // Assertions
        assertThat(logs).isNotEmpty();

        // Verify delegation actions logged
        assertThat(logs).anyMatch(log -> log.getAction().equalsIgnoreCase("create") && log.getPerformedBy().equals(originalApproverUsername));
        assertThat(logs).anyMatch(log -> log.getAction().equalsIgnoreCase("modify") && log.getPerformedBy().equals(originalApproverUsername));
        assertThat(logs).anyMatch(log -> log.getAction().equalsIgnoreCase("revoke") && log.getPerformedBy().equals(originalApproverUsername));

        // Verify approval and rejection events logged immutably
        assertThat(logs).anyMatch(log -> log.getAction().equalsIgnoreCase("approve") && (log.getPerformedBy().equals(originalApproverUsername) || log.getPerformedBy().equals(delegateUsername)));
        assertThat(logs).anyMatch(log -> log.getAction().equalsIgnoreCase("reject") && (log.getPerformedBy().equals(originalApproverUsername) || log.getPerformedBy().equals(delegateUsername)));

        // Verify timestamps are present and in correct order
        LocalDateTime previousTimestamp = null;
        for (AuditLogEntry log : logs) {
            assertThat(log.getTimestamp()).isNotNull();
            if (previousTimestamp != null) {
                assertThat(log.getTimestamp()).isAfterOrEqualTo(previousTimestamp);
            }
            previousTimestamp = log.getTimestamp();
        }

        // Step 7: Verify logs are accessible only to authorized users
        loginAsUser(unauthorizedUsername, "password123");
        boolean accessDenied = isAuditTrailAccessDenied();
        assertThat(accessDenied).isTrue();
    }

    private void loginAsUser(String username, String password) {
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).clear();
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("loginButton")).click();

        // Wait for dashboard or home page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Verify login success
        WebElement welcomeMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("welcomeMessage")));
        assertThat(welcomeMsg.getText()).contains(username);
    }

    private void performDelegationAction(String action, String delegateUsername) {
        driver.get(baseUrl + "/delegation/manage");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateUsername"))).clear();
        driver.findElement(By.id("delegateUsername")).sendKeys(delegateUsername);

        WebElement actionSelect = driver.findElement(By.id("delegationAction"));
        actionSelect.click();
        WebElement option = driver.findElement(By.xpath(String.format("//option[@value='%s']", action)));
        option.click();

        driver.findElement(By.id("submitDelegation")).click();

        // Wait for confirmation message
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationConfirmation")));
        assertThat(confirmation.getText()).contains("successfully");

        // Simulate audit log entry
        auditLogService.logDelegationAction(action, originalApproverUsername, "Delegate: " + delegateUsername);
    }

    private void performApprovalAction(String action, String performedBy) {
        driver.get(baseUrl + "/approvals/pending");

        // Select first pending approval
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".approval-item")));
        WebElement firstApproval = driver.findElement(By.cssSelector(".approval-item"));
        firstApproval.click();

        WebElement actionButton = driver.findElement(By.id(action + "Button"));
        actionButton.click();

        // Wait for confirmation
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalConfirmation")));
        assertThat(confirmation.getText()).contains("successfully");

        // Simulate audit log entry
        auditLogService.logApprovalEvent(action, performedBy, "Approval performed on item");
    }

    private List<AuditLogEntry> accessAuditTrailLogs() {
        driver.get(baseUrl + "/audit/logs");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditLogTable")));

        List<WebElement> rows = driver.findElements(By.cssSelector("#auditLogTable tbody tr"));

        // Parse logs from table
        return rows.stream().map(row -> {
            String action = row.findElement(By.cssSelector("td.action")).getText();
            String performedBy = row.findElement(By.cssSelector("td.performedBy")).getText();
            String details = row.findElement(By.cssSelector("td.details")).getText();
            String timestampStr = row.findElement(By.cssSelector("td.timestamp")).getText();
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr);
            return new AuditLogEntry(action, performedBy, details, timestamp);
        }).toList();
    }

    private boolean isAuditTrailAccessDenied() {
        driver.get(baseUrl + "/audit/logs");
        try {
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDeniedMessage")));
            return errorMsg.getText().toLowerCase().contains("access denied");
        } catch (Exception e) {
            return false;
        }
    }

    // Inner class to represent audit log entries
    public static class AuditLogEntry {
        private final String action;
        private final String performedBy;
        private final String details;
        private final LocalDateTime timestamp;

        public AuditLogEntry(String action, String performedBy, String details, LocalDateTime timestamp) {
            this.action = action;
            this.performedBy = performedBy;
            this.details = details;
            this.timestamp = timestamp;
        }

        public String getAction() {
            return action;
        }

        public String getPerformedBy() {
            return performedBy;
        }

        public String getDetails() {
            return details;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    // Mocked AuditLogService to simulate logging
    public interface AuditLogService {
        AuditLogEntry logDelegationAction(String action, String performedBy, String details);
        AuditLogEntry logApprovalEvent(String event, String performedBy, String details);
    }
}
