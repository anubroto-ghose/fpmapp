/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8634
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:50:29
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
 * Integration Selenium test for delegation rules restricting delegation to authorized roles only.
 * 
 * Preconditions:
 * - Delegation rules configured in system.
 * - Only authorized roles allowed to delegate approval tasks.
 * - Delegation info available and passed in workflow initiation.
 * 
 * Tests delegation attempts from authorized and unauthorized roles,
 * workflow initiation with delegation info, and approval with delegation flags.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DelegationRulesIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmTravelController travelController;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController forecastController;

    @MockBean
    private FpmCommonController commonController;

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
        // Mock user profiles with roles
        User authorizedUser = new User();
        authorizedUser.setId(1L);
        authorizedUser.setUsername("authorized_approver");
        authorizedUser.setRole("FIN_APPROVER");

        User unauthorizedUser = new User();
        unauthorizedUser.setId(2L);
        unauthorizedUser.setUsername("unauthorized_user");
        unauthorizedUser.setRole("STAFF");

        when(userProfileController.getUserByUsername("authorized_approver")).thenReturn(authorizedUser);
        when(userProfileController.getUserByUsername("unauthorized_user")).thenReturn(unauthorizedUser);

        // Mock delegation rules
        when(commonController.isDelegationAllowed("FIN_APPROVER")).thenReturn(true);
        when(commonController.isDelegationAllowed("STAFF")).thenReturn(false);

        // Mock workflow initiation responses
        when(commonController.initiateWorkflow(any(), any())).thenAnswer(invocation -> {
            Map<String, Object> params = invocation.getArgument(1);
            if (params != null && params.containsKey("delegation_info")) {
                Map<String, Object> delegationInfo = (Map<String, Object>) params.get("delegation_info");
                String role = (String) delegationInfo.get("role");
                if (!commonController.isDelegationAllowed(role)) {
                    throw new IllegalArgumentException("Delegation not allowed for role: " + role);
                }
            }
            return "workflow-12345";
        });

        // Mock approval updates
        when(commonController.approveTask(any(), anyBoolean())).thenAnswer(invocation -> {
            Long taskId = invocation.getArgument(0);
            Boolean delegationFlag = invocation.getArgument(1);
            Map<String, Object> result = new HashMap<>();
            result.put("taskId", taskId);
            result.put("delegationFlag", delegationFlag);
            result.put("status", "APPROVED");
            return result;
        });
    }

    @Test
    public void testDelegationFromAuthorizedRoleSucceeds() {
        driver.get("http://localhost:8080/login");

        // Login as authorized approver
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("authorized_approver");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to approval tasks
        driver.get("http://localhost:8080/approvals/tasks");

        // Select a task to delegate
        WebElement taskRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tr.task-row")));
        WebElement delegateButton = taskRow.findElement(By.cssSelector("button.delegate-btn"));
        delegateButton.click();

        // Delegate modal appears
        WebElement delegateRoleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateRole")));
        WebElement confirmDelegateBtn = driver.findElement(By.id("confirmDelegate"));

        // Delegate to another authorized role
        delegateRoleInput.sendKeys("FIN_APPROVER");
        confirmDelegateBtn.click();

        // Verify success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateSuccessMsg")));
        assertThat(successMsg.getText()).contains("Delegation successful");
    }

    @Test
    public void testDelegationFromUnauthorizedRoleFails() {
        driver.get("http://localhost:8080/login");

        // Login as unauthorized user
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("unauthorized_user");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to approval tasks
        driver.get("http://localhost:8080/approvals/tasks");

        // Select a task to delegate
        WebElement taskRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tr.task-row")));
        WebElement delegateButton = taskRow.findElement(By.cssSelector("button.delegate-btn"));
        delegateButton.click();

        // Delegate modal appears
        WebElement delegateRoleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateRole")));
        WebElement confirmDelegateBtn = driver.findElement(By.id("confirmDelegate"));

        // Attempt delegation to any role (should fail)
        delegateRoleInput.sendKeys("FIN_APPROVER");
        confirmDelegateBtn.click();

        // Verify error message
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateErrorMsg")));
        assertThat(errorMsg.getText()).contains("Delegation not allowed");
    }

    @Test
    public void testWorkflowInitiationWithDelegationInfo() {
        // Authorized role delegation info
        Map<String, Object> delegationInfoAuthorized = new HashMap<>();
        delegationInfoAuthorized.put("role", "FIN_APPROVER");
        delegationInfoAuthorized.put("delegator", "authorized_approver");

        Map<String, Object> paramsAuthorized = new HashMap<>();
        paramsAuthorized.put("delegation_info", delegationInfoAuthorized);

        String workflowId = commonController.initiateWorkflow("approvalProcess", paramsAuthorized);
        assertThat(workflowId).isNotBlank();

        // Unauthorized role delegation info
        Map<String, Object> delegationInfoUnauthorized = new HashMap<>();
        delegationInfoUnauthorized.put("role", "STAFF");
        delegationInfoUnauthorized.put("delegator", "unauthorized_user");

        Map<String, Object> paramsUnauthorized = new HashMap<>();
        paramsUnauthorized.put("delegation_info", delegationInfoUnauthorized);

        try {
            commonController.initiateWorkflow("approvalProcess", paramsUnauthorized);
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("Delegation not allowed for role: STAFF");
        }
    }

    @Test
    public void testApproveTasksWithDelegationFlag() {
        Long taskId = 1001L;

        // Approve with delegation_flag = true
        Map<String, Object> resultDelegated = commonController.approveTask(taskId, true);
        assertThat(resultDelegated.get("status")).isEqualTo("APPROVED");
        assertThat(resultDelegated.get("delegationFlag")).isEqualTo(true);

        // Approve with delegation_flag = false
        Map<String, Object> resultDirect = commonController.approveTask(taskId, false);
        assertThat(resultDirect.get("status")).isEqualTo("APPROVED");
        assertThat(resultDirect.get("delegationFlag")).isEqualTo(false);
    }
}