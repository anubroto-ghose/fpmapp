/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8797
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:04:06
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration test verifying the system fetches real-time currency rates at configured intervals.
 * 
 * Preconditions:
 * - System configured with valid third-party currency exchange API.
 * - Fetch interval set to 5 minutes.
 * - Scheduler and system clock operational.
 * 
 * This test uses Selenium WebDriver to simulate UI interaction and verify logs/audit trail.
 * It mocks the CurrencyConvertionController to simulate API responses.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RealTimeCurrencyFetchIntegrationTest {

    private static WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final int FETCH_INTERVAL_SECONDS = 5 * 60; // 5 minutes

    private static final String LOGS_PAGE_URL = BASE_URL + "/admin/logs";

    private static final String CURRENCY_RATES_API_PATH = "/api/currency/latest";

    private static final AtomicInteger fetchCount = new AtomicInteger(0);

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
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

    /**
     * Test verifies that the system fetches real-time currency rates automatically at configured intervals,
     * logs each fetch event with timestamp, and stores data correctly.
     */
    @Test
    public void testRealTimeCurrencyFetchAtConfiguredIntervals() throws InterruptedException {
        // Mock the currency conversion API response
        when(currencyConvertionController.fetchLatestRates()).thenAnswer(invocation -> {
            fetchCount.incrementAndGet();
            Map<String, Double> rates = new HashMap<>();
            rates.put("USD", 1.0);
            rates.put("EUR", 0.85);
            rates.put("JPY", 110.0);
            return rates;
        });

        // Step 1: Start the system and ensure scheduler service is running
        driver.get(BASE_URL + "/login");

        // Simulate login as admin to access logs
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("admin123");
        loginButton.click();

        // Wait for redirect to dashboard
        Thread.sleep(2000);
        assertThat(driver.getCurrentUrl()).contains("/dashboard");

        // Step 2: Observe system behavior over a period longer than configured interval
        // Wait for 2 intervals + buffer (e.g., 11 minutes)
        int waitSeconds = FETCH_INTERVAL_SECONDS * 2 + 60;
        Instant start = Instant.now();

        // Poll every 30 seconds to check fetch count
        int polls = waitSeconds / 30;
        int lastFetchCount = 0;
        boolean fetchOccurred = false;

        for (int i = 0; i < polls; i++) {
            Thread.sleep(30000); // 30 seconds
            int currentCount = fetchCount.get();
            if (currentCount > lastFetchCount) {
                fetchOccurred = true;
                lastFetchCount = currentCount;
            }
        }

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);

        // Step 3: Verify system automatically fetched real-time currency rates at each configured interval
        assertThat(fetchCount.get())
            .withFailMessage("Expected at least 2 fetches but got %d", fetchCount.get())
            .isGreaterThanOrEqualTo(2);

        // Step 4: Check logs or audit trails for successful fetch events
        driver.get(LOGS_PAGE_URL);
        Thread.sleep(2000); // wait for logs page to load

        // Verify logs contain fetch events with timestamps
        // Assuming logs are in a table with id 'logsTable' and each row has class 'log-entry'
        boolean foundFetchLog = false;
        for (WebElement row : driver.findElements(By.cssSelector("#logsTable .log-entry"))) {
            String logText = row.getText();
            if (logText.contains("Currency rates fetched successfully")) {
                foundFetchLog = true;
                // Verify timestamp format (simple check for date/time presence)
                assertThat(logText).matches(".*\\d{4}-\\d{2}-\\d{2}.*\\d{2}:\\d{2}:\\d{2}.*");
            }
        }

        assertThat(foundFetchLog).withFailMessage("No successful currency fetch log entries found").isTrue();

        // Verify no error logs
        boolean foundErrorLog = false;
        for (WebElement row : driver.findElements(By.cssSelector("#logsTable .log-entry"))) {
            String logText = row.getText().toLowerCase();
            if (logText.contains("error") || logText.contains("exception") || logText.contains("fail")) {
                foundErrorLog = true;
                break;
            }
        }

        assertThat(foundErrorLog).withFailMessage("Error logs found during currency fetch process").isFalse();

        // Verify fetched data stored correctly in DB via service call
        Map<String, Double> storedRates = fpmCommonController.getLatestCurrencyRates();
        assertThat(storedRates).isNotNull();
        assertThat(storedRates).containsKeys("USD", "EUR", "JPY");
        assertThat(storedRates.get("USD")).isEqualTo(1.0);
        assertThat(storedRates.get("EUR")).isEqualTo(0.85);
        assertThat(storedRates.get("JPY")).isEqualTo(110.0);

        // Verify the mocked controller was called at least twice
        verify(currencyConvertionController, times(fetchCount.get())).fetchLatestRates();
    }
}
