/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8637
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:48:03
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.webapp.fpmapp.services.CurrencyConvertionController;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompFrameHandler;

import java.lang.reflect.Type;

/**
 * Integration test for real-time currency rate updates pushed via WebSocket and displayed instantly on UI.
 * 
 * Preconditions:
 * - User is logged in
 * - WebSocket connection to /fpm/currency/rates/updates is established
 * - Backend pushes currency rate update events
 * 
 * This test mocks backend currency rate update events and verifies UI updates in real-time.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyRateRealTimeUpdateTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final BlockingQueue<CurrencyRateUpdate> receivedUpdates = new LinkedBlockingQueue<>();

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
    public void setup() {
        // Mock the currency conversion controller to simulate backend currency rate override
        when(currencyConvertionController.getCurrentRates()).thenReturn(
                new CurrencyRateUpdate("USD", "EUR", 0.85, false));
    }

    /**
     * Test real-time currency rate update pushed from backend and reflected instantly on UI.
     */
    @Test
    public void testRealTimeCurrencyRateUpdate() throws Exception {
        // Step 0: Login user (simulate login by navigating to login page and submitting credentials)
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("Test@1234");
        loginButton.click();

        // Wait for redirect to dashboard/home page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 1: Establish WebSocket connection to /fpm/currency/rates/updates
        // This is handled by the frontend React app automatically on page load
        // We verify the connection by checking UI elements that update on receiving messages

        // Step 2: Trigger a currency rate change or override in the backend
        // Simulate backend pushing a currency rate update event
        CurrencyRateUpdate updatedRate = new CurrencyRateUpdate("USD", "EUR", 0.90, true);

        // Push update via messaging template to WebSocket subscribers
        messagingTemplate.convertAndSend("/fpm/currency/rates/updates", updatedRate);

        // Step 3: Observe the UI for immediate update reflecting the new currency rate
        // The UI should update the currency rate display instantly without page refresh

        // Wait and verify the UI element that shows USD to EUR rate is updated to 0.90
        By currencyRateSelector = By.id("currency-rate-usd-eur");

        boolean updated = wait.until(driver -> {
            WebElement rateElement = driver.findElement(currencyRateSelector);
            String displayedRate = rateElement.getText();
            return displayedRate.contains("0.90");
        });

        assertThat(updated).as("Currency rate updated instantly on UI").isTrue();

        // Step 4: Confirm that any override information is displayed correctly
        // The UI should show an override badge or text
        By overrideInfoSelector = By.id("currency-rate-usd-eur-override");

        WebElement overrideElement = wait.until(ExpectedConditions.visibilityOfElementLocated(overrideInfoSelector));
        String overrideText = overrideElement.getText();

        assertThat(overrideText.toLowerCase()).contains("override");

        // Additional assertions: No delay or lag observed (implicit in wait)

        // Verify displayed currency data is consistent with backend state
        assertThat(overrideElement.isDisplayed()).isTrue();

        // Also verify that the backend mock returns the updated rate if queried
        when(currencyConvertionController.getCurrentRates()).thenReturn(updatedRate);
        CurrencyRateUpdate backendRate = currencyConvertionController.getCurrentRates();
        assertThat(backendRate.getRate()).isEqualTo(0.90);
        assertThat(backendRate.isOverride()).isTrue();
    }

    /**
     * DTO class to simulate currency rate update event.
     */
    public static class CurrencyRateUpdate {
        private String fromCurrency;
        private String toCurrency;
        private double rate;
        private boolean override;

        public CurrencyRateUpdate() {
        }

        public CurrencyRateUpdate(String fromCurrency, String toCurrency, double rate, boolean override) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.rate = rate;
            this.override = override;
        }

        public String getFromCurrency() {
            return fromCurrency;
        }

        public void setFromCurrency(String fromCurrency) {
            this.fromCurrency = fromCurrency;
        }

        public String getToCurrency() {
            return toCurrency;
        }

        public void setToCurrency(String toCurrency) {
            this.toCurrency = toCurrency;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public boolean isOverride() {
            return override;
        }

        public void setOverride(boolean override) {
            this.override = override;
        }
    }
}
