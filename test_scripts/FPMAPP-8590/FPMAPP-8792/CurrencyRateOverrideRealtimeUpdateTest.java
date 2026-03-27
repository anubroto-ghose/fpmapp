/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8792
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:07:09
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

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

/**
 * Integration Selenium test for immediate currency rate override reflection in UI.
 * 
 * Preconditions:
 * - User logged in
 * - CurrencyOverridePanel visible and active
 * - WebSocket connection established
 * 
 * Test Steps:
 * 1. Simulate currency rate override from another user/system via mocked WebSocket message
 * 2. Verify UI updates immediately without page refresh
 * 3. Validate no error notifications and UI responsiveness
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyRateOverrideRealtimeUpdateTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static TestWebSocketClient wsClient;

    private static final String TEST_CURRENCY_FROM = "USD";
    private static final String TEST_CURRENCY_TO = "EUR";
    private static final double OVERRIDDEN_RATE = 0.85;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
        if (wsClient != null) {
            wsClient.closeSession();
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        // Mock the currency conversion controller to return initial rate
        when(currencyConvertionController.getCurrentRate(TEST_CURRENCY_FROM, TEST_CURRENCY_TO))
                .thenReturn(0.80); // initial rate

        // Navigate to the FPM Tools UI login page
        driver.get("http://localhost:" + port + "/login");

        // Perform login (assuming test user credentials)
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("Test@1234");
        loginButton.click();

        // Wait for main dashboard page to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Verify CurrencyOverridePanel is visible and active
        WebElement currencyPanel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyOverridePanel")));
        assertThat(currencyPanel.isDisplayed()).isTrue();

        // Establish WebSocket client connection to simulate real-time updates
        wsClient = new TestWebSocketClient(new URI("ws://localhost:" + port + "/ws/currency-updates"));
        wsClient.connect();
    }

    @Test
    public void testImmediateCurrencyRateOverrideReflection() throws Exception {
        // Locate the element displaying the currency rate
        WebElement rateDisplay = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyRateDisplay")));

        // Assert initial rate is displayed
        String initialRateText = rateDisplay.getText();
        assertThat(initialRateText).contains("0.80");

        // Prepare latch to wait for UI update
        CountDownLatch latch = new CountDownLatch(1);

        // Inject JavaScript listener to detect UI update for currency rate
        ((JavascriptExecutor) driver).executeScript(
                "window.currencyRateUpdated = false;"
                        + "var targetNode = document.getElementById('currencyRateDisplay');"
                        + "var observer = new MutationObserver(function(mutations) {"
                        + "  window.currencyRateUpdated = true;"
                        + "  observer.disconnect();"
                        + "  var event = new Event('currencyRateUpdated');"
                        + "  targetNode.dispatchEvent(event);"
                        + "});"
                        + "observer.observe(targetNode, { childList: true, characterData: true, subtree: true });");

        // Add event listener to count down latch when update happens
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('currencyRateDisplay').addEventListener('currencyRateUpdated', function() {"
                        + "window.currencyRateUpdated = true;"
                        + "});");

        // Simulate currency rate override from another user/system via WebSocket message
        String overrideMessage = String.format(
                "{\"currencyFrom\":\"%s\",\"currencyTo\":\"%s\",\"newRate\":%s,\"validationMessage\":\"Rate override successful\"}",
                TEST_CURRENCY_FROM, TEST_CURRENCY_TO, OVERRIDDEN_RATE);

        wsClient.sendMessage(overrideMessage);

        // Wait up to 10 seconds for UI to update
        boolean updated = wait.until(driver -> {
            Object updatedFlag = ((JavascriptExecutor) driver).executeScript("return window.currencyRateUpdated;");
            return updatedFlag != null && (Boolean) updatedFlag;
        });

        assertThat(updated).isTrue();

        // Verify the displayed rate is updated to overridden rate
        String updatedRateText = rateDisplay.getText();
        assertThat(updatedRateText).contains(String.format("%.2f", OVERRIDDEN_RATE));

        // Verify the CurrencyOverridePanel shows validation message
        WebElement validationMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyOverrideValidationMessage")));
        assertThat(validationMsg.getText()).isEqualTo("Rate override successful");

        // Verify no full page reload occurred by checking URL remains the same
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("/dashboard");

        // Verify UI remains responsive: try clicking a button (e.g. refresh button) and assert no error
        WebElement refreshButton = driver.findElement(By.id("refreshDataButton"));
        refreshButton.click();

        // Wait briefly and check no error notification
        Thread.sleep(1000);
        boolean errorNotificationPresent = driver.findElements(By.className("error-notification")).size() > 0;
        assertThat(errorNotificationPresent).isFalse();
    }

    /**
     * Simple WebSocket client to simulate server push messages for currency rate updates.
     */
    @ClientEndpoint
    public static class TestWebSocketClient {

        private Session userSession = null;
        private final URI endpointURI;

        public TestWebSocketClient(URI endpointURI) {
            this.endpointURI = endpointURI;
        }

        public void connect() throws Exception {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        }

        @OnMessage
        public void onMessage(String message) {
            // No-op for this test client
        }

        public void sendMessage(String message) throws Exception {
            if (userSession != null && userSession.isOpen()) {
                userSession.getAsyncRemote().sendText(message);
            } else {
                throw new IllegalStateException("WebSocket session is not open.");
            }
        }

        @jakarta.websocket.OnOpen
        public void onOpen(Session session) {
            this.userSession = session;
        }

        @jakarta.websocket.OnClose
        public void onClose(Session session) {
            this.userSession = null;
        }

        public void closeSession() {
            try {
                if (userSession != null && userSession.isOpen()) {
                    userSession.close();
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
