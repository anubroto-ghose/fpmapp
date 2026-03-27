/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8817
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:51:11
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test verifying real-time currency exchange rates UI updates.
 * 
 * Preconditions:
 * - User is logged in with access to currency exchange UI.
 * - Real-time currency exchange rates are fetched and stored correctly (mocked).
 * 
 * Test verifies automatic UI refresh, timestamps, override flags, and no stale data.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CurrencyExchangeRatesRealtimeTest {

    private static WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String CURRENCY_PAGE_URL = BASE_URL + "/currency-exchange";

    // Mocked currency data
    private Map<String, CurrencyRate> initialRates;
    private Map<String, CurrencyRate> updatedRates;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Prepare initial mocked currency rates
        initialRates = new HashMap<>();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        initialRates.put("USD_EUR", new CurrencyRate("USD", "EUR", 0.85, now, false));
        initialRates.put("USD_GBP", new CurrencyRate("USD", "GBP", 0.75, now, false));

        // Prepare updated mocked currency rates (simulate change after interval)
        Instant later = now.plus(1, ChronoUnit.MINUTES);
        updatedRates = new HashMap<>();
        updatedRates.put("USD_EUR", new CurrencyRate("USD", "EUR", 0.86, later, true)); // override flag true
        updatedRates.put("USD_GBP", new CurrencyRate("USD", "GBP", 0.76, later, false));

        // Mock the service to return initial rates first, then updated rates
        when(currencyConvertionController.getLatestRates())
            .thenReturn(initialRates)
            .thenReturn(updatedRates);
    }

    @Test
    public void testCurrencyExchangeRatesAutoRefresh() {
        // Step 1: Navigate to currency exchange rates display page
        driver.get(CURRENCY_PAGE_URL);

        // Step 2: Observe displayed currency rates and timestamps
        verifyRatesDisplayed(initialRates);

        // Step 3: Wait for the configured interval for real-time updates (simulate 65 seconds)
        // The UI is expected to auto-refresh without manual reload
        waitForAutoRefresh(70);

        // Step 4: Verify UI automatically refreshes and displays updated rates
        verifyRatesDisplayed(updatedRates);

        // Step 5: Check override flags and timestamps are correctly shown
        verifyOverrideFlagsAndTimestamps(updatedRates);
    }

    private void verifyRatesDisplayed(Map<String, CurrencyRate> expectedRates) {
        for (Map.Entry<String, CurrencyRate> entry : expectedRates.entrySet()) {
            String pair = entry.getKey();
            CurrencyRate rate = entry.getValue();

            // Locate the row for the currency pair
            WebElement row = findCurrencyRow(pair);
            assertThat(row).withFailMessage("Currency row for %s should be present", pair).isNotNull();

            // Verify rate value
            WebElement rateElement = row.findElement(By.cssSelector(".rate-value"));
            String rateText = rateElement.getText();
            assertThat(rateText).withFailMessage("Rate text for %s should be %.2f", pair, rate.getRate())
                .isEqualTo(String.format("%.2f", rate.getRate()));

            // Verify timestamp
            WebElement timestampElement = row.findElement(By.cssSelector(".rate-timestamp"));
            String timestampText = timestampElement.getText();
            assertThat(timestampText).withFailMessage("Timestamp for %s should match", pair)
                .contains(rate.getTimestamp().toString().substring(0, 19)); // ISO string truncated
        }
    }

    private void verifyOverrideFlagsAndTimestamps(Map<String, CurrencyRate> expectedRates) {
        for (Map.Entry<String, CurrencyRate> entry : expectedRates.entrySet()) {
            String pair = entry.getKey();
            CurrencyRate rate = entry.getValue();

            WebElement row = findCurrencyRow(pair);
            assertThat(row).withFailMessage("Currency row for %s should be present", pair).isNotNull();

            WebElement overrideFlagElement = null;
            try {
                overrideFlagElement = row.findElement(By.cssSelector(".override-flag"));
            } catch (NoSuchElementException e) {
                // If override flag is false, element may not be present
            }

            if (rate.isOverrideFlag()) {
                assertThat(overrideFlagElement).withFailMessage("Override flag should be visible for %s", pair).isNotNull();
                assertThat(overrideFlagElement.getText()).isEqualToIgnoringCase("OVERRIDE");
            } else {
                assertThat(overrideFlagElement).withFailMessage("Override flag should NOT be visible for %s", pair).isNull();
            }

            // Timestamp already verified in verifyRatesDisplayed
        }
    }

    private WebElement findCurrencyRow(String currencyPair) {
        // Assuming each currency pair row has an attribute data-pair="USD_EUR" etc.
        try {
            return driver.findElement(By.cssSelector(String.format("tr[data-pair='%s']", currencyPair)));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private void waitForAutoRefresh(int seconds) {
        // Wait for the UI to refresh automatically by polling the DOM
        // We wait until the timestamp of USD_EUR rate changes to the updated timestamp

        Instant expectedTimestamp = updatedRates.get("USD_EUR").getTimestamp();
        WebDriverWait wait = new WebDriverWait(driver, seconds);

        boolean refreshed = wait.until((ExpectedCondition<Boolean>) d -> {
            WebElement row = findCurrencyRow("USD_EUR");
            if (row == null) return false;
            WebElement timestampElement = row.findElement(By.cssSelector(".rate-timestamp"));
            String timestampText = timestampElement.getText();
            return timestampText.contains(expectedTimestamp.toString().substring(0, 19));
        });

        assertThat(refreshed).withFailMessage("UI did not auto-refresh with updated currency rates within %d seconds", seconds).isTrue();
    }

    /**
     * Simple DTO to hold currency rate info for mocking and verification.
     */
    private static class CurrencyRate {
        private final String fromCurrency;
        private final String toCurrency;
        private final double rate;
        private final Instant timestamp;
        private final boolean overrideFlag;

        public CurrencyRate(String fromCurrency, String toCurrency, double rate, Instant timestamp, boolean overrideFlag) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.rate = rate;
            this.timestamp = timestamp;
            this.overrideFlag = overrideFlag;
        }

        public String getFromCurrency() {
            return fromCurrency;
        }

        public String getToCurrency() {
            return toCurrency;
        }

        public double getRate() {
            return rate;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public boolean isOverrideFlag() {
            return overrideFlag;
        }
    }
}
