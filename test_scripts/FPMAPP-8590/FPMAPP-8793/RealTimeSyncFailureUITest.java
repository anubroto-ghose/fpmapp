/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8793
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:06:32
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration Selenium test for UI error handling and notification on real-time sync failure.
 * 
 * Preconditions:
 * - User is logged in.
 * - WebSocket connection is disrupted.
 * 
 * Test Steps:
 * 1. Simulate WebSocket failure.
 * 2. Perform approval or currency override action.
 * 3. Verify UI behavior and notifications.
 * 
 * Expected:
 * - UI handles failure gracefully.
 * - User notified about sync failure.
 * - No full page reload.
 * - Retry or manual refresh options available.
 * - Audit trail and delegation components stable.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RealTimeSyncFailureUITest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

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
        MockitoAnnotations.openMocks(this);

        // Mock currency conversion to return fixed value
        when(currencyConvertionController.convertCurrency(any(), any(), any())).thenReturn(100.0);

        // Mock forecast controller to return dummy forecast
        when(fpmForecastController.getForecast(any())).thenReturn("Forecast Data");

        // Mock common controller to simulate normal behavior
        when(fpmCommonController.getAuditTrail(any())).thenReturn("Audit Trail Data");

        // Mock dealsheet controller approval action to succeed
        when(fpmDealsheetController.approveDeal(any(), any())).thenReturn(true);

        // Mock travel controller currency override to succeed
        when(fpmTravelController.overrideCurrency(any(), any())).thenReturn(true);

        // Mock user profile controller to return logged in user
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);
    }

    /**
     * Helper method to simulate WebSocket failure by injecting JS that closes the socket or disables it.
     */
    private void simulateWebSocketFailure() {
        // Inject JavaScript to override WebSocket and simulate failure
        String script = "window.WebSocket = function() {" +
                "this.readyState = 3;" +
                "this.send = function() { throw new Error('WebSocket is closed'); };" +
                "this.close = function() {};" +
                "this.addEventListener = function() {};" +
                "this.dispatchEvent = function() {};" +
                "this.onopen = null;" +
                "this.onclose = null;" +
                "this.onerror = null;" +
                "this.onmessage = null;" +
                "};";
        ((JavascriptExecutor) driver).executeScript(script);
    }

    /**
     * Helper method to login user via UI.
     */
    private void loginUser() {
        driver.get("http://localhost:8080/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard or home page
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    /**
     * Test case for UI error handling and notification on real-time sync failure.
     */
    @Test
    public void testUIErrorHandlingOnRealTimeSyncFailure() {
        // Step 1: Login user
        loginUser();

        // Step 2: Simulate WebSocket failure
        simulateWebSocketFailure();

        // Step 3: Navigate to approval page
        driver.get("http://localhost:8080/fpm/approval");

        // Wait for approval button
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("approveDealButton")));

        // Step 4: Perform approval action
        approveButton.click();

        // Step 5: Verify UI does not crash and shows notification
        // Wait for notification element
        WebElement notification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification")));

        String notificationText = notification.getText();
        assertThat(notificationText).containsIgnoringCase("sync failure").withFailMessage("Expected sync failure notification");

        // Step 6: Verify no full page reload occurred
        // We can check that URL remains the same
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("/fpm/approval").withFailMessage("Page should not reload on sync failure");

        // Step 7: Verify retry or manual refresh options are present
        WebElement retryButton = null;
        try {
            retryButton = driver.findElement(By.id("retrySyncButton"));
        } catch (NoSuchElementException e) {
            // fail test if retry button not found
            assertThat(false).withFailMessage("Retry button should be present on sync failure").isTrue();
        }
        assertThat(retryButton.isDisplayed()).isTrue();

        // Step 8: Verify audit trail and delegation components remain stable
        WebElement auditTrail = driver.findElement(By.id("auditTrailSection"));
        WebElement delegationMgmt = driver.findElement(By.id("delegationManagementSection"));

        assertThat(auditTrail.isDisplayed()).isTrue();
        assertThat(delegationMgmt.isDisplayed()).isTrue();

        // Step 9: Click retry and verify retry attempt
        retryButton.click();

        // Wait for notification to update or disappear
        wait.until(ExpectedConditions.or(
                ExpectedConditions.invisibilityOf(notification),
                ExpectedConditions.textToBePresentInElement(notification, "retrying")
        ));

        // Test complete
    }
}