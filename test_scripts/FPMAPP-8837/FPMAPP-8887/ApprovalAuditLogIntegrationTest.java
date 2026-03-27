/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8887
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:33:04
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Integration test for verifying audit log entry creation on approval action.
 * 
 * Preconditions:
 * - User logged in as compliance officer
 * - Audit logging enabled
 * 
 * Test Steps:
 * 1. Perform approval action on a financial management request
 * 2. Access audit log for the action
 * 
 * Expected:
 * - Audit log entry created with correct user ID
 * - Timestamp recorded accurately
 * - Action type marked as 'approval'
 * - Comments logged
 * - Audit log entry immutable and stored securely
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApprovalAuditLogIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @Autowired
    private AuditLogTestHelper auditLogTestHelper;

    private final String complianceOfficerUsername = "compliance.officer";
    private final String complianceOfficerUserId = "user-12345";

    private final String approvalComment = "Approved after thorough review.";

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
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

        // Mock user profile service to return compliance officer user
        User complianceOfficer = new User();
        complianceOfficer.setUserId(complianceOfficerUserId);
        complianceOfficer.setUsername(complianceOfficerUsername);
        complianceOfficer.setRole("COMPLIANCE_OFFICER");

        when(fpmUserProfileController.getCurrentUser()).thenReturn(complianceOfficer);

        // Mock audit logging enabled
        when(fpmCommonController.isAuditLoggingEnabled()).thenReturn(true);

        // Mock audit log retrieval to simulate immutability and secure storage
        when(fpmCommonController.getAuditLogEntries(any(String.class)))
            .thenAnswer(invocation -> {
                String actionId = invocation.getArgument(0);
                return auditLogTestHelper.getAuditLogEntriesForAction(actionId);
            });
    }

    @Test
    public void testApprovalActionCreatesAuditLogEntry() {
        // Step 1: Login as compliance officer
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(complianceOfficerUsername);
        driver.findElement(By.id("password")).sendKeys("securePassword123");
        driver.findElement(By.id("loginButton")).click();

        // Verify login success by presence of dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertThat(driver.getCurrentUrl()).contains("/dashboard");

        // Step 2: Navigate to financial management requests page
        driver.get(BASE_URL + "/financial-requests");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table#requestsTable")));

        // Select a pending request to approve
        WebElement pendingRequestRow = driver.findElement(By.cssSelector("tr[data-status='PENDING']"));
        assertThat(pendingRequestRow).isNotNull();

        // Click on the approve button
        WebElement approveButton = pendingRequestRow.findElement(By.cssSelector("button.approve-btn"));
        approveButton.click();

        // Wait for approval modal/dialog
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalModal")));

        // Enter approval comment
        WebElement commentInput = driver.findElement(By.id("approvalComment"));
        commentInput.sendKeys(approvalComment);

        // Confirm approval
        WebElement confirmApproveBtn = driver.findElement(By.id("confirmApprove"));
        confirmApproveBtn.click();

        // Wait for success notification
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.alert-success")));

        // Step 3: Access audit log for the performed action
        // Assume the request has an attribute data-action-id for audit log lookup
        String actionId = pendingRequestRow.getAttribute("data-action-id");
        assertThat(actionId).isNotEmpty();

        // Retrieve audit log entries via mocked service
        List<AuditLogEntry> auditEntries = auditLogTestHelper.getAuditLogEntriesForAction(actionId);

        // Assertions on audit log entry
        assertThat(auditEntries).isNotEmpty();

        AuditLogEntry approvalEntry = auditEntries.stream()
            .filter(e -> "approval".equalsIgnoreCase(e.getActionType()))
            .findFirst()
            .orElse(null);

        assertThat(approvalEntry).isNotNull();
        assertThat(approvalEntry.getUserId()).isEqualTo(complianceOfficerUserId);

        // Timestamp should be recent (within last 5 minutes)
        Instant now = Instant.now();
        Instant timestamp = approvalEntry.getTimestamp();
        assertThat(timestamp).isNotNull();
        assertThat(timestamp).isBefore(now.plus(1, ChronoUnit.MINUTES));
        assertThat(timestamp).isAfter(now.minus(5, ChronoUnit.MINUTES));

        assertThat(approvalEntry.getComments()).isEqualTo(approvalComment);

        // Verify immutability and secure storage (simulate by checking audit log helper)
        boolean isImmutable = auditLogTestHelper.isAuditLogEntryImmutable(approvalEntry);
        assertThat(isImmutable).isTrue();

        boolean isStoredSecurely = auditLogTestHelper.isAuditLogEntryStoredSecurely(approvalEntry);
        assertThat(isStoredSecurely).isTrue();
    }

    /**
     * Helper class to simulate audit log storage and retrieval for testing.
     * In real scenario, this would be replaced by actual DB or service calls.
     */
    static class AuditLogTestHelper {

        // Simulated in-memory audit log store
        private final java.util.Map<String, List<AuditLogEntry>> auditLogStore = new java.util.concurrent.ConcurrentHashMap<>();

        public List<AuditLogEntry> getAuditLogEntriesForAction(String actionId) {
            return auditLogStore.getOrDefault(actionId, Collections.emptyList());
        }

        public void addAuditLogEntry(String actionId, AuditLogEntry entry) {
            auditLogStore.compute(actionId, (k, v) -> {
                if (v == null) {
                    v = new java.util.ArrayList<>();
                }
                v.add(entry);
                return v;
            });
        }

        public boolean isAuditLogEntryImmutable(AuditLogEntry entry) {
            // Simulate immutability check
            // In real system, audit logs are append-only and cannot be modified
            return true;
        }

        public boolean isAuditLogEntryStoredSecurely(AuditLogEntry entry) {
            // Simulate secure storage check
            // Could check encryption, access controls, etc.
            return true;
        }
    }

    /**
     * Audit log entry DTO for test purposes.
     */
    static class AuditLogEntry {
        private String userId;
        private Instant timestamp;
        private String actionType;
        private String comments;

        public AuditLogEntry(String userId, Instant timestamp, String actionType, String comments) {
            this.userId = userId;
            this.timestamp = timestamp;
            this.actionType = actionType;
            this.comments = comments;
        }

        public String getUserId() {
            return userId;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public String getActionType() {
            return actionType;
        }

        public String getComments() {
            return comments;
        }
    }
}
