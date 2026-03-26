/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8815
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:48:04
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Integration test verifying the system fetches and stores real-time currency exchange rates at configured intervals.
 * 
 * Preconditions:
 * - System configured with valid third-party API credentials.
 * - Configured interval for fetching real-time rates is set (e.g., every 5 minutes).
 * - Database tables for currency exchange rates are accessible.
 * 
 * This test uses Selenium WebDriver to simulate system start and verify UI elements,
 * and mocks the CurrencyConvertionController service to simulate third-party API calls.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyExchangeRateIntegrationTest {

    private static WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private DataSource dataSource;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI environments)
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

    /**
     * Test verifies that the system fetches and stores real-time currency exchange rates at configured intervals.
     * It mocks the third-party API call and verifies database persistence.
     */
    @Test
    public void testRealTimeCurrencyExchangeRateFetchingAndStorage() throws Exception {
        // Arrange: Prepare mock response from third-party API
        List<CurrencyRate> mockRates = Arrays.asList(
                new CurrencyRate("USD", "EUR", 0.85, Instant.now().truncatedTo(ChronoUnit.SECONDS)),
                new CurrencyRate("USD", "JPY", 110.25, Instant.now().truncatedTo(ChronoUnit.SECONDS))
        );

        when(currencyConvertionController.fetchRealTimeRates()).thenReturn(mockRates);

        // Act: Start the system and simulate the currency fetch service running
        driver.get(BASE_URL + "/currency/fetch-start");

        // Wait for the configured interval (simulate 5 minutes with shorter wait for test)
        TimeUnit.SECONDS.sleep(6); // In real test, this would be 5 minutes or mocked time

        // Verify that the system called the third-party API to fetch rates
        verify(currencyConvertionController, times(1)).fetchRealTimeRates();

        // Verify that the fetched rates are stored in the database
        try (Connection conn = dataSource.getConnection()) {
            for (CurrencyRate rate : mockRates) {
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT exchange_rate, rate_timestamp, is_override FROM currency_exchange_rates " +
                        "WHERE base_currency = ? AND target_currency = ? ORDER BY rate_timestamp DESC LIMIT 1");
                ps.setString(1, rate.getBaseCurrency());
                ps.setString(2, rate.getTargetCurrency());

                try (ResultSet rs = ps.executeQuery()) {
                    Assertions.assertTrue(rs.next(), "No exchange rate found for " + rate.getBaseCurrency() + "->" + rate.getTargetCurrency());
                    double storedRate = rs.getDouble("exchange_rate");
                    Timestamp storedTimestamp = rs.getTimestamp("rate_timestamp");
                    boolean isOverride = rs.getBoolean("is_override");

                    Assertions.assertEquals(rate.getExchangeRate(), storedRate, 0.0001, "Exchange rate mismatch");
                    Assertions.assertNotNull(storedTimestamp, "Rate timestamp should not be null");
                    Assertions.assertFalse(isOverride, "Override flag should be false for real-time fetched rates");
                }
            }
        }

        // Act & Verify: Repeat step 2 and 3 to confirm periodic fetching
        TimeUnit.SECONDS.sleep(6); // Wait for next interval

        verify(currencyConvertionController, times(2)).fetchRealTimeRates();

        // Additional UI verification (optional): Check UI shows last fetch time
        driver.get(BASE_URL + "/currency/status");
        WebElement lastFetchElement = driver.findElement(By.id("lastFetchTimestamp"));
        Assertions.assertNotNull(lastFetchElement, "Last fetch timestamp element should be present");
        String lastFetchText = lastFetchElement.getText();
        Assertions.assertFalse(lastFetchText.isEmpty(), "Last fetch timestamp should be displayed");

        // Verify no errors on UI
        List<WebElement> errorElements = driver.findElements(By.className("error-message"));
        Assertions.assertTrue(errorElements.isEmpty(), "No error messages should be displayed on UI");
    }

    /**
     * Simple DTO representing a currency exchange rate for testing.
     */
    private static class CurrencyRate {
        private final String baseCurrency;
        private final String targetCurrency;
        private final double exchangeRate;
        private final Instant rateTimestamp;

        public CurrencyRate(String baseCurrency, String targetCurrency, double exchangeRate, Instant rateTimestamp) {
            this.baseCurrency = baseCurrency;
            this.targetCurrency = targetCurrency;
            this.exchangeRate = exchangeRate;
            this.rateTimestamp = rateTimestamp;
        }

        public String getBaseCurrency() {
            return baseCurrency;
        }

        public String getTargetCurrency() {
            return targetCurrency;
        }

        public double getExchangeRate() {
            return exchangeRate;
        }

        public Instant getRateTimestamp() {
            return rateTimestamp;
        }
    }
}
