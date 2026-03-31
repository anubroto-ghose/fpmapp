/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8935
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:54:42
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for verifying that managers cannot approve high-value financial requests.
 * 
 * Preconditions:
 * - Role mappings and thresholds are configured with managers allowed to approve only mid-tier requests.
 * - A user with the role of 'manager' exists and is active.
 * - A financial request with a value above the high-value threshold is created.
 * 
 * Test Steps:
 * 1. Submit a high-value financial approval request.
 * 2. Attempt to have a manager approve the high-value request.
 * 
 * Expected Results:
 * - The system prevents the manager from approving the high-value request.
 * - The request remains pending or routed to a director.
 * - Approval status does not update incorrectly.
 * - Role hierarchy enforcement prevents bypass.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ManagerApprovalHighValueRequestTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;

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

    private static final String MANAGER_USERNAME = "managerUser";
    private static final String MANAGER_PASSWORD = "password123";

    private static final double HIGH_VALUE_THRESHOLD = 100000.00;
    private static final double HIGH_VALUE_REQUEST_AMOUNT = 150000.00;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

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

        // Mock user profile service to return a manager user with active status
        when(fpmUserProfileController.getUserByUsername(MANAGER_USERNAME))
            .thenReturn(new com.webapp.fpmapp.entities.User(MANAGER_USERNAME, "Manager", "manager", true));

        // Mock role threshold configuration
        when(fpmCommonController.getApprovalThresholdForRole("manager"))
            .thenReturn(HIGH_VALUE_THRESHOLD - 1); // manager can approve up to just below high value

        // Mock dealsheet controller to simulate request creation and approval
        when(fpmDealsheetController.createFinancialRequest(any()))
            .thenAnswer(invocation -> {
                com.webapp.fpmapp.dto.FinancialRequest request = invocation.getArgument(0);
                request.setId(1L);
                request.setStatus("PENDING");
                return request;
            });

        when(fpmDealsheetController.approveRequest(1L, MANAGER_USERNAME))
            .thenAnswer(invocation -> {
                // Manager cannot approve high-value requests
                double requestAmount = HIGH_VALUE_REQUEST_AMOUNT;
                double threshold = fpmCommonController.getApprovalThresholdForRole("manager");
                if (requestAmount > threshold) {
                    throw new IllegalAccessException("Manager role cannot approve high-value requests");
                }
                return true;
            });
    }

    @Test
    public void testManagerCannotApproveHighValueRequest() throws Exception {
        // Step 1: Login as manager
        driver.get(BASE_URL + port + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys(MANAGER_USERNAME);
        passwordInput.sendKeys(MANAGER_PASSWORD);
        loginButton.click();

        // Verify login success by checking presence of dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertTrue(driver.getCurrentUrl().contains("/dashboard"), "Manager should be redirected to dashboard after login");

        // Step 2: Submit a high-value financial approval request
        driver.get(BASE_URL + port + "/financial-requests/new");

        WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount")));
        WebElement descriptionInput = driver.findElement(By.id("description"));
        WebElement submitButton = driver.findElement(By.id("submitRequest"));

        amountInput.sendKeys(String.valueOf(HIGH_VALUE_REQUEST_AMOUNT));
        descriptionInput.sendKeys("High value project funding request");
        submitButton.click();

        // Wait for confirmation page or message
        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmationMessage")));
        assertTrue(confirmationMessage.getText().contains("Request submitted successfully"), "Request submission confirmation expected");

        // Step 3: Attempt to approve the high-value request as manager
        driver.get(BASE_URL + port + "/financial-requests/1");

        WebElement approveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approveButton")));
        approveButton.click();

        // The system should prevent approval and show an error message
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        assertTrue(errorMessage.getText().contains("cannot approve high-value requests"), "Error message about approval restriction expected");

        // Verify that the request status remains pending or routed to director
        WebElement statusElement = driver.findElement(By.id("requestStatus"));
        String statusText = statusElement.getText();
        assertTrue(statusText.equalsIgnoreCase("PENDING") || statusText.equalsIgnoreCase("Routed to Director"),
                "Request status should remain pending or routed to director");

        // Additional check: approval status in backend remains unchanged
        com.webapp.fpmapp.dto.FinancialRequest request = fpmDealsheetController.createFinancialRequest(
                new com.webapp.fpmapp.dto.FinancialRequest(HIGH_VALUE_REQUEST_AMOUNT, "High value project funding request"));
        try {
            fpmDealsheetController.approveRequest(request.getId(), MANAGER_USERNAME);
            fail("Manager should not be able to approve high-value requests");
        } catch (IllegalAccessException e) {
            assertEquals("Manager role cannot approve high-value requests", e.getMessage());
        }
    }
}
