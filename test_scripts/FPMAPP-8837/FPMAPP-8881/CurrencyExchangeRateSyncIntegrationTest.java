/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8881
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:36:46
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Integration test verifying the scheduled currency exchange rate sync job.
 * 
 * Preconditions:
 * - Third-party currency API is mocked and operational.
 * - System configured with API credentials and sync interval.
 * - Database accessible.
 * 
 * This test uses Selenium WebDriver to simulate system start and UI verification,
 * mocks the external API, and verifies DB state.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(TestConfig.class) // Assume TestConfig configures mocks and beans
public class CurrencyExchangeRateSyncIntegrationTest {

    private static WebDriver driver;

    @Autowired
    private DataSource dataSource;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final long SYNC_INTERVAL_MILLIS = 5000; // 5 seconds for test

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
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

    @Test
    public void testCurrencyExchangeRateSyncJob() throws Exception {
        // Mock current and historical exchange rates from third-party API
        Map<String, Double> currentRates = new HashMap<>();
        currentRates.put("USD_EUR", 0.85);
        currentRates.put("USD_GBP", 0.75);

        Map<String, Double> historicalRates = new HashMap<>();
        historicalRates.put("USD_EUR", 0.83);
        historicalRates.put("USD_GBP", 0.73);

        when(currencyConvertionController.fetchCurrentExchangeRates()).thenReturn(currentRates);
        when(currencyConvertionController.fetchHistoricalExchangeRates(any())).thenReturn(historicalRates);

        // Start the system by navigating to the home page (simulate system start)
        driver.get(BASE_URL + "/fpm/home");

        // Verify page loaded
        WebElement header = driver.findElement(By.tagName("h1"));
        assertThat(header.getText()).containsIgnoringCase("FPM Tools");

        // Wait for the scheduled sync job to run at least once
        // The system is configured to sync every SYNC_INTERVAL_MILLIS milliseconds
        TimeUnit.MILLISECONDS.sleep(SYNC_INTERVAL_MILLIS + 2000); // wait a bit longer

        // Verify that the mocked API methods were called
        verify(currencyConvertionController, times(1)).fetchCurrentExchangeRates();
        verify(currencyConvertionController, times(1)).fetchHistoricalExchangeRates(any());

        // Verify database entries for current and historical exchange rates
        try (Connection conn = dataSource.getConnection()) {
            // Check current rates
            String currentSql = "SELECT currency_pair, rate, fetched_at FROM exchange_rates WHERE type = 'CURRENT' ORDER BY fetched_at DESC LIMIT 2";
            try (PreparedStatement ps = conn.prepareStatement(currentSql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    int count = 0;
                    Instant now = Instant.now();
                    while (rs.next()) {
                        String pair = rs.getString("currency_pair");
                        double rate = rs.getDouble("rate");
                        Timestamp fetchedAt = rs.getTimestamp("fetched_at");
                        assertThat(currentRates).containsKey(pair);
                        assertThat(rate).isEqualTo(currentRates.get(pair));
                        // Timestamp should be recent (within last 1 minute)
                        Instant fetchedInstant = fetchedAt.toInstant();
                        assertThat(fetchedInstant).isAfter(now.minus(1, ChronoUnit.MINUTES));
                        count++;
                    }
                    assertThat(count).isEqualTo(currentRates.size());
                }
            }

            // Check historical rates
            String historicalSql = "SELECT currency_pair, rate, fetched_at FROM exchange_rates WHERE type = 'HISTORICAL' ORDER BY fetched_at DESC LIMIT 2";
            try (PreparedStatement ps = conn.prepareStatement(historicalSql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    int count = 0;
                    Instant now = Instant.now();
                    while (rs.next()) {
                        String pair = rs.getString("currency_pair");
                        double rate = rs.getDouble("rate");
                        Timestamp fetchedAt = rs.getTimestamp("fetched_at");
                        assertThat(historicalRates).containsKey(pair);
                        assertThat(rate).isEqualTo(historicalRates.get(pair));
                        Instant fetchedInstant = fetchedAt.toInstant();
                        assertThat(fetchedInstant).isAfter(now.minus(1, ChronoUnit.MINUTES));
                        count++;
                    }
                    assertThat(count).isEqualTo(historicalRates.size());
                }
            }

            // Verify no duplicate entries for the same timestamp and currency pair
            String dupCheckSql = "SELECT currency_pair, fetched_at, COUNT(*) as cnt FROM exchange_rates GROUP BY currency_pair, fetched_at HAVING cnt > 1";
            try (PreparedStatement ps = conn.prepareStatement(dupCheckSql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    assertThat(rs.next()).isFalse(); // no duplicates
                }
            }
        }
    }
}
