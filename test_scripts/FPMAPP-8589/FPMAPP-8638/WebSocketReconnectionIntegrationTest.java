/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8638
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:47:27
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration test for WebSocket connection interruption and graceful reconnection with state resynchronization.
 * 
 * Preconditions:
 * - User is logged in
 * - WebSocket connections to /fpmcamunda/approvals/updates and /fpm/currency/rates/updates are established
 * 
 * Test Steps:
 * 1. Simulate network interruption causing WebSocket disconnection
 * 2. Verify UI detects disconnection and attempts reconnect
 * 3. Verify UI resynchronizes approval statuses and currency rates after reconnect
 * 4. Confirm no stale/inconsistent data during/after reconnect
 * 5. Confirm notifications continue without delay after reconnect
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class WebSocketReconnectionIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private final String baseUrl = "http://localhost:";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
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
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Mock backend responses for currency rates and approvals
        when(currencyConvertionController.getCurrentRates()).thenReturn(
                // Simulated currency rates JSON or DTO
                "{\"USD\":1.0,\"EUR\":0.85,\"GBP\":0.75}"
        );

        when(fpmCommonController.getApprovalStatuses()).thenReturn(
                // Simulated approval statuses JSON or DTO
                "[{\"id\":101,\"status\":\"APPROVED\"},{\"id\":102,\"status\":\"PENDING\"}]"
        );
    }

    @Test
    public void testWebSocketReconnectionAndStateResynchronization() throws InterruptedException {
        driver.get(baseUrl + port + "/login");

        // Simulate user login
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("Test@1234");
        loginButton.click();

        // Wait for dashboard/homepage to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Verify WebSocket connections are established by checking UI elements or JS variables
        Boolean wsApprovalsConnected = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return window.fpmWebSocketApprovals && window.fpmWebSocketApprovals.readyState === 1;"
        );
        Boolean wsCurrencyConnected = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return window.fpmWebSocketCurrency && window.fpmWebSocketCurrency.readyState === 1;"
        );

        assertThat(wsApprovalsConnected).isTrue();
        assertThat(wsCurrencyConnected).isTrue();

        // Step 1: Simulate network interruption causing WebSocket disconnection
        // We simulate by forcibly closing the WebSocket connections via JS
        ((JavascriptExecutor) driver).executeScript(
                "if(window.fpmWebSocketApprovals) { window.fpmWebSocketApprovals.close(); }"
        );
        ((JavascriptExecutor) driver).executeScript(
                "if(window.fpmWebSocketCurrency) { window.fpmWebSocketCurrency.close(); }"
        );

        // Step 2: Verify UI detects disconnection and attempts reconnect
        // Wait and poll for reconnect attempts
        boolean reconnectAttempted = wait.until(driver -> {
            Boolean reconnectingApprovals = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "return window.fpmWebSocketApprovals && (window.fpmWebSocketApprovals.readyState === 0 || window.fpmWebSocketApprovals.readyState === 1);"
            );
            Boolean reconnectingCurrency = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "return window.fpmWebSocketCurrency && (window.fpmWebSocketCurrency.readyState === 0 || window.fpmWebSocketCurrency.readyState === 1);"
            );
            return reconnectingApprovals && reconnectingCurrency;
        });

        assertThat(reconnectAttempted).isTrue();

        // Step 3: Upon reconnection, verify UI resynchronizes approval statuses and currency rates
        // We wait for UI elements that display approval statuses and currency rates to update

        // Wait for approval status elements to be updated
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("approval-status-101"), "APPROVED"));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("approval-status-102"), "PENDING"));

        // Wait for currency rates elements to be updated
        WebElement usdRate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currency-rate-USD")));
        WebElement eurRate = driver.findElement(By.id("currency-rate-EUR"));
        WebElement gbpRate = driver.findElement(By.id("currency-rate-GBP"));

        assertThat(usdRate.getText()).isEqualTo("1.0");
        assertThat(eurRate.getText()).isEqualTo("0.85");
        assertThat(gbpRate.getText()).isEqualTo("0.75");

        // Step 4: Confirm no stale or inconsistent data during or after reconnection
        // Check that no error banners or stale data warnings are visible
        boolean errorBannerPresent = driver.findElements(By.id("ws-error-banner")).size() > 0;
        assertThat(errorBannerPresent).isFalse();

        // Step 5: Observe notifications for approvals, delegations, and overrides continue without delay
        // Simulate backend sending a new approval notification via JS event
        ((JavascriptExecutor) driver).executeScript(
                "window.dispatchEvent(new CustomEvent('approvalNotification', { detail: { id: 103, status: 'APPROVED' } }));"
        );

        // Wait for notification to appear
        WebElement notification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-103")));
        assertThat(notification.getText()).contains("Approval ID 103 approved");

        // Similarly, simulate delegation notification
        ((JavascriptExecutor) driver).executeScript(
                "window.dispatchEvent(new CustomEvent('delegationNotification', { detail: { id: 201, delegateTo: 'user2' } }));"
        );
        WebElement delegationNotification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-201")));
        assertThat(delegationNotification.getText()).contains("Delegated to user2");

        // Simulate override notification
        ((JavascriptExecutor) driver).executeScript(
                "window.dispatchEvent(new CustomEvent('overrideNotification', { detail: { id: 301, overriddenBy: 'admin' } }));"
        );
        WebElement overrideNotification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-301")));
        assertThat(overrideNotification.getText()).contains("Overridden by admin");
    }
}
