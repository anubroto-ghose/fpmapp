/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8927
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 15:02:40
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for concurrent approval status updates.
 * 
 * Preconditions:
 * - Multiple users have access to update the same approval request.
 * - User is logged into the FPMApplication UI with real-time updates enabled.
 * 
 * Test Steps:
 * 1. Simultaneously update the status of the same approval request from two different users.
 * 2. Observe the UI and notification system for the requester.
 * 3. Check for any data conflicts or inconsistent status displays.
 * 
 * Expected Results:
 * - The system resolves concurrent updates gracefully without data loss.
 * - The requester sees the final consistent status in real time.
 * - Notifications reflect the correct final status.
 * - No duplicate or conflicting notifications are sent.
 * - UI does not show flickering or inconsistent states.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ConcurrentApprovalStatusUpdateTest {

    private static WebDriver driverUser1;
    private static WebDriver driverUser2;
    private static WebDriver driverRequester;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String APPROVAL_REQUEST_ID = "AR-123456";

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @BeforeAll
    public static void setup() {
        // Setup ChromeDriver for headless testing
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driverUser1 = new ChromeDriver(options);
        driverUser2 = new ChromeDriver(options);
        driverRequester = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDown() {
        if (driverUser1 != null) {
            driverUser1.quit();
        }
        if (driverUser2 != null) {
            driverUser2.quit();
        }
        if (driverRequester != null) {
            driverRequester.quit();
        }
    }

    /**
     * Test concurrent approval status updates from two users and verify UI and notifications for requester.
     */
    @Test
    public void testConcurrentApprovalStatusUpdates() throws Exception {
        // Mock backend services responses
        mockBackendServices();

        // Login all users
        loginUser(driverUser1, "user1", "password1");
        loginUser(driverUser2, "user2", "password2");
        loginUser(driverRequester, "requester", "password3");

        // Navigate requester to approval request page
        navigateToApprovalRequest(driverRequester, APPROVAL_REQUEST_ID);

        // Wait for real-time updates to be enabled
        waitForRealTimeUpdates(driverRequester);

        // Navigate users to the same approval request update page
        navigateToApprovalRequest(driverUser1, APPROVAL_REQUEST_ID);
        navigateToApprovalRequest(driverUser2, APPROVAL_REQUEST_ID);

        // Prepare concurrent update tasks
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Exception> exceptionInThread = new AtomicReference<>();

        CompletableFuture<Void> user1Update = CompletableFuture.runAsync(() -> {
            try {
                latch.await();
                updateApprovalStatus(driverUser1, "Approved");
            } catch (Exception e) {
                exceptionInThread.set(e);
            }
        });

        CompletableFuture<Void> user2Update = CompletableFuture.runAsync(() -> {
            try {
                latch.await();
                updateApprovalStatus(driverUser2, "Rejected");
            } catch (Exception e) {
                exceptionInThread.set(e);
            }
        });

        // Release both threads simultaneously
        latch.countDown();

        // Wait for both updates to complete
        CompletableFuture.allOf(user1Update, user2Update).get(15, TimeUnit.SECONDS);

        if (exceptionInThread.get() != null) {
            throw exceptionInThread.get();
        }

        // Wait for UI and notifications to reflect final status
        String finalStatus = waitForFinalStatus(driverRequester);

        // Assert final status is consistent and one of the expected values
        assertThat(finalStatus).isIn("Approved", "Rejected");

        // Assert no flickering or inconsistent UI states
        assertNoFlickeringOrInconsistency(driverRequester);

        // Assert notifications are correct and not duplicated
        assertNotifications(driverRequester, finalStatus);
    }

    private void mockBackendServices() {
        // Mock currency conversion to always return 1.0 for simplicity
        when(currencyConvertionController.convertCurrency(any(), any(), any())).thenReturn(1.0);

        // Mock forecast controller to return dummy forecast data
        when(fpmForecastController.getForecast(any())).thenReturn("ForecastData");

        // Mock common controller to simulate approval request update
        doAnswer(invocation -> {
            String approvalRequestId = invocation.getArgument(0);
            String status = invocation.getArgument(1);
            // Simulate some processing delay
            Thread.sleep(100);
            // Return success
            return true;
        }).when(fpmCommonController).updateApprovalStatus(any(), any());

        // Mock user profile controller to return user info
        when(fpmUserProfileController.getUserProfile(any())).thenReturn(new com.webapp.fpmapp.entities.User("requester", "Requester User"));
    }

    private void loginUser(WebDriver driver, String username, String password) {
        driver.get(BASE_URL + "/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("loginButton")).click();

        // Wait for dashboard or home page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Verify login success
        assertThat(driver.getCurrentUrl()).contains("/dashboard");
    }

    private void navigateToApprovalRequest(WebDriver driver, String approvalRequestId) {
        driver.get(BASE_URL + "/approvals/" + approvalRequestId);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalRequestId")));

        WebElement idElement = driver.findElement(By.id("approvalRequestId"));
        assertThat(idElement.getText()).isEqualTo(approvalRequestId);
    }

    private void waitForRealTimeUpdates(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.attributeToBe(By.id("realTimeStatusIndicator"), "data-status", "enabled"));
    }

    private void updateApprovalStatus(WebDriver driver, String newStatus) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement statusDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("statusDropdown")));
        statusDropdown.click();

        WebElement statusOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[text()='" + newStatus + "']")));
        statusOption.click();

        WebElement updateButton = driver.findElement(By.id("updateStatusButton"));
        updateButton.click();

        // Wait for update confirmation
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("updateSuccessMessage")));
    }

    private String waitForFinalStatus(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        // Wait until the status text stabilizes (no flickering)
        String lastStatus = "";
        int stableCount = 0;
        while (stableCount < 3) { // check 3 times stable
            WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currentStatus")));
            String currentStatus = statusElement.getText();
            if (currentStatus.equals(lastStatus)) {
                stableCount++;
            } else {
                stableCount = 0;
                lastStatus = currentStatus;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return lastStatus;
    }

    private void assertNoFlickeringOrInconsistency(WebDriver driver) {
        // Check that the status element does not flicker or show inconsistent states
        WebElement statusElement = driver.findElement(By.id("currentStatus"));
        String initialStatus = statusElement.getText();

        try {
            Thread.sleep(2000); // wait 2 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String laterStatus = statusElement.getText();

        assertThat(laterStatus).isEqualTo(initialStatus);
    }

    private void assertNotifications(WebDriver driver, String expectedStatus) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement notificationArea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notificationArea")));

        // Check notifications text
        String notificationsText = notificationArea.getText();

        // Assert notification contains the final status
        assertThat(notificationsText).containsIgnoringCase(expectedStatus);

        // Assert no duplicate notifications for the same status
        long count = notificationsText.lines()
                .filter(line -> line.toLowerCase().contains(expectedStatus.toLowerCase()))
                .count();
        assertThat(count).isEqualTo(1);
    }
}
