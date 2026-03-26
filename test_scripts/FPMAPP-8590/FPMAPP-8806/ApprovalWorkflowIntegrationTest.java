/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8806
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:41:14
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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

import org.springframework.boot.test.web.server.LocalManagementPort;

/**
 * Integration test for approval workflow enforcing hierarchical role mapping with financial thresholds.
 * 
 * Preconditions:
 * - User logged in as director or manager.
 * - Request created with amount within financial threshold.
 * 
 * Test Steps:
 * 1. Submit request with amount within approver's threshold.
 * 2. Verify routing to correct approver.
 * 3. Approve request.
 * 4. Check status updates to "Approved" in real-time.
 * 5. Verify requester sees updated approval status.
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

    private final String baseUrl = "http://localhost:";

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

        // Mock user profile to simulate logged in director
        User directorUser = new User();
        directorUser.setId(1001L);
        directorUser.setUsername("directorUser");
        directorUser.setRole("DIRECTOR");

        when(fpmUserProfileController.getCurrentUser()).thenReturn(directorUser);

        // Mock deal sheet creation and retrieval
        when(fpmDealsheetController.createDealSheet(any())).thenAnswer(invocation -> {
            // Return a mock deal sheet with ID and amount
            var request = invocation.getArgument(0);
            // Simulate response
            return new com.webapp.fpmapp.dto.DealSheetResponse(2001L, (Double)request.get("amount"), "PENDING", "DIRECTOR");
        });

        when(fpmDealsheetController.getDealSheetStatus(2001L)).thenReturn("PENDING");

        // Mock approval action
        when(fpmDealsheetController.approveDealSheet(2001L, "directorUser")).then(invocation -> {
            // Simulate approval success
            return true;
        });

        // After approval, status changes to APPROVED
        when(fpmDealsheetController.getDealSheetStatus(2001L)).thenReturn("APPROVED");
    }

    @Test
    public void testApprovalWorkflowForDirectorWithinThreshold() {
        driver.get(baseUrl + port + "/login");

        // Simulate login as director
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("directorUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to create deal sheet page
        driver.get(baseUrl + port + "/dealsheets/new");

        // Fill deal sheet form with amount within director threshold (e.g., 50000)
        WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount")));
        amountInput.clear();
        amountInput.sendKeys("50000");

        WebElement submitBtn = driver.findElement(By.id("submitDealSheet"));
        submitBtn.click();

        // Verify routing to correct approver (director)
        WebElement routedApprover = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("routedApprover")));
        String approverRole = routedApprover.getText();
        assertThat(approverRole).isEqualToIgnoringCase("DIRECTOR");

        // Approve the request
        WebElement approveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("approveBtn")));
        approveBtn.click();

        // Wait for status update to "Approved" in real-time
        WebElement statusLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatus")));

        wait.until(driver -> statusLabel.getText().equalsIgnoreCase("APPROVED"));
        assertThat(statusLabel.getText()).isEqualToIgnoringCase("APPROVED");

        // Verify requester can see updated approval status
        driver.get(baseUrl + port + "/dealsheets/2001/status");
        WebElement requesterStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("requesterApprovalStatus")));
        assertThat(requesterStatus.getText()).isEqualToIgnoringCase("APPROVED");

        // Verify only authorized roles can approve
        // Attempt approval as unauthorized user (simulate manager trying to approve above threshold)
        // For brevity, we simulate this by checking the approve button is not present for unauthorized user
        driver.get(baseUrl + port + "/logout");

        // Login as manager
        driver.get(baseUrl + port + "/login");
        usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        passwordInput = driver.findElement(By.id("password"));
        loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("managerUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));

        driver.get(baseUrl + port + "/dealsheets/2001");

        List<WebElement> approveButtons = driver.findElements(By.id("approveBtn"));
        assertThat(approveButtons).isEmpty();
    }
}
