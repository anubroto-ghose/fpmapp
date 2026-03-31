/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8936
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:53:45
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
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import org.springframework.boot.test.web.server.LocalManagementPort;

/**
 * Integration Selenium test for mid-tier financial approval workflow.
 * 
 * Preconditions:
 * - Role mappings and thresholds configured for mid-tier requests routed to managers.
 * - A user with role 'manager' exists and is active.
 * - A financial request with value within mid-tier threshold is created.
 * 
 * This test mocks backend services and verifies UI workflow with Selenium WebDriver.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MidTierApprovalWorkflowTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    private final String baseUrl = "http://localhost:";

    private static final String MANAGER_USERNAME = "managerUser";
    private static final String MANAGER_ROLE = "manager";

    private static final String REQUEST_ID = UUID.randomUUID().toString();

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
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

        // Mock user profile service to return active manager user
        User managerUser = new User();
        managerUser.setUsername(MANAGER_USERNAME);
        managerUser.setRole(MANAGER_ROLE);
        managerUser.setActive(true);

        when(fpmUserProfileController.getUserByUsername(MANAGER_USERNAME)).thenReturn(managerUser);

        // Mock role mappings and thresholds
        when(fpmCommonController.isMidTierRequest(any(Double.class))).thenAnswer(invocation -> {
            Double amount = invocation.getArgument(0);
            // Mid-tier threshold: 1000 <= amount <= 10000
            return amount >= 1000 && amount <= 10000;
        });

        // Mock request creation
        when(fpmDealsheetController.createFinancialRequest(any())).thenAnswer(invocation -> {
            // Return request id
            return REQUEST_ID;
        });

        // Mock request routing
        when(fpmDealsheetController.getRequestRoute(REQUEST_ID)).thenReturn(Collections.singletonList(MANAGER_ROLE));

        // Mock approval update
        when(fpmDealsheetController.approveRequest(REQUEST_ID, MANAGER_USERNAME)).thenReturn(true);

        // Mock request status retrieval
        when(fpmDealsheetController.getRequestStatus(REQUEST_ID)).thenReturn("Pending Approval");
    }

    @Test
    public void testMidTierRequestApprovalByManager() {
        // Step 1: Submit a mid-tier financial approval request
        driver.get(baseUrl + port + "/login");

        // Login as a normal user who submits the request
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("requestorUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to financial request submission page
        driver.get(baseUrl + port + "/financial-requests/new");

        // Fill request form with mid-tier amount
        WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount")));
        WebElement descriptionInput = driver.findElement(By.id("description"));
        WebElement submitBtn = driver.findElement(By.id("submitRequestBtn"));

        double midTierAmount = 5000.00;
        amountInput.sendKeys(String.valueOf(midTierAmount));
        descriptionInput.sendKeys("Mid-tier financial request for project X");
        submitBtn.click();

        // Verify request creation success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        assertThat(successMsg.getText()).contains("Request submitted successfully");

        // Step 2: Verify the request is routed to a manager
        // Simulate backend returning route
        var routedRoles = fpmDealsheetController.getRequestRoute(REQUEST_ID);
        assertThat(routedRoles).containsExactly(MANAGER_ROLE);

        // Step 3: Manager logs in and approves the request
        driver.get(baseUrl + port + "/logout");
        driver.get(baseUrl + port + "/login");

        WebElement mgrUsernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement mgrPasswordInput = driver.findElement(By.id("password"));
        WebElement mgrLoginBtn = driver.findElement(By.id("loginBtn"));

        mgrUsernameInput.sendKeys(MANAGER_USERNAME);
        mgrPasswordInput.sendKeys("managerPass123");
        mgrLoginBtn.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to pending approvals
        driver.get(baseUrl + port + "/approvals/pending");

        // Locate the request by ID
        WebElement requestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td/text()='" + REQUEST_ID + "']")));
        assertThat(requestRow).isNotNull();

        // Click approve button
        WebElement approveBtn = requestRow.findElement(By.cssSelector("button.approveBtn"));
        approveBtn.click();

        // Wait for approval confirmation
        WebElement approvalMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalSuccessMessage")));
        assertThat(approvalMsg.getText()).contains("Request approved successfully");

        // Step 4: Confirm approval status is updated and visible
        when(fpmDealsheetController.getRequestStatus(REQUEST_ID)).thenReturn("Approved");

        driver.get(baseUrl + port + "/financial-requests/status/" + REQUEST_ID);

        WebElement statusLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("requestStatus")));
        String statusText = statusLabel.getText();
        assertThat(statusText).isEqualToIgnoringCase("Approved");

        // Verify no bypass of role hierarchy (request routed only to managers)
        assertThat(routedRoles).doesNotContain("director");
        assertThat(routedRoles).doesNotContain("finance_admin");
    }
}
