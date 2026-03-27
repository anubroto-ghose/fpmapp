/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8608
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 08:08:39
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration Selenium test for WebSocket connection management under resource constraints.
 * 
 * Preconditions:
 * - User is logged into FPM_UI.
 * - WebSocket connection is established.
 * - System resource usage is monitored.
 * 
 * Test Steps:
 * 1. Simulate multiple rapid backend events triggering real-time updates.
 * 2. Monitor WebSocket connection stability and resource consumption on client side.
 * 3. Verify connection stability and resource usage.
 * 4. Check UI updates reflect real-time data without lag or failure.
 * 
 * Expected Results:
 * - WebSocket connection remains stable without disconnects or errors.
 * - Resource usage remains optimized.
 * - Real-time updates delivered and displayed correctly.
 * - No memory leaks or excessive reconnections.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class WebSocketConnectionManagementTest {

    private static WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver with headless options for CI
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock currency conversion response
        when(currencyConvertionController.convertCurrency(anyString(), anyString(), anyDouble()))
            .thenAnswer(invocation -> {
                String from = invocation.getArgument(0);
                String to = invocation.getArgument(1);
                Double amount = invocation.getArgument(2);
                // Simple mock conversion rate
                double rate = 1.1;
                if ("USD".equals(from) && "EUR".equals(to)) {
                    rate = 0.9;
                }
                return amount * rate;
            });

        // Mock forecast controller to simulate backend events
        doNothing().when(fpmForecastController).triggerRealTimeUpdate(anyString());

        // Mock common controller for audit trail updates
        doNothing().when(fpmCommonController).logAuditTrail(anyString(), anyString());
    }

    @Test
    public void testWebSocketConnectionStabilityUnderLoad() throws InterruptedException {
        // Step 0: Login user
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("Test@1234");
        loginButton.click();

        // Wait for dashboard page to load
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            d -> d.getCurrentUrl().contains("/dashboard")
        );

        // Step 1: Ensure WebSocket connection is established
        Boolean wsConnected = (Boolean) ((JavascriptExecutor) driver).executeScript(
            "return window.fpmWebSocket && window.fpmWebSocket.readyState === 1;"
        );
        assertTrue(wsConnected, "WebSocket should be connected after login");

        // Step 2: Simulate multiple rapid backend events triggering real-time updates
        // We'll simulate 50 rapid events
        int eventCount = 50;

        // Use JS to simulate backend pushing events via WebSocket
        String simulateEventsScript =
            "var count = arguments[0];" +
            "var ws = window.fpmWebSocket;" +
            "if (!ws || ws.readyState !== 1) return false;" +
            "for (var i=0; i<count; i++) {" +
            "  setTimeout(() => {" +
            "    var event = JSON.stringify({type: 'update', id: i, message: 'Real-time update ' + i});" +
            "    ws.onmessage({data: event});" +
            "  }, i*10);" +
            "}" +
            "return true;";

        Boolean eventsSimulated = (Boolean) ((JavascriptExecutor) driver).executeScript(simulateEventsScript, eventCount);
        assertTrue(eventsSimulated, "Should simulate backend events via WebSocket");

        // Step 3: Monitor WebSocket connection stability and resource consumption
        // We'll wait for all events to be processed (eventCount * 10ms + buffer)
        Thread.sleep(eventCount * 10 + 1000);

        // Check WebSocket connection still open
        wsConnected = (Boolean) ((JavascriptExecutor) driver).executeScript(
            "return window.fpmWebSocket && window.fpmWebSocket.readyState === 1;"
        );
        assertTrue(wsConnected, "WebSocket should remain connected after rapid events");

        // Step 4: Verify UI updates reflect real-time data
        // The UI should have appended messages for each event
        // Assuming messages are appended to an element with id 'realtimeUpdates'
        WebElement updatesContainer = driver.findElement(By.id("realtimeUpdates"));

        // Wait until all messages appear or timeout
        new WebDriverWait(driver, Duration.ofSeconds(5)).until((ExpectedCondition<Boolean>) d -> {
            WebElement container = d.findElement(By.id("realtimeUpdates"));
            return container.getText().split("Real-time update").length - 1 >= eventCount;
        });

        String updatesText = updatesContainer.getText();
        int receivedUpdates = updatesText.split("Real-time update").length - 1;
        assertEquals(eventCount, receivedUpdates, "All real-time updates should be displayed in UI");

        // Step 5: Check resource usage (CPU, memory) - limited in Selenium, so we check for no excessive reconnections
        // Check reconnection count tracked in JS variable window.wsReconnectionCount
        Long reconnectionCount = (Long) ((JavascriptExecutor) driver).executeScript(
            "return window.wsReconnectionCount || 0;"
        );
        assertTrue(reconnectionCount <= 1, "WebSocket reconnections should be minimal (<=1)");

        // Step 6: Check no errors in browser console
        // Selenium does not provide direct access to console logs in all drivers,
        // but ChromeDriver supports it via LogEntries
        var logs = driver.manage().logs().get("browser");
        boolean hasErrors = logs.getAll().stream()
            .anyMatch(log -> log.getLevel().toString().equals("SEVERE"));
        assertFalse(hasErrors, "Browser console should have no severe errors");
    }
}
