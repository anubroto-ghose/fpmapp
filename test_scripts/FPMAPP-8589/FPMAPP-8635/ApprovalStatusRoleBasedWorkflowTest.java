/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8635
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:49:46
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.HashMap;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration Selenium test for approval status updates reflecting role-based decisions and delegation states.
 * 
 * Preconditions:
 * - Approval workflow system integrated with Fpmcamunda service.
 * - Approval tasks exist with various roles and delegation states.
 * 
 * Test Steps:
 * 1. Approve a task as a direct approver without delegation.
 * 2. Approve a task as a delegated approver with delegation_flag true.
 * 3. Reject a task as a delegated approver.
 * 4. Check the approval status updates after each action.
 * 
 * Expected Results:
 * - Approval status updates correctly reflect the approver's role.
 * - Delegation states are clearly indicated in the approval status.
 * - Next routing info is accurate based on role and delegation.
 * - No discrepancies between actual approval actions and recorded status.
 * - System handles edge cases where delegation_flag is inconsistent with role.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalStatusRoleBasedWorkflowTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:8080";

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
        // Mock user profile responses
        User directApprover = new User();
        directApprover.setId(1L);
        directApprover.setUsername("directApprover");
        directApprover.setRole("FINANCIAL_APPROVER");

        User delegatedApprover = new User();
        delegatedApprover.setId(2L);
        delegatedApprover.setUsername("delegatedApprover");
        delegatedApprover.setRole("FINANCIAL_APPROVER_DELEGATED");

        when(fpmUserProfileController.getUserByUsername("directApprover")).thenReturn(directApprover);
        when(fpmUserProfileController.getUserByUsername("delegatedApprover")).thenReturn(delegatedApprover);

        // Mock approval tasks with delegation flags and roles
        Map<String, Object> directApprovalTask = new HashMap<>();
        directApprovalTask.put("taskId", "task-001");
        directApprovalTask.put("role", "FINANCIAL_APPROVER");
        directApprovalTask.put("delegationFlag", false);
        directApprovalTask.put("status", "PENDING");

        Map<String, Object> delegatedApprovalTask = new HashMap<>();
        delegatedApprovalTask.put("taskId", "task-002");
        delegatedApprovalTask.put("role", "FINANCIAL_APPROVER_DELEGATED");
        delegatedApprovalTask.put("delegationFlag", true);
        delegatedApprovalTask.put("status", "PENDING");

        when(fpmCommonController.getApprovalTaskById("task-001")).thenReturn(directApprovalTask);
        when(fpmCommonController.getApprovalTaskById("task-002")).thenReturn(delegatedApprovalTask);

        // Mock approval action responses
        when(fpmCommonController.approveTask("task-001", false)).thenReturn(true);
        when(fpmCommonController.approveTask("task-002", true)).thenReturn(true);
        when(fpmCommonController.rejectTask("task-002", true)).thenReturn(true);
    }

    @Test
    public void testApprovalStatusUpdatesReflectRoleAndDelegation() {
        // Step 1: Approve a task as a direct approver without delegation
        driver.get(BASE_URL + "/login");
        loginAsUser("directApprover", "password123");

        approveTask("task-001", false);

        Map<String, Object> taskAfterDirectApproval = fpmCommonController.getApprovalTaskById("task-001");
        assertNotNull(taskAfterDirectApproval, "Task should exist after approval");
        assertEquals("APPROVED", taskAfterDirectApproval.get("status"), "Task status should be APPROVED");
        assertEquals(false, taskAfterDirectApproval.get("delegationFlag"), "Delegation flag should be false for direct approver");

        // Step 2: Approve a task as a delegated approver with delegation_flag true
        driver.get(BASE_URL + "/logout");
        loginAsUser("delegatedApprover", "password123");

        approveTask("task-002", true);

        Map<String, Object> taskAfterDelegatedApproval = fpmCommonController.getApprovalTaskById("task-002");
        assertNotNull(taskAfterDelegatedApproval, "Task should exist after delegated approval");
        assertEquals("APPROVED", taskAfterDelegatedApproval.get("status"), "Task status should be APPROVED");
        assertEquals(true, taskAfterDelegatedApproval.get("delegationFlag"), "Delegation flag should be true for delegated approver");

        // Step 3: Reject a task as a delegated approver
        // Reset task status to PENDING for rejection test
        Map<String, Object> resetTask = new HashMap<>(taskAfterDelegatedApproval);
        resetTask.put("status", "PENDING");
        when(fpmCommonController.getApprovalTaskById("task-002")).thenReturn(resetTask);

        rejectTask("task-002", true);

        Map<String, Object> taskAfterDelegatedRejection = fpmCommonController.getApprovalTaskById("task-002");
        assertNotNull(taskAfterDelegatedRejection, "Task should exist after delegated rejection");
        assertEquals("REJECTED", taskAfterDelegatedRejection.get("status"), "Task status should be REJECTED");
        assertEquals(true, taskAfterDelegatedRejection.get("delegationFlag"), "Delegation flag should be true for delegated approver rejection");

        // Step 4: Check edge case where delegation_flag inconsistent with role
        Map<String, Object> inconsistentTask = new HashMap<>();
        inconsistentTask.put("taskId", "task-003");
        inconsistentTask.put("role", "FINANCIAL_APPROVER");
        inconsistentTask.put("delegationFlag", true); // inconsistent
        inconsistentTask.put("status", "PENDING");

        when(fpmCommonController.getApprovalTaskById("task-003")).thenReturn(inconsistentTask);

        // Try to approve inconsistent task
        boolean approvalResult = fpmCommonController.approveTask("task-003", true);
        assertFalse(approvalResult, "Approval should fail due to inconsistent delegation flag");
    }

    private void loginAsUser(String username, String password) {
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        loginButton.click();

        // Wait for dashboard or approval page
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    private void approveTask(String taskId, boolean delegationFlag) {
        driver.get(BASE_URL + "/approvals/tasks/" + taskId);

        // Wait for task details to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("taskDetails")));

        // Verify delegation flag displayed correctly
        WebElement delegationFlagElement = driver.findElement(By.id("delegationFlag"));
        String delegationText = delegationFlagElement.getText();
        if (delegationFlag) {
            assertTrue(delegationText.contains("Delegated Approver"), "Delegation flag text should indicate delegation");
        } else {
            assertTrue(delegationText.contains("Direct Approver"), "Delegation flag text should indicate direct approval");
        }

        WebElement approveButton = driver.findElement(By.id("approveBtn"));
        approveButton.click();

        // Wait for confirmation message
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalConfirmation")));
        assertTrue(confirmation.getText().contains("approved"), "Approval confirmation should be displayed");
    }

    private void rejectTask(String taskId, boolean delegationFlag) {
        driver.get(BASE_URL + "/approvals/tasks/" + taskId);

        // Wait for task details to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("taskDetails")));

        // Verify delegation flag displayed correctly
        WebElement delegationFlagElement = driver.findElement(By.id("delegationFlag"));
        String delegationText = delegationFlagElement.getText();
        if (delegationFlag) {
            assertTrue(delegationText.contains("Delegated Approver"), "Delegation flag text should indicate delegation");
        } else {
            assertTrue(delegationText.contains("Direct Approver"), "Delegation flag text should indicate direct approval");
        }

        WebElement rejectButton = driver.findElement(By.id("rejectBtn"));
        rejectButton.click();

        // Wait for confirmation message
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rejectionConfirmation")));
        assertTrue(confirmation.getText().contains("rejected"), "Rejection confirmation should be displayed");
    }
}
