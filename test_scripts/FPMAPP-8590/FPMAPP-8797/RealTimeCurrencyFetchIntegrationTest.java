/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8797
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:34:13
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration test verifying the system fetches real-time currency rates at configured intervals.
 * 
 * Preconditions:
 * - System configured with valid third-party currency exchange API.
 * - Configurable interval set (e.g., 5 minutes).
 * - Scheduler service operational.
 * 
 * This test uses Selenium WebDriver to simulate user interaction and verify UI updates,
 * and mocks CurrencyConvertionController to simulate external API calls.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(TestConfig.class) // Assuming TestConfig sets up test beans and configs
public class RealTimeCurrencyFetchIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(RealTimeCurrencyFetchIntegrationTest.class);

    private static WebDriver driver;

    @Autowired
    private FpmCommonController fpmCommonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static final int FETCH_INTERVAL_SECONDS = 5 * 60; // 5 minutes

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver in headless mode
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        logger.info("WebDriver initialized.");
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
            logger.info("WebDriver closed.");
        }
    }

    @Test
    public void testRealTimeCurrencyFetchAtConfiguredIntervals() throws Exception {
        // Mock the currencyConvertionController to simulate successful fetch
        when(currencyConvertionController.fetchRealTimeRates()).thenAnswer(invocation -> {
            logger.info("Mock fetchRealTimeRates called at {}", Instant.now());
            // Simulate fetched data
            return Collections.singletonMap("USD_EUR", 0.85);
        });

        // Start the system scheduler (assumed to be started by Spring Boot context)
        // Navigate to a UI page that shows last fetch timestamp or status
        driver.get("http://localhost:8080/currency-rates");

        // Wait for the page to load and display initial data
        TimeUnit.SECONDS.sleep(2);

        // Capture initial fetch timestamp from UI
        WebElement lastFetchElement = driver.findElement(By.id("lastFetchTimestamp"));
        String initialTimestampText = lastFetchElement.getText();
        logger.info("Initial last fetch timestamp from UI: {}", initialTimestampText);

        // Wait for longer than the configured interval to allow at least one scheduled fetch
        int waitSeconds = FETCH_INTERVAL_SECONDS + 30; // 5 min + 30 sec buffer
        logger.info("Waiting {} seconds to observe scheduled fetches...", waitSeconds);
        TimeUnit.SECONDS.sleep(waitSeconds);

        // Refresh the page to get updated fetch timestamp
        driver.navigate().refresh();
        TimeUnit.SECONDS.sleep(2);

        WebElement updatedFetchElement = driver.findElement(By.id("lastFetchTimestamp"));
        String updatedTimestampText = updatedFetchElement.getText();
        logger.info("Updated last fetch timestamp from UI: {}", updatedTimestampText);

        // Assert that the timestamp has changed indicating a new fetch
        assertThat(updatedTimestampText).isNotEmpty();
        assertThat(updatedTimestampText).isNotEqualTo(initialTimestampText);

        // Verify that the fetchRealTimeRates method was called at least twice (initial + scheduled)
        verify(currencyConvertionController, times(2)).fetchRealTimeRates();

        // Verify audit logs contain successful fetch events
        List<String> auditLogs = fpmCommonController.getAuditLogsForEvent("CURRENCY_RATE_FETCH");
        assertThat(auditLogs).isNotEmpty();
        boolean foundRecentFetch = auditLogs.stream().anyMatch(log -> log.contains("SUCCESS") && log.contains("currency rates fetched"));
        assertThat(foundRecentFetch).isTrue();

        // Verify no errors in audit logs
        boolean foundErrors = auditLogs.stream().anyMatch(log -> log.toLowerCase().contains("error") || log.toLowerCase().contains("fail"));
        assertThat(foundErrors).isFalse();

        // Verify fetched data stored correctly in DB via service call
        Double storedRate = fpmCommonController.getStoredExchangeRate("USD", "EUR");
        assertThat(storedRate).isNotNull();
        assertThat(storedRate).isEqualTo(0.85);
    }
}
