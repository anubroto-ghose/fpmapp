/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6195
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:59:18
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.FpmCommonController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Integration Selenium Test for multi-level role-based approval workflow with automatic escalation.
 * 
 * Preconditions:
 * - Multi-level approval hierarchy exists
 * - Approval request requires multiple approval levels
 * 
 * Test steps automate UI interactions simulating approval process and verifying real-time status updates.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MultiLevelApprovalWorkflowIT {

    private static final Logger logger = LoggerFactory.getLogger(MultiLevelApprovalWorkflowIT.class);

    private static WebDriver driver;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @Autowired
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeAll
    public static void setupClass() {
        // Assuming chromedriver executable is available on PATH
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testMultiLevelApprovalWorkflowWithAutoEscalation() {
        baseUrl = "http://localhost:" + port;

        // Mock service responses to simulate real backend approval logic
        when(fpmForecastController.getApprovalThresholdForRole("Level1Approver")).thenReturn(10000.0);
        when(fpmForecastController.getApprovalThresholdForRole("Level2Approver")).thenReturn(50000.0);

        // Step 1: Submit request requiring multi-level approval
        logger.info("Submitting new deal sheet approval request above Level1 threshold...");
        driver.get(baseUrl + "/login");

        loginAsUser("submitterUser", "password123");

        driver.get(baseUrl + "/dealsheets/new");

        fillDealSheetForm(60000.00, "Business travel and deal closure funding");

        WebElement submitButton = driver.findElement(By.id("submitDealSheetBtn"));
        submitButton.click();

        // Assert success message appears
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submissionSuccessMsg")));
        assertThat(successMsg.getText()).contains("submitted successfully");

        // Step 2: Verify Level1 approver receives the task first
        logger.info("Logging in as Level1 approver to verify task assignment...");
        logout();
        loginAsUser("level1ApproverUser", "password123");

        driver.get(baseUrl + "/approvals/tasks");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("taskTable")));
        List<WebElement> tasksLevel1 = driver.findElements(By.cssSelector("#taskTable tbody tr"));
        assertThat(tasksLevel1).isNotEmpty();

        boolean foundRelevantTask = tasksLevel1.stream().anyMatch(tr -> tr.getText().contains("60000.0") && tr.getText().contains("Submitted"));
        assertThat(foundRelevantTask).isTrue();

        // Step 3: Simulate no action within escalation timeout period
        // For testing, we simulate escalation triggering via API call/mock rather than actual wait
        logger.info("Simulating no action and triggering escalation to Level2 approver...");
        simulateEscalationForApproval("deal123");

        // Step 4: Login as Level2 approver and verify escalation task
        logout();
        loginAsUser("level2ApproverUser", "password123");
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("taskTable")));

        List<WebElement> tasksLevel2 = driver.findElements(By.cssSelector("#taskTable tbody tr"));
        assertThat(tasksLevel2).isNotEmpty();

        foundRelevantTask = tasksLevel2.stream().anyMatch(tr -> tr.getText().contains("60000.0") && tr.getText().contains("Escalated"));
        assertThat(foundRelevantTask).isTrue();

        // Step 5: Approve request at top level
        WebElement approvalButton = driver.findElement(By.cssSelector("button.approve-btn"));
        approvalButton.click();

        WebElement approvalSuccessMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalSuccessMsg")));
        assertThat(approvalSuccessMsg.getText()).contains("approved successfully");

        // Step 6: Verify approval status update and notification
        driver.get(baseUrl + "/dealsheets/status/deal123");
        WebElement statusEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dealStatus")));
        assertThat(statusEl.getText()).isEqualToIgnoringCase("Approved");

        WebElement notificationBanner = driver.findElement(By.id("realTimeNotification"));
        assertThat(notificationBanner.getText()).contains("has been approved");

        logger.info("Multi-level approval workflow with escalation validated successfully.");
    }

    private void loginAsUser(String username, String password) {
        WebElement usernameInput = driver.findElement(By.id("usernameInput"));
        WebElement passwordInput = driver.findElement(By.id("passwordInput"));
        WebElement loginBtn = driver.findElement(By.id("loginButton"));

        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        loginBtn.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("dashboard"));
    }

    private void logout() {
        WebElement logoutBtn = driver.findElement(By.id("logoutButton"));
        logoutBtn.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("login"));
    }

    private void fillDealSheetForm(double amount, String description) {
        WebElement amountInput = driver.findElement(By.id("amountInput"));
        WebElement descInput = driver.findElement(By.id("descriptionInput"));

        amountInput.clear();
        amountInput.sendKeys(String.valueOf(amount));
        descInput.clear();
        descInput.sendKeys(description);
    }

    /**
     * Simulates backend triggering escalation logic - in prod would be a timed job.
     * For test, trigger via REST or direct mock interaction.
     * @param approvalId the approval request ID
     */
    private void simulateEscalationForApproval(String approvalId) {
        // This mock triggers escalation event in system.
        doAnswer(invocation -> {
            logger.info("Escalation logic triggered for approval id: {}", approvalId);
            return null;
        }).when(fpmCommonController).escalateApproval(approvalId);

        fpmCommonController.escalateApproval(approvalId);
    }
}