/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8622
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:59:01
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
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
import org.openqa.selenium.NoSuchElementException;
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

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

/**
 * Integration Selenium test for currency rate override UI immediate update.
 * 
 * Preconditions:
 * - Admin user logged in
 * - WebSocket connection active
 * - Currency exchange rate UI elements visible
 * 
 * Validates:
 * - Immediate UI update on override
 * - Notification alert shown
 * - Client-side validation
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyRateOverrideUITest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static Session wsSession;
    private static CountDownLatch messageLatch;
    private static String lastNotificationMessage;

    private final String adminUsername = "admin";
    private final String adminPassword = "adminPass123";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
        if (wsSession != null && wsSession.isOpen()) {
            try {
                wsSession.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock currency conversion controller to accept override and return updated rate
        when(currencyConvertionController.overrideCurrencyRate("USD", "EUR", 0.85))
            .thenReturn(true);
        when(currencyConvertionController.getExchangeRate("USD", "EUR"))
            .thenReturn(0.85);

        // Login as admin
        driver.get("http://localhost:" + port + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.clear();
        usernameInput.sendKeys(adminUsername);
        passwordInput.clear();
        passwordInput.sendKeys(adminPassword);
        loginButton.click();

        // Wait for redirect to dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Open WebSocket connection
        openWebSocketConnection();

        // Navigate to currency override page
        driver.get("http://localhost:" + port + "/admin/currency-override");

        // Wait for currency rate UI elements
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyRateDisplay")));
    }

    private void openWebSocketConnection() throws Exception {
        messageLatch = new CountDownLatch(1);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = new URI("ws://localhost:" + port + "/fpm/ui/notifications/ws");
        wsSession = container.connectToServer(new ClientEndpoint() {
            @OnMessage
            public void onMessage(String message) {
                lastNotificationMessage = message;
                messageLatch.countDown();
            }
        }, uri);
    }

    @Test
    public void testCurrencyRateOverrideImmediateUIUpdate() throws Exception {
        // Locate input fields and override button
        WebElement fromCurrencyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fromCurrency")));
        WebElement toCurrencyInput = driver.findElement(By.id("toCurrency"));
        WebElement rateInput = driver.findElement(By.id("overrideRate"));
        WebElement overrideButton = driver.findElement(By.id("overrideBtn"));
        WebElement rateDisplay = driver.findElement(By.id("currencyRateDisplay"));

        // Validate client-side validation: input invalid rate (negative number)
        rateInput.clear();
        rateInput.sendKeys("-0.5");
        overrideButton.click();

        // Expect validation error message
        WebElement validationError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rateValidationError")));
        assertThat(validationError.getText()).contains("Invalid rate");

        // Now input valid override data
        fromCurrencyInput.clear();
        fromCurrencyInput.sendKeys("USD");
        toCurrencyInput.clear();
        toCurrencyInput.sendKeys("EUR");
        rateInput.clear();
        rateInput.sendKeys("0.85");

        // Click override
        overrideButton.click();

        // Wait for notification via WebSocket
        boolean messageReceived = messageLatch.await(5, TimeUnit.SECONDS);
        assertThat(messageReceived).isTrue();
        assertThat(lastNotificationMessage).contains("Currency rate overridden");

        // Wait for UI to update the displayed rate
        wait.until(ExpectedConditions.textToBePresentInElement(rateDisplay, "0.85"));

        // Assert displayed rate matches override
        String displayedRate = rateDisplay.getText();
        assertThat(displayedRate).isEqualTo("0.85");

        // Assert alert/notification is visible on UI
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overrideSuccessAlert")));
        assertThat(alert.getText()).contains("Currency rate overridden successfully");
    }

    @Test
    public void testCurrencyRateOverrideInvalidInputPreventsSubmission() {
        WebElement rateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overrideRate")));
        WebElement overrideButton = driver.findElement(By.id("overrideBtn"));

        // Input invalid non-numeric value
        rateInput.clear();
        rateInput.sendKeys("abc");
        overrideButton.click();

        WebElement validationError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rateValidationError")));
        assertThat(validationError.getText()).contains("Invalid rate");

        // Input empty value
        rateInput.clear();
        overrideButton.click();

        validationError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rateValidationError")));
        assertThat(validationError.getText()).contains("Rate is required");
    }

    @Test
    public void testCurrencyRateDisplayUpdatesWithoutPageRefresh() throws InterruptedException {
        WebElement rateDisplay = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyRateDisplay")));

        // Simulate server push of new rate via JS (mock WebSocket message)
        ((JavascriptExecutor) driver).executeScript(
            "window.dispatchEvent(new CustomEvent('currencyRateUpdate', { detail: { from: 'USD', to: 'EUR', rate: 0.90 } }));");

        // Wait for UI update
        wait.until(ExpectedConditions.textToBePresentInElement(rateDisplay, "0.90"));

        String updatedRate = rateDisplay.getText();
        assertThat(updatedRate).isEqualTo("0.90");
    }

    // Helper method to check element presence
    private boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
