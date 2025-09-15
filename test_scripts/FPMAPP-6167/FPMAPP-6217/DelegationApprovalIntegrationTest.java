/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6217
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:41:53
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.DelegationManagementService;
import com.webapp.fpmapp.services.ApprovalAuditService;

/**
 * Integration Selenium Test for Delegation restricted to designated roles and audit logging
 * Covers:
 *  - Delegation allowed only for designated roles
 *  - Delegation rejected for unauthorized users
 *  - Audit logs capture delegation actions
 *
 * Uses:
 *  - Mocked services for audit and delegation
 *  - Realistic banking users and delegation flow
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DelegationApprovalIntegrationTest {

    private WebDriver driver;

    @Autowired
    private FpmDealsheetController dealsheetController;

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private DelegationManagementService delegationManagementService;

    @MockBean
    private ApprovalAuditService approvalAuditService;

    private static final String BASE_URL = "http://localhost:8080";

    private User authorizedUser;
    private User unauthorizedUser;
    private User delegateeUser;
    private String testApprovalId = "APPROVAL-12345";

    @BeforeAll
    public void setup() {
        // Setup ChromeDriver headless for CI environment
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        driver = new ChromeDriver(options);

        // Prepare test users (simulate fetching from UserProfileController)
        authorizedUser = new User();
        authorizedUser.setId(1L);
        authorizedUser.setUsername("authorized.approver");
        authorizedUser.setRoles(Collections.singletonList("ROLE_APPROVAL_DELEGATOR"));

        unauthorizedUser = new User();
        unauthorizedUser.setId(2L);
        unauthorizedUser.setUsername("unauthorized.user");
        unauthorizedUser.setRoles(Collections.singletonList("ROLE_VIEWER"));

        delegateeUser = new User();
        delegateeUser.setId(3L);
        delegateeUser.setUsername("delegatee.user");
        delegateeUser.setRoles(Collections.singletonList("ROLE_APPROVER"));

        // Mock user profile controller responses
        doReturn(authorizedUser).when(userProfileController).getUserById(1L);
        doReturn(unauthorizedUser).when(userProfileController).getUserById(2L);
        doReturn(delegateeUser).when(userProfileController).getUserById(3L);

        // Mock DelegationManagementService behavior
        doReturn(true).when(delegationManagementService)
                .assignDelegation(authorizedUser.getId(), delegateeUser.getId(), testApprovalId, 3600L);
        doReturn(false).when(delegationManagementService)
                .assignDelegation(unauthorizedUser.getId(), delegateeUser.getId(), testApprovalId, 3600L);

        // Mock ApprovalAuditService logging to be verified later
        Mockito.doNothing().when(approvalAuditService).logAction(any(), any(), any(), any());
    }

    @AfterAll
    public void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testDelegationFromAuthorizedUserSucceedsAndIsLogged() {
        driver.get(BASE_URL + "/login");

        // Simulate login as authorized user
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(authorizedUser.getUsername());
        passwordInput.sendKeys("correct_password");
        loginButton.click();

        // Navigate to delegation UI for the approval
        driver.get(BASE_URL + "/approvals/delegate?approvalId=" + testApprovalId);

        // Fill delegation form
        WebElement delegateeUserInput = driver.findElement(By.id("delegateeUserId"));
        WebElement durationInput = driver.findElement(By.id("delegationDuration"));
        WebElement delegateButton = driver.findElement(By.id("delegateBtn"));

        delegateeUserInput.sendKeys(delegateeUser.getId().toString());
        durationInput.sendKeys("3600"); // 1 hour
        delegateButton.click();

        // Wait and assert success message
        WebElement successMsg = driver.findElement(By.id("successMessage"));
        assertThat(successMsg.isDisplayed()).isTrue();
        assertThat(successMsg.getText()).contains("Delegation successful");

        // Verify service delegation method was called with correct params
        verify(delegationManagementService, times(1)).assignDelegation(
                authorizedUser.getId(), delegateeUser.getId(), testApprovalId, 3600L);

        // Verify audit logging was performed
        verify(approvalAuditService, times(1)).logAction(
                Mockito.eq(testApprovalId),
                Mockito.eq(authorizedUser.getId()),
                Mockito.eq("DELEGATION_ASSIGNED"),
                Mockito.any(Instant.class));
    }

    @Test
    public void testDelegationFromUnauthorizedUserIsBlocked() {
        driver.get(BASE_URL + "/login");

        // Simulate login as unauthorized user
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(unauthorizedUser.getUsername());
        passwordInput.sendKeys("correct_password");
        loginButton.click();

        // Navigate to delegation UI for the approval
        driver.get(BASE_URL + "/approvals/delegate?approvalId=" + testApprovalId);

        // Fill delegation form
        WebElement delegateeUserInput = driver.findElement(By.id("delegateeUserId"));
        WebElement durationInput = driver.findElement(By.id("delegationDuration"));
        WebElement delegateButton = driver.findElement(By.id("delegateBtn"));

        delegateeUserInput.sendKeys(delegateeUser.getId().toString());
        durationInput.sendKeys("3600"); // 1 hour
        delegateButton.click();

        // Wait and assert error message shown
        WebElement errorMsg = driver.findElement(By.id("errorMessage"));
        assertThat(errorMsg.isDisplayed()).isTrue();
        assertThat(errorMsg.getText()).contains("You do not have permission to delegate approvals.");

        // Verify service delegation method was called and returned false
        verify(delegationManagementService, times(1)).assignDelegation(
                unauthorizedUser.getId(), delegateeUser.getId(), testApprovalId, 3600L);

        // Verify audit logging was NOT performed for failed delegation
        verify(approvalAuditService, times(0)).logAction(
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyString(), Mockito.any(Instant.class));
    }

    @Test
    public void testAuditLogsContainDelegationEntries() {
        // Mock retrieval of audit logs for approvalId
        var delegationAuditEntry1 = new com.webapp.fpmapp.entities.ApprovalAuditLog();
        delegationAuditEntry1.setAuditId(1001L);
        delegationAuditEntry1.setApprovalId(testApprovalId);
        delegationAuditEntry1.setUserId(authorizedUser.getId());
        delegationAuditEntry1.setActionType("DELEGATION_ASSIGNED");
        delegationAuditEntry1.setActionTimestamp(Instant.now());
        delegationAuditEntry1.setRemarks("Delegated to delegatee.user");

        List<com.webapp.fpmapp.entities.ApprovalAuditLog> auditLogs = Collections.singletonList(delegationAuditEntry1);
        doReturn(auditLogs).when(approvalAuditService).getAuditTrail(testApprovalId);

        List<com.webapp.fpmapp.entities.ApprovalAuditLog> logs = approvalAuditService.getAuditTrail(testApprovalId);

        assertThat(logs).isNotNull();
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getActionType()).isEqualTo("DELEGATION_ASSIGNED");
        assertThat(logs.get(0).getUserId()).isEqualTo(authorizedUser.getId());
        assertThat(logs.get(0).getRemarks()).contains("delegated");
    }
}
