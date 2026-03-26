/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8823
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:53:54
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test for approval status reflecting detailed states and recording timestamps and roles.
 * 
 * Preconditions:
 * - Approval workflow system is active with status tracking enabled.
 * - Approval requests submitted and pending.
 * 
 * Test Steps:
 * 1. Approve an approval request and verify status changes to 'approved'.
 * 2. Reject an approval request and verify status changes to 'rejected'.
 * 3. Delegate an approval request (for roles with delegation permission) and verify status changes to 'delegated'.
 * 4. Check that each action records the approver role and timestamp.
 * 5. View the current approval status from a user perspective.
 * 
 * Expected Results:
 * - Approval statuses update correctly.
 * - Each status change logs the approver role and timestamp accurately.
 * - Users can view the current approval status reflecting the hierarchical workflow.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ApprovalStatusIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:";

    private static final String APPROVAL_REQUEST_ID = "req-12345";

    private static final String ROLE_MANAGER = "Manager";
    private static final String ROLE_DIRECTOR = "Director";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
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
    public void setupMocks() {
        // Mock approval request initial state: pending
        when(fpmDealsheetController.getApprovalStatus(APPROVAL_REQUEST_ID))
            .thenReturn(new ApprovalStatusResponse(APPROVAL_REQUEST_ID, "pending", null, null));

        // Mock approve action
        when(fpmDealsheetController.approveRequest(APPROVAL_REQUEST_ID, ROLE_MANAGER))
            .thenReturn(new ApprovalStatusResponse(APPROVAL_REQUEST_ID, "approved", ROLE_MANAGER, Instant.now()));

        // Mock reject action
        when(fpmDealsheetController.rejectRequest(APPROVAL_REQUEST_ID, ROLE_MANAGER))
            .thenReturn(new ApprovalStatusResponse(APPROVAL_REQUEST_ID, "rejected", ROLE_MANAGER, Instant.now()));

        // Mock delegate action
        when(fpmDealsheetController.delegateRequest(APPROVAL_REQUEST_ID, ROLE_MANAGER, "user-delegate-1"))
            .thenReturn(new ApprovalStatusResponse(APPROVAL_REQUEST_ID, "delegated", ROLE_MANAGER, Instant.now()));

        // Mock get current status after actions
        when(fpmDealsheetController.getApprovalStatus(APPROVAL_REQUEST_ID))
            .thenReturn(new ApprovalStatusResponse(APPROVAL_REQUEST_ID, "pending", null, null));
    }

    @Test
    public void testApprovalWorkflowStatusTransitions() throws InterruptedException {
        String baseUrl = BASE_URL + port + "/approval-requests/" + APPROVAL_REQUEST_ID;

        // Navigate to approval request page
        driver.get(baseUrl);

        // Verify initial status is pending
        WebElement statusElement = driver.findElement(By.id("approval-status"));
        assertThat(statusElement.getText()).isEqualToIgnoringCase("pending");

        // Step 1: Approve the request
        WebElement approveButton = driver.findElement(By.id("btn-approve"));
        approveButton.click();

        // Wait for status update (simulate async update)
        Thread.sleep(1000);

        // Verify status changed to approved
        statusElement = driver.findElement(By.id("approval-status"));
        assertThat(statusElement.getText()).isEqualToIgnoringCase("approved");

        // Verify approver role and timestamp displayed
        WebElement roleElement = driver.findElement(By.id("approver-role"));
        WebElement timestampElement = driver.findElement(By.id("approval-timestamp"));
        assertThat(roleElement.getText()).isEqualTo(ROLE_MANAGER);
        assertThat(timestampElement.getText()).isNotEmpty();

        // Step 2: Reject the request
        WebElement rejectButton = driver.findElement(By.id("btn-reject"));
        rejectButton.click();

        Thread.sleep(1000);

        // Verify status changed to rejected
        statusElement = driver.findElement(By.id("approval-status"));
        assertThat(statusElement.getText()).isEqualToIgnoringCase("rejected");

        // Verify approver role and timestamp updated
        roleElement = driver.findElement(By.id("approver-role"));
        timestampElement = driver.findElement(By.id("approval-timestamp"));
        assertThat(roleElement.getText()).isEqualTo(ROLE_MANAGER);
        assertThat(timestampElement.getText()).isNotEmpty();

        // Step 3: Delegate the request
        WebElement delegateButton = driver.findElement(By.id("btn-delegate"));
        delegateButton.click();

        // Simulate selecting delegate user
        WebElement delegateUserInput = driver.findElement(By.id("delegate-user-input"));
        delegateUserInput.sendKeys("user-delegate-1");

        WebElement confirmDelegateButton = driver.findElement(By.id("btn-confirm-delegate"));
        confirmDelegateButton.click();

        Thread.sleep(1000);

        // Verify status changed to delegated
        statusElement = driver.findElement(By.id("approval-status"));
        assertThat(statusElement.getText()).isEqualToIgnoringCase("delegated");

        // Verify approver role and timestamp updated
        roleElement = driver.findElement(By.id("approver-role"));
        timestampElement = driver.findElement(By.id("approval-timestamp"));
        assertThat(roleElement.getText()).isEqualTo(ROLE_MANAGER);
        assertThat(timestampElement.getText()).isNotEmpty();

        // Step 4: View current approval status from user perspective
        WebElement userViewStatus = driver.findElement(By.id("user-view-status"));
        assertThat(userViewStatus.getText()).containsIgnoringCase("delegated");

        // Additional assertions: audit trail entries visible
        List<WebElement> auditEntries = driver.findElements(By.cssSelector(".audit-trail-entry"));
        assertThat(auditEntries).isNotEmpty();

        boolean foundApprove = auditEntries.stream().anyMatch(e -> e.getText().toLowerCase().contains("approved") && e.getText().contains(ROLE_MANAGER));
        boolean foundReject = auditEntries.stream().anyMatch(e -> e.getText().toLowerCase().contains("rejected") && e.getText().contains(ROLE_MANAGER));
        boolean foundDelegate = auditEntries.stream().anyMatch(e -> e.getText().toLowerCase().contains("delegated") && e.getText().contains(ROLE_MANAGER));

        assertThat(foundApprove).isTrue();
        assertThat(foundReject).isTrue();
        assertThat(foundDelegate).isTrue();
    }

    // DTO for mocked approval status response
    public static class ApprovalStatusResponse {
        private String requestId;
        private String status;
        private String approverRole;
        private Instant timestamp;

        public ApprovalStatusResponse(String requestId, String status, String approverRole, Instant timestamp) {
            this.requestId = requestId;
            this.status = status;
            this.approverRole = approverRole;
            this.timestamp = timestamp;
        }

        public String getRequestId() {
            return requestId;
        }

        public String getStatus() {
            return status;
        }

        public String getApproverRole() {
            return approverRole;
        }

        public Instant getTimestamp() {
            return timestamp;
        }
    }
}
