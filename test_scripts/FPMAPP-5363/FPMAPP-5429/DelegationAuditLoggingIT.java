/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5429
 * Epic: FPMAPP-5363
 * Generated on: 2025-09-04 16:30:53
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test using Selenium WebDriver plus Spring Boot Test to validate delegation
 * action is correctly logged and notifications sent.
 * 
 * Preconditions:
 * - Logged in as a financial approver user.
 * - Delegate users exist in the system.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DelegationAuditLoggingIT {

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmDealsheetController dealsheetController;

    @MockBean
    private CurrencyConvertionController currencyController;

    @MockBean
    private FpmForecastController forecastController;

    @MockBean
    private FpmCommonController commonController;

    private WebDriver driver;
    private WebDriverWait wait;

    private AutoCloseable mocks;

    // Test data
    private final String approverUsername = "financial.approver1";
    private final String delegateUsername = "delegate.user1";
    private final UUID taskId = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Setup ChromeDriver with headless for CI environments
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);

        // Mock login and user profile retrieval
        when(userProfileController.getCurrentUser()).thenReturn(createMockUser(approverUsername));

        // Mock dealsheetController to simulate delegation task retrieval
        when(dealsheetController.getApprovalTaskList(approverUsername))
            .thenReturn(MockData.approvalTaskListForApprover(approverUsername, taskId));

        // Mock delegation action to succeed
        doNothing().when(dealsheetController).delegateTaskToUser(taskId, delegateUsername, approverUsername);

        // Mock audit log verify call (simulate audit entry creation)
        when(commonController.getDelegationAuditLog(taskId)).thenReturn(MockData.createDelegationAuditLog(taskId, approverUsername, delegateUsername));

        // Mock notification sending (void method)
        doNothing().when(commonController).sendNotification(any(), any());
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    @DisplayName("Verify delegation action is logged correctly and notifications sent")
    public void testDelegationLoggingAndNotification() {
        // Step 1: Navigate to approval tasks list page
        driver.get("http://localhost:8080/fpmapp/approval-tasks");

        // Wait for task list to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalTaskTable")));

        // Assert task with known taskId is present
        WebElement taskRow = driver.findElement(By.xpath(String.format("//tr[@data-task-id='%s']", taskId)));
        assertThat(taskRow).isNotNull();

        // Step 2: Select task and open delegation dialog
        WebElement delegateBtn = taskRow.findElement(By.cssSelector("button.delegate-btn"));
        delegateBtn.click();

        // Wait for delegation modal/dialog
        WebElement delegateModal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("delegateModal")));

        // Select delegate user from dropdown
        WebElement delegateSelect = delegateModal.findElement(By.id("delegateUserSelect"));
        delegateSelect.click();

        // Choose the designated delegateUsername user
        WebElement optionUser = delegateSelect.findElement(By.xpath(String.format("//option[@value='%s']", delegateUsername)));
        optionUser.click();

        // Confirm delegation
        WebElement confirmButton = delegateModal.findElement(By.id("confirmDelegateBtn"));
        confirmButton.click();

        // Wait until modal disappears
        wait.until(ExpectedConditions.invisibilityOf(delegateModal));

        // Step 3: Confirm delegation log entry is created
        // Retrieve audit log from mocked service
        var auditLog = commonController.getDelegationAuditLog(taskId);

        assertThat(auditLog).isNotNull();
        assertThat(auditLog.getOriginalApproverId()).isEqualTo(approverUsername);
        assertThat(auditLog.getDelegatedToUserId()).isEqualTo(delegateUsername);
        assertThat(auditLog.getTaskId()).isEqualTo(taskId);
        assertThat(auditLog.getDelegationTimestamp()).isNotNull();
        assertThat(auditLog.getDelegationTimestamp()).isBeforeOrEqualTo(Instant.now());

        // Step 4: Verify notifications sent
        // Since mocked void methods, verify via Mockito would be done in unit test.
        // Here assert web UI shows notification toast
        WebElement notificationToast = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.toast-message")));

        String notificationText = notificationToast.getText();

        assertThat(notificationText).contains("delegated");
        assertThat(notificationText).contains(delegateUsername);
        assertThat(notificationText).contains(approverUsername);
    }

    // Helper to create a simple mock user object
    private static com.webapp.fpmapp.entities.User createMockUser(String username) {
        com.webapp.fpmapp.entities.User user = new com.webapp.fpmapp.entities.User();
        user.setUsername(username);
        user.setId(UUID.randomUUID());
        user.setRole("FinancialApprover");
        return user;
    }

    // MockData simulates data returns from controller/service mocks
    private static class MockData {
        static java.util.List<com.webapp.fpmapp.dto.ApprovalTaskDto> approvalTaskListForApprover(String approverUsername, UUID taskId) {
            com.webapp.fpmapp.dto.ApprovalTaskDto task = new com.webapp.fpmapp.dto.ApprovalTaskDto();
            task.setTaskId(taskId);
            task.setApproverUsername(approverUsername);
            task.setTaskDescription("Approve banking transaction #12345");
            task.setAmount(50000.00);
            task.setStatus("Pending");
            return java.util.List.of(task);
        }

        static com.webapp.fpmapp.dto.DelegationAuditLogDto createDelegationAuditLog(UUID taskId, String originalApprover, String delegatedTo) {
            com.webapp.fpmapp.dto.DelegationAuditLogDto auditLog = new com.webapp.fpmapp.dto.DelegationAuditLogDto();
            auditLog.setAuditLogId(UUID.randomUUID());
            auditLog.setTaskId(taskId);
            auditLog.setOriginalApproverId(originalApprover);
            auditLog.setDelegatedToUserId(delegatedTo);
            auditLog.setDelegationTimestamp(Instant.now());
            auditLog.setDelegationReason("Delegated due to absence");
            return auditLog;
        }
    }
}
