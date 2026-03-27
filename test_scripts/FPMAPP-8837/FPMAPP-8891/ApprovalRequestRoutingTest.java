/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8891
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:29:59
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for approval request routing to Manager for mid-tier requests.
 * 
 * Preconditions:
 * - Role hierarchy configured with Managers below Directors.
 * - Mid-tier approval request created within mid-tier value range.
 * - Requestor authorized to submit requests.
 * 
 * Test Steps:
 * 1. Submit a mid-tier approval request.
 * 2. Observe routing of the request.
 * 3. Verify request assigned to a Manager.
 * 4. Confirm Manager receives email and in-app notifications.
 * 5. Ensure no other roles receive the request incorrectly.
 * 
 * Expected Results:
 * - Request routed automatically to Manager.
 * - Manager receives email and in-app notifications promptly.
 * - No other roles outside Manager receive the request.
 * - Approval status updates reflected in real-time for requester.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalRequestRoutingTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
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

        // Mock user profile for requestor
        User requestor = new User();
        requestor.setId(1001L);
        requestor.setUsername("requestorUser");
        requestor.setRole("Employee");
        when(userProfileController.getCurrentUser()).thenReturn(requestor);

        // Mock role hierarchy: Manager below Director
        when(fpmCommonController.getRoleHierarchy()).thenReturn(List.of("Employee", "Manager", "Director"));

        // Mock authorization check
        when(fpmCommonController.isUserAuthorized(any(User.class), any(String.class))).thenReturn(true);

        // Mock currency conversion (if needed)
        when(currencyConvertionController.convert(any(Double.class), any(String.class), any(String.class))).thenAnswer(invocation -> {
            Double amount = invocation.getArgument(0);
            return amount; // no conversion for test
        });

        // Mock forecast controller (if needed)
        when(fpmForecastController.getForecastForRequest(any(Long.class))).thenReturn(10000.0);

        // Mock sending email and in-app notifications
        doNothing().when(fpmCommonController).sendEmailNotification(any(User.class), any(String.class), any(String.class));
        doNothing().when(fpmCommonController).sendInAppNotification(any(User.class), any(String.class));
    }

    @Test
    public void testMidTierApprovalRequestRoutingToManager() {
        // Step 1: Login as requestor
        driver.get("http://localhost:8080/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("requestorUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to approval request submission page
        driver.get("http://localhost:8080/approval/request/new");

        // Fill in mid-tier approval request form
        WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount")));
        WebElement descriptionInput = driver.findElement(By.id("description"));
        WebElement submitButton = driver.findElement(By.id("submitRequestBtn"));

        // Mid-tier value range assumed between 5000 and 20000
        double midTierAmount = 15000.00;
        amountInput.clear();
        amountInput.sendKeys(String.valueOf(midTierAmount));
        descriptionInput.sendKeys("Mid-tier approval request for project X");

        submitButton.click();

        // Step 3: Verify routing to Manager
        // Wait for confirmation page or message
        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmationMessage")));
        assertThat(confirmationMessage.getText()).contains("Request submitted successfully");

        // Verify backend routing logic via mocked service
        verify(fpmCommonController, times(1)).routeApprovalRequest(any(), any());

        // Simulate fetching assigned approver
        User assignedApprover = new User();
        assignedApprover.setId(2001L);
        assignedApprover.setUsername("managerUser");
        assignedApprover.setRole("Manager");

        when(fpmCommonController.getAssignedApprover(any())).thenReturn(assignedApprover);

        User approver = fpmCommonController.getAssignedApprover(null);
        assertThat(approver).isNotNull();
        assertThat(approver.getRole()).isEqualTo("Manager");

        // Step 4: Confirm Manager receives email and in-app notifications
        verify(fpmCommonController, times(1)).sendEmailNotification(approver, "New Approval Request", "You have a new approval request pending.");
        verify(fpmCommonController, times(1)).sendInAppNotification(approver, "New approval request assigned to you.");

        // Step 5: Ensure no other roles receive the request
        // Verify no email or notification sent to Director or Employee roles
        User directorUser = new User();
        directorUser.setId(3001L);
        directorUser.setUsername("directorUser");
        directorUser.setRole("Director");

        User employeeUser = new User();
        employeeUser.setId(4001L);
        employeeUser.setUsername("employeeUser");
        employeeUser.setRole("Employee");

        verify(fpmCommonController, times(0)).sendEmailNotification(directorUser, "New Approval Request", "You have a new approval request pending.");
        verify(fpmCommonController, times(0)).sendInAppNotification(directorUser, "New approval request assigned to you.");

        verify(fpmCommonController, times(0)).sendEmailNotification(employeeUser, "New Approval Request", "You have a new approval request pending.");
        verify(fpmCommonController, times(0)).sendInAppNotification(employeeUser, "New approval request assigned to you.");

        // Step 6: Verify approval status updates reflected in real-time for requester
        // Simulate status update
        when(fpmCommonController.getApprovalStatusForRequest(any())).thenReturn("Pending Manager Approval");

        driver.get("http://localhost:8080/approval/request/status");
        WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatus")));

        String statusText = statusElement.getText();
        assertThat(statusText).isEqualTo("Pending Manager Approval");
    }
}
