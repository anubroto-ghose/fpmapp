/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8940
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:50:07
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.entities.CurrencyExchangeRate;
import com.webapp.fpmapp.repositories.CurrencyExchangeRateRepository;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test verifying real-time currency rate fetching and storage.
 * 
 * Preconditions:
 * - System configured with valid third-party currency API.
 * - Configured interval for fetching real-time rates is set.
 * - Database accessible and ready.
 * 
 * This test mocks the external API call and verifies the system behavior end-to-end,
 * including UI verification via Selenium WebDriver.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyRateFetchingIntegrationTest {

    private static WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private CurrencyExchangeRateRepository currencyExchangeRateRepository;

    private static final String BASE_URL = "http://localhost:8080";

    private static final long FETCH_INTERVAL_MILLIS = 5000; // 5 seconds for test

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
        // Clear DB before each test
        currencyExchangeRateRepository.deleteAll();
    }

    /**
     * Test verifies that the system fetches real-time currency rates at configured intervals,
     * stores them correctly with accurate timestamps, and sets is_historical flag to false.
     * It mocks the third-party API response and verifies DB and UI.
     */
    @Test
    public void testRealTimeCurrencyRateFetchingAndStorage() throws InterruptedException {
        // Mocked currency rates returned by third-party API
        Map<String, Double> mockedRates = new HashMap<>();
        mockedRates.put("USD", 1.0);
        mockedRates.put("EUR", 0.85);
        mockedRates.put("JPY", 110.5);

        // Mock the external API call to return mockedRates
        when(currencyConvertionController.fetchLatestRates()).thenReturn(mockedRates);

        // Start the currency rate fetching service (simulate by calling the method directly or via REST)
        // For this test, assume a REST endpoint triggers the fetch
        driver.get(BASE_URL + "/currency/fetch-start");

        // Wait for configured interval + buffer to allow fetch to happen
        TimeUnit.MILLISECONDS.sleep(FETCH_INTERVAL_MILLIS + 2000);

        // Verify that the controller's fetchLatestRates was called at least once
        verify(currencyConvertionController, times(1)).fetchLatestRates();

        // Verify DB entries
        Iterable<CurrencyExchangeRate> storedRates = currencyExchangeRateRepository.findAll();

        assertNotNull(storedRates, "Stored currency rates should not be null");

        int count = 0;
        Instant now = Instant.now();

        for (CurrencyExchangeRate rate : storedRates) {
            count++;
            assertTrue(mockedRates.containsKey(rate.getCurrencyCode()), "Stored currency code should be in mocked rates");
            assertEquals(mockedRates.get(rate.getCurrencyCode()), rate.getRate(), 0.0001, "Stored rate should match mocked rate");
            assertFalse(rate.isHistorical(), "is_historical flag should be false for real-time rates");

            // Check timestamp is recent (within last 1 minute)
            Instant rateTimestamp = rate.getRateTimestamp();
            assertNotNull(rateTimestamp, "Rate timestamp should not be null");
            long secondsDiff = ChronoUnit.SECONDS.between(rateTimestamp, now);
            assertTrue(secondsDiff >= 0 && secondsDiff < 60, "Rate timestamp should be recent");
        }

        assertEquals(mockedRates.size(), count, "Number of stored rates should match mocked rates count");

        // UI Verification: Navigate to currency rates page and verify displayed data
        driver.get(BASE_URL + "/currency/rates");

        for (Map.Entry<String, Double> entry : mockedRates.entrySet()) {
            String currencyCode = entry.getKey();
            Double expectedRate = entry.getValue();

            // Locate row by currency code
            WebElement row = driver.findElement(By.xpath("//tr[td/text()='" + currencyCode + "']"));
            assertNotNull(row, "Currency row should be present in UI for " + currencyCode);

            WebElement rateCell = row.findElement(By.xpath("td[@class='rate']"));
            assertNotNull(rateCell, "Rate cell should be present for " + currencyCode);

            double displayedRate = Double.parseDouble(rateCell.getText());
            assertEquals(expectedRate, displayedRate, 0.0001, "Displayed rate should match expected rate for " + currencyCode);

            WebElement historicalCell = row.findElement(By.xpath("td[@class='historical']"));
            assertNotNull(historicalCell, "Historical flag cell should be present for " + currencyCode);
            assertEquals("false", historicalCell.getText().toLowerCase(), "Historical flag should be false in UI for " + currencyCode);
        }
    }
}
