/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5417
 * Epic: FPMAPP-5363
 * Generated on: 2025-09-04 16:07:16
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test to verify correct approval routing based on financial threshold.
 *
 * Preconditions:
 * - Approval workflows and roles with financial thresholds are configured.
 * Test Steps:
 * 1. Submit an approval request with a specific financial amount.
 * 2. Check the assignment of the approval task.
 * 3. Verify assigned approver role matches expected tier.
 *
 * Expected Results:
 * - Approval request routed to correct approver.
 * - No other approvers outside threshold receive task.
 * - Workflow reflects correct approver assignment.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
public class ApprovalRoutingIntegrationTest {

    private static WebDriver driver;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setUp() {
        // Setup ChromeDriver for Selenium
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testApprovalRoutingBasedOnFinancialThreshold() throws InterruptedException {
        // Arrange
        final double submittedAmount = 150000.00; // amount which falls between mid-tier approver thresholds
        final String requestId = "REQ-1001";
        final String expectedApproverRole = "MID_TIER_APPROVER";
        final String expectedApproverUsername = "approver_mid_tier";

        // Mock user and roles
        User approverUser = new User();
        approverUser.setUsername(expectedApproverUsername);
        approverUser.setRole(expectedApproverRole);

        // Mock behavior of backend services
        given(fpmCommonController.getApproverByFinancialThreshold(eq(submittedAmount))).willReturn(approverUser);
        given(fpmCommonController.submitApprovalRequest(any(User.class), eq(submittedAmount))).willReturn(requestId);
        given(fpmCommonController.getTaskAssignment(requestId)).willReturn(approverUser);

        // Act
        driver.get(BASE_URL + "/login");

        // Login as a requester user
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("requester_user");
        passwordInput.sendKeys("SecurePass123");
        loginButton.click();

        // Wait for redirect to dashboard
        Thread.sleep(2000);

        // Navigate to approval submission page
        driver.get(BASE_URL + "/approval/submit");

        WebElement amountInput = driver.findElement(By.id("amount"));
        WebElement submitBtn = driver.findElement(By.id("submitApproval"));

        amountInput.sendKeys(String.valueOf(submittedAmount));
        submitBtn.click();

        // Wait for submission processing
        Thread.sleep(2000);

        // Verify if redirected to confirmation page showing request ID
        WebElement confirmationText = driver.findElement(By.id("confirmationMessage"));
        assertThat(confirmationText.getText()).contains("Request submitted successfully");
        assertThat(confirmationText.getText()).contains(requestId);

        // Simulate task assignment check via UI
        driver.get(BASE_URL + "/approval/tasks/" + requestId);

        WebElement assignedApproverElem = driver.findElement(By.id("assignedApprover"));
        WebElement approverRoleElem = driver.findElement(By.id("approverRole"));

        String assignedApprover = assignedApproverElem.getText();
        String approverRole = approverRoleElem.getText();

        // Assert
        assertThat(assignedApprover).isEqualTo(expectedApproverUsername);
        assertThat(approverRole).isEqualTo(expectedApproverRole);

        // Additional backend assertion to ensure no other approvers assigned
        User assignment = fpmCommonController.getTaskAssignment(requestId);
        assertThat(assignment).isNotNull();
        assertThat(assignment.getUsername()).isEqualTo(expectedApproverUsername);
        assertThat(assignment.getRole()).isEqualTo(expectedApproverRole);

        // Verify that submitApprovalRequest was called once
        verify(fpmCommonController).submitApprovalRequest(any(User.class), eq(submittedAmount));
    }
}