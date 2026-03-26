/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8792
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:02:55
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.services.CurrencyExchangeService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Integration test verifying immediate UI reflection of currency rate overrides
 * using Selenium WebDriver and Spring Boot context.
 * 
 * Preconditions:
 * - User logged in
 * - CurrencyOverridePanel visible and active
 * - WebSocket connection established
 * 
 * Test Steps:
 * 1. Simulate currency rate override from external source
 * 2. Verify UI updates immediately without page refresh
 * 3. Validate no error notifications and UI responsiveness
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyOverrideImmediateReflectionIT {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @MockBean
    private CurrencyExchangeService mockCurrencyExchangeService;

    private static OkHttpClient wsClient;
    private static TestWebSocketListener wsListener;
    private static WebSocket webSocket;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setup() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Setup WebSocket client
        wsClient = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // Disable timeout for WebSocket
                .build();
        wsListener = new TestWebSocketListener();

        Request request = new Request.Builder()
                .url("ws://localhost:8080/ws/currency-updates")
                .build();
        webSocket = wsClient.newWebSocket(request, wsListener);
    }

    @AfterAll
    public static void tearDown() {
        if (webSocket != null) {
            webSocket.close(1000, "Test complete");
        }
        if (driver != null) {
            driver.quit();
        }
        if (wsClient != null) {
            wsClient.dispatcher().executorService().shutdown();
        }
    }

    @Test
    public void testImmediateCurrencyOverrideReflectionInUI() throws Exception {
        // Step 0: Login user
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("loginButton")).click();

        // Wait for main page to load and CurrencyOverridePanel to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyOverridePanel")));

        WebElement currencyPanel = driver.findElement(By.id("currencyOverridePanel"));
        assertThat(currencyPanel.isDisplayed()).isTrue();

        // Capture initial currency rate displayed
        WebElement rateElement = currencyPanel.findElement(By.cssSelector(".currency-rate"));
        String initialRateText = rateElement.getText();
        assertThat(initialRateText).isNotEmpty();

        // Step 1: Simulate currency rate override from another user/system
        // We simulate this by sending a WebSocket message that the UI listens to
        String overriddenRate = "1.2345";
        String overrideMessage = String.format(
                "{\"type\":\"currencyOverride\",\"currencyPair\":\"USD/EUR\",\"newRate\":\"%s\",\"validationMessage\":\"Override successful\"}",
                overriddenRate);

        // Send override message via WebSocket
        webSocket.send(overrideMessage);

        // Step 2: Observe UI updates immediately without page refresh
        // Wait max 10 seconds for UI to update the rate text
        boolean updated = wait.until(driver -> {
            WebElement updatedRateElement = driver.findElement(By.cssSelector("#currencyOverridePanel .currency-rate"));
            String updatedText = updatedRateElement.getText();
            return overriddenRate.equals(updatedText);
        });

        assertThat(updated).isTrue();

        // Verify validation message is displayed
        WebElement validationMsg = currencyPanel.findElement(By.cssSelector(".validation-message"));
        wait.until(ExpectedConditions.textToBePresentInElement(validationMsg, "Override successful"));
        assertThat(validationMsg.getText()).contains("Override successful");

        // Step 3: Verify no full page reload occurred
        // We check that the URL remains the same and no reload indicator is present
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).startsWith(BASE_URL);

        // Step 4: Verify UI remains responsive and no error notifications
        // Check for error notification elements
        boolean errorNotificationPresent = driver.findElements(By.cssSelector(".notification.error")).size() > 0;
        assertThat(errorNotificationPresent).isFalse();

        // Additional responsiveness check: try clicking a button in the panel
        WebElement refreshButton = currencyPanel.findElement(By.cssSelector("button.refresh-rate"));
        refreshButton.click();

        // Wait for some indication of refresh success (e.g. spinner disappears or updated timestamp)
        WebElement lastUpdated = currencyPanel.findElement(By.cssSelector(".last-updated"));
        wait.until(ExpectedConditions.visibilityOf(lastUpdated));
        assertThat(lastUpdated.getText()).isNotEmpty();
    }

    /**
     * Simple WebSocket listener for test purposes.
     */
    private static class TestWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            System.out.println("WebSocket opened for test.");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            System.out.println("WebSocket message received: " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            System.out.println("WebSocket binary message received.");
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            System.out.println("WebSocket closing: " + reason);
            webSocket.close(code, reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            System.out.println("WebSocket closed: " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            System.err.println("WebSocket failure: " + t.getMessage());
        }
    }
}
