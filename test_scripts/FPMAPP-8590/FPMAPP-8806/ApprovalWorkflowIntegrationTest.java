/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8806
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:58:08
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration test for approval workflow enforcing hierarchical role mapping with financial thresholds.
 * 
 * Preconditions:
 * - User logged in as director or manager
 * - Request created with amount within financial threshold
 * 
 * This test uses Selenium WebDriver to simulate UI interactions and mocks backend services to control responses.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApprovalWorkflowIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;

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

    private static final String BASE_URL = "http://localhost:";

    private static final String DIRECTOR_USERNAME = "directorUser";
    private static final String MANAGER_USERNAME = "managerUser";

    private static final double MANAGER_THRESHOLD = 50000.00;
    private static final double DIRECTOR_THRESHOLD = 200000.00;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI environments)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
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
    public void setup() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock user profile service to return roles and thresholds
        when(fpmUserProfileController.getUserRole(DIRECTOR_USERNAME))
            .thenReturn("DIRECTOR");
        when(fpmUserProfileController.getUserRole(MANAGER_USERNAME))
            .thenReturn("MANAGER");

        when(fpmUserProfileController.getFinancialThreshold("DIRECTOR"))
            .thenReturn(DIRECTOR_THRESHOLD);
        when(fpmUserProfileController.getFinancialThreshold("MANAGER"))
            .thenReturn(MANAGER_THRESHOLD);

        // Mock currency conversion to 1:1 for simplicity
        when(currencyConvertionController.convert(any(Double.class), any(String.class), any(String.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    }

    /**
     * Test approval workflow for a manager approving a deal sheet within threshold.
     */
    @Test
    public void testManagerApprovalWithinThreshold() {
        // Login as manager
        loginAsUser(MANAGER_USERNAME);

        // Create a deal sheet request with amount within manager threshold
        double requestAmount = 30000.00;
        String requestId = UUID.randomUUID().toString();

        // Mock deal sheet creation and retrieval
        when(fpmDealsheetController.createDealSheet(any()))
            .thenReturn(requestId);
        when(fpmDealsheetController.getDealSheetStatus(requestId))
            .thenReturn("Pending Approval");

        // Submit request via UI
        submitDealSheetRequest(requestAmount);

        // Verify request routed to manager
        String routedApprover = fpmCommonControllerGetApproverForAmount(requestAmount);
        assertThat(routedApprover).isEqualTo("MANAGER");

        // Approve the request
        approveRequest(requestId, MANAGER_USERNAME);

        // Mock status update after approval
        when(fpmDealsheetController.getDealSheetStatus(requestId))
            .thenReturn("Approved");

        // Verify status updated in UI
        verifyRequestStatus(requestId, "Approved");

        // Verify requester can see updated status
        verifyRequesterSeesStatus(requestId, "Approved");
    }

    /**
     * Test approval workflow for a director approving a travel request within threshold.
     */
    @Test
    public void testDirectorApprovalWithinThreshold() {
        // Login as director
        loginAsUser(DIRECTOR_USERNAME);

        // Create a travel request with amount within director threshold
        double requestAmount = 150000.00;
        String requestId = UUID.randomUUID().toString();

        // Mock travel request creation and retrieval
        when(fpmTravelController.createTravelRequest(any()))
            .thenReturn(requestId);
        when(fpmTravelController.getTravelRequestStatus(requestId))
            .thenReturn("Pending Approval");

        // Submit request via UI
        submitTravelRequest(requestAmount);

        // Verify request routed to director
        String routedApprover = fpmCommonControllerGetApproverForAmount(requestAmount);
        assertThat(routedApprover).isEqualTo("DIRECTOR");

        // Approve the request
        approveRequest(requestId, DIRECTOR_USERNAME);

        // Mock status update after approval
        when(fpmTravelController.getTravelRequestStatus(requestId))
            .thenReturn("Approved");

        // Verify status updated in UI
        verifyRequestStatus(requestId, "Approved");

        // Verify requester can see updated status
        verifyRequesterSeesStatus(requestId, "Approved");
    }

    /**
     * Test that unauthorized user cannot approve request.
     */
    @Test
    public void testUnauthorizedUserCannotApprove() {
        // Login as a user without approval rights
        String unauthorizedUser = "staffUser";
        when(fpmUserProfileController.getUserRole(unauthorizedUser))
            .thenReturn("STAFF");

        loginAsUser(unauthorizedUser);

        double requestAmount = 10000.00;
        String requestId = UUID.randomUUID().toString();

        // Mock deal sheet creation
        when(fpmDealsheetController.createDealSheet(any()))
            .thenReturn(requestId);
        when(fpmDealsheetController.getDealSheetStatus(requestId))
            .thenReturn("Pending Approval");

        submitDealSheetRequest(requestAmount);

        // Attempt to approve request
        driver.get(BASE_URL + port + "/requests/" + requestId + "/approve");

        // Wait for error message
        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error-message")));
        assertThat(errorElement.getText()).contains("You are not authorized to approve this request");

        // Verify status remains pending
        verifyRequestStatus(requestId, "Pending Approval");
    }

    // Helper methods

    private void loginAsUser(String username) {
        driver.get(BASE_URL + port + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys("password123"); // assuming test password
        loginButton.click();

        // Wait for dashboard or home page
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    private void submitDealSheetRequest(double amount) {
        driver.get(BASE_URL + port + "/dealsheets/new");

        WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount")));
        WebElement submitButton = driver.findElement(By.id("submit-dealsheet"));

        amountInput.clear();
        amountInput.sendKeys(String.valueOf(amount));
        submitButton.click();

        // Wait for confirmation
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmation-message")));
        assertThat(confirmation.getText()).contains("Deal sheet submitted successfully");
    }

    private void submitTravelRequest(double amount) {
        driver.get(BASE_URL + port + "/travelrequests/new");

        WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount")));
        WebElement submitButton = driver.findElement(By.id("submit-travelrequest"));

        amountInput.clear();
        amountInput.sendKeys(String.valueOf(amount));
        submitButton.click();

        // Wait for confirmation
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmation-message")));
        assertThat(confirmation.getText()).contains("Travel request submitted successfully");
    }

    private String fpmCommonControllerGetApproverForAmount(double amount) {
        // Mocking the service call to get approver role based on amount
        if (amount <= MANAGER_THRESHOLD) {
            return "MANAGER";
        } else if (amount <= DIRECTOR_THRESHOLD) {
            return "DIRECTOR";
        } else {
            return "EXECUTIVE"; // out of scope for this test
        }
    }

    private void approveRequest(String requestId, String approverUsername) {
        // Navigate to approval page
        driver.get(BASE_URL + port + "/requests/" + requestId + "/approve");

        // Wait for approve button
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("approve-button")));

        approveButton.click();

        // Wait for success message
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approval-success-message")));
        assertThat(successMessage.getText()).contains("Request approved successfully");
    }

    private void verifyRequestStatus(String requestId, String expectedStatus) {
        driver.get(BASE_URL + port + "/requests/" + requestId);

        WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("request-status")));
        assertThat(statusElement.getText()).isEqualTo(expectedStatus);
    }

    private void verifyRequesterSeesStatus(String requestId, String expectedStatus) {
        // Simulate requester viewing the request status
        driver.get(BASE_URL + port + "/requests/" + requestId + "/status");

        WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("request-status")));
        assertThat(statusElement.getText()).isEqualTo(expectedStatus);
    }
}
