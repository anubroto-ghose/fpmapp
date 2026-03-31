/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8928
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 15:01:32
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.time.Duration;
import java.time.Instant;
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
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
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

import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test verifying automatic fetching and updating of currency exchange rates at configured intervals.
 * 
 * Preconditions:
 * - System connected to third-party currency exchange API (mocked).
 * - Sync interval configured and active.
 * - User has access to view currency exchange rates.
 * 
 * Test Steps:
 * 1. Wait for the configured sync interval to elapse.
 * 2. Observe system fetching new currency exchange rates automatically.
 * 3. Verify currency exchange rates updated in the system.
 * 4. Check system logs for successful sync job execution.
 * 
 * Expected Results:
 * - Automatic fetch and update at each interval without manual intervention.
 * - Updated rates reflected immediately.
 * - Sync job logs show successful completion without errors.
 * - No data inconsistencies or delays.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyExchangeRateSyncIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private CurrencyConvertionController realCurrencyConvertionController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final Duration SYNC_INTERVAL = Duration.ofSeconds(10); // Example sync interval for test

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock the currency conversion controller to simulate third-party API response
        Map<String, Double> mockedRates = new HashMap<>();
        mockedRates.put("USD", 1.0);
        mockedRates.put("EUR", 0.85);
        mockedRates.put("GBP", 0.75);

        doReturn(mockedRates).when(currencyConvertionController).fetchLatestExchangeRates();

        // Assume sync job is triggered automatically by the system at configured intervals
    }

    @Test
    public void testAutomaticCurrencyExchangeRateSync() throws InterruptedException {
        // Step 1: Navigate to currency exchange rates page
        driver.get(BASE_URL + "/currency-rates");

        // Step 2: Capture initial rates displayed
        Map<String, Double> initialRates = getDisplayedRates();
        assertThat(initialRates).isNotEmpty();

        // Step 3: Wait for sync interval + buffer to allow automatic update
        TimeUnit.SECONDS.sleep(SYNC_INTERVAL.getSeconds() + 5);

        // Step 4: Mock updated rates to simulate new data from third-party API
        Map<String, Double> updatedRates = new HashMap<>();
        updatedRates.put("USD", 1.0);
        updatedRates.put("EUR", 0.88); // updated rate
        updatedRates.put("GBP", 0.77); // updated rate

        doReturn(updatedRates).when(currencyConvertionController).fetchLatestExchangeRates();

        // Step 5: Trigger manual refresh or wait for next automatic refresh
        // For this test, we simulate waiting for next sync interval
        TimeUnit.SECONDS.sleep(SYNC_INTERVAL.getSeconds() + 5);

        // Step 6: Refresh page to reflect updated rates
        driver.navigate().refresh();

        // Step 7: Capture updated rates displayed
        Map<String, Double> displayedUpdatedRates = getDisplayedRates();

        // Step 8: Verify that rates have been updated
        assertThat(displayedUpdatedRates.get("EUR")).isEqualTo(updatedRates.get("EUR"));
        assertThat(displayedUpdatedRates.get("GBP")).isEqualTo(updatedRates.get("GBP"));

        // Step 9: Verify sync job logs (simulate by checking a log element on UI or API call)
        driver.get(BASE_URL + "/sync-logs");
        WebElement lastLogEntry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".log-entry:first-child")));
        String logText = lastLogEntry.getText();
        assertThat(logText).containsIgnoringCase("Currency exchange rates sync completed successfully");

        // Step 10: Verify no errors shown on UI
        WebElement errorBanner = driver.findElement(By.id("error-banner"));
        assertThat(errorBanner.isDisplayed()).isFalse();
    }

    /**
     * Helper method to parse displayed currency rates from the UI.
     * Assumes rates are displayed in a table with id 'currency-rates-table' with rows containing currency code and rate.
     *
     * @return Map of currency code to rate value
     */
    private Map<String, Double> getDisplayedRates() {
        Map<String, Double> rates = new HashMap<>();
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currency-rates-table")));
        for (WebElement row : table.findElements(By.tagName("tr"))) {
            try {
                WebElement codeCell = row.findElement(By.cssSelector("td.currency-code"));
                WebElement rateCell = row.findElement(By.cssSelector("td.currency-rate"));
                String code = codeCell.getText().trim();
                String rateText = rateCell.getText().trim();
                double rate = Double.parseDouble(rateText);
                rates.put(code, rate);
            } catch (Exception e) {
                // Skip header or malformed rows
            }
        }
        return rates;
    }
}
