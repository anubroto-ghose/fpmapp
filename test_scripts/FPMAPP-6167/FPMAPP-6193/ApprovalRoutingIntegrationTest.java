/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6193
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:00:38
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Integration test to validate role-based hierarchical approval routing of deal sheet approval requests.
 * It uses Spring Boot Test with embedded web server and Selenium WebDriver for end-to-end scenario.
 * 
 * Preconditions:
 * - Users and roles configured with thresholds
 * - Approval thresholds and delegations mocked
 * - Email and in-app notification services mocked
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApprovalRoutingIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private com.webapp.fpmapp.services.NotificationService notificationService;

    @MockBean
    private com.webapp.fpmapp.services.ApprovalRoutingService approvalRoutingService;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (make sure chromedriver binary is in PATH)
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
        setupMocks();
    }

    private void setupMocks() {
        // Mock the currency conversion responses
        when(currencyConvertionController.getExchangeRate(any())).thenReturn(1.0);

        // Mock approval routing to return correct approver based on role hierarchy
        when(approvalRoutingService.routeApproval(any())).thenReturn(
            new com.webapp.fpmapp.dto.ApprovalRoutingResponse("approverUserId123", "Manager", "manager@example.com")
        );

        // Mock notification service to simulate in-app and email notifications
        Mockito.doNothing().when(notificationService).sendInAppNotification(any(), any());
        Mockito.doNothing().when(notificationService).sendEmailNotification(any(), any(), any());
    }

    /**
     * Tests submitting a deal sheet approval request that should automatically be routed to correct approver
     * with notifications delivered.
     */
    @Test
    public void testApprovalRequestRoutingToCorrectApprover() {
        String baseUrl = "http://localhost:" + port;

        // Step 1: Submit an approval request with amount within submitter's immediate approval threshold
        driver.get(baseUrl + "/login");

        // Login as submitter user
        WebElement userInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        userInput.sendKeys("submitterUser");
        WebElement passInput = driver.findElement(By.id("password"));
        passInput.sendKeys("SubmitterPass123");
        driver.findElement(By.id("loginButton")).click();

        // Verify login success
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertThat(driver.getCurrentUrl()).contains("/dashboard");

        // Navigate to dealsheet creation page
        driver.get(baseUrl + "/dealsheet/create");

        // Fill deal sheet approval form with financial amount within threshold
        WebElement amountField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dealAmount")));
        double dealAmount = 5000.00; // assume submitter approval threshold is 10,000
        amountField.sendKeys(String.valueOf(dealAmount));

        WebElement descriptionField = driver.findElement(By.id("dealDescription"));
        descriptionField.sendKeys("Test deal sheet approval routing");

        // Submit deal sheet for approval
        driver.findElement(By.id("submitApprovalButton")).click();

        // Step 2: Verify the approval request is routed to correct approver as per the role hierarchy
        // Mocked approvalRoutingService returns approverUserId123 as approver
        // Simulated UI should show submission success
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submissionConfirmation")));
        assertThat(confirmation.getText()).contains("Request submitted successfully");

        // Navigate to approver login page to verify the approval task
        driver.get(baseUrl + "/logout");

        driver.get(baseUrl + "/login");
        WebElement approverUserInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        approverUserInput.sendKeys("approverUserId123");
        WebElement approverPassInput = driver.findElement(By.id("password"));
        approverPassInput.sendKeys("ApproverPass123");
        driver.findElement(By.id("loginButton")).click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 3: Approver's pending task list should reflect new approval
        driver.get(baseUrl + "/approvals/pending");

        List<WebElement> pendingItems = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".approval-task-item")));
        boolean foundApproval = pendingItems.stream().anyMatch(el -> el.getText().contains("Test deal sheet approval routing")
                && el.getText().contains(String.format("$%.2f", dealAmount)));

        assertThat(foundApproval).isTrue();

        // Step 4: Verify notifications
        // Since notification service is mocked, verify that notification methods were called

        Mockito.verify(notificationService).sendInAppNotification(org.mockito.ArgumentMatchers.eq("approverUserId123"), org.mockito.ArgumentMatchers.contains("new approval request"));
        Mockito.verify(notificationService).sendEmailNotification(org.mockito.ArgumentMatchers.eq("manager@example.com"), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }
}
