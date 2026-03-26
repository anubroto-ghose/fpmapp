/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8817
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:49:37
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
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
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test verifying real-time currency exchange rates UI updates.
 * 
 * Preconditions:
 * - User logged in with access to currency exchange UI.
 * - Real-time currency rates fetched and stored correctly (mocked).
 * 
 * Test verifies automatic UI refresh with updated rates and override flags.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyExchangeRatesRealTimeUpdateIT {

    private static WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private FpmTravelController fpmTravelController; // Just to ensure context loads

    private static final String BASE_URL = "http://localhost:8080/currency-exchange";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Mocks the currency rates returned by the CurrencyConvertionController.
     * Returns a list of currency rate DTOs with timestamps and override flags.
     */
    private void mockCurrencyRatesResponse(List<CurrencyRateDTO> rates) {
        Mockito.when(currencyConvertionController.getRealTimeRates())
            .thenReturn(rates);
    }

    /**
     * DTO representing currency rate data for mocking and verification.
     */
    public static class CurrencyRateDTO {
        private String currencyPair;
        private double rate;
        private Instant timestamp;
        private boolean overrideFlag;

        public CurrencyRateDTO(String currencyPair, double rate, Instant timestamp, boolean overrideFlag) {
            this.currencyPair = currencyPair;
            this.rate = rate;
            this.timestamp = timestamp;
            this.overrideFlag = overrideFlag;
        }

        public String getCurrencyPair() {
            return currencyPair;
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

    @Test
    public void testRealTimeCurrencyExchangeRatesAutoUpdate() {
        // Prepare initial mocked data
        Instant now = Instant.now();
        CurrencyRateDTO initialRate = new CurrencyRateDTO("USD/EUR", 0.85, now, false);
        CurrencyRateDTO initialRate2 = new CurrencyRateDTO("USD/JPY", 110.25, now, true);
        mockCurrencyRatesResponse(List.of(initialRate, initialRate2));

        // Navigate to currency exchange rates page
        driver.get(BASE_URL);

        // Verify page loaded and user is logged in (assumed by presence of currency table)
        WebElement currencyTable = driver.findElement(By.id("currency-rates-table"));
        assertThat(currencyTable).isNotNull();

        // Verify initial currency rates and timestamps displayed
        verifyCurrencyRateDisplayed("USD/EUR", 0.85, now, false);
        verifyCurrencyRateDisplayed("USD/JPY", 110.25, now, true);

        // Prepare updated mocked data for next refresh
        Instant later = now.plusSeconds(30);
        CurrencyRateDTO updatedRate = new CurrencyRateDTO("USD/EUR", 0.86, later, false);
        CurrencyRateDTO updatedRate2 = new CurrencyRateDTO("USD/JPY", 110.30, later, false);
        mockCurrencyRatesResponse(List.of(updatedRate, updatedRate2));

        // Wait for the configured interval + buffer (assume 20 seconds update interval)
        waitForUiAutoRefresh(Duration.ofSeconds(25));

        // Verify UI updated with new rates and timestamps
        verifyCurrencyRateDisplayed("USD/EUR", 0.86, later, false);
        verifyCurrencyRateDisplayed("USD/JPY", 110.30, later, false);
    }

    /**
     * Verifies that the UI displays the currency rate, timestamp, and override flag correctly.
     * 
     * @param currencyPair the currency pair string (e.g. "USD/EUR")
     * @param expectedRate the expected exchange rate
     * @param expectedTimestamp the expected timestamp
     * @param expectedOverrideFlag whether override flag should be shown
     */
    private void verifyCurrencyRateDisplayed(String currencyPair, double expectedRate, Instant expectedTimestamp, boolean expectedOverrideFlag) {
        // Locate the row by currency pair
        List<WebElement> rows = driver.findElements(By.cssSelector("#currency-rates-table tbody tr"));
        WebElement targetRow = null;
        for (WebElement row : rows) {
            WebElement pairCell = row.findElement(By.cssSelector("td.currency-pair"));
            if (pairCell.getText().trim().equals(currencyPair)) {
                targetRow = row;
                break;
            }
        }
        assertThat(targetRow).withFailMessage("Currency pair %s not found in table", currencyPair).isNotNull();

        // Verify rate
        WebElement rateCell = targetRow.findElement(By.cssSelector("td.currency-rate"));
        double actualRate = Double.parseDouble(rateCell.getText().trim());
        assertThat(actualRate).isCloseTo(expectedRate, withinPercentage(0.001));

        // Verify timestamp
        WebElement timestampCell = targetRow.findElement(By.cssSelector("td.currency-timestamp"));
        String timestampText = timestampCell.getText().trim();
        Instant displayedTimestamp = Instant.parse(timestampText);
        assertThat(displayedTimestamp).isEqualTo(expectedTimestamp);

        // Verify override flag
        WebElement overrideCell = targetRow.findElement(By.cssSelector("td.currency-override-flag"));
        boolean isFlagShown = overrideCell.getText().trim().equalsIgnoreCase("Yes") || overrideCell.getAttribute("class").contains("override-flag");
        assertThat(isFlagShown).isEqualTo(expectedOverrideFlag);
    }

    /**
     * Waits for the UI to auto-refresh currency rates table.
     * This method waits until the currency rates table's timestamp cells update to a newer timestamp.
     * 
     * @param timeout maximum duration to wait
     */
    private void waitForUiAutoRefresh(Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);

        // We wait until at least one timestamp cell updates to a newer timestamp
        boolean refreshed = wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                List<WebElement> timestampCells = d.findElements(By.cssSelector("#currency-rates-table tbody tr td.currency-timestamp"));
                Instant now = Instant.now();
                for (WebElement cell : timestampCells) {
                    try {
                        Instant cellTime = Instant.parse(cell.getText().trim());
                        if (cellTime.isAfter(now.minusSeconds(60))) {
                            return true;
                        }
                    } catch (Exception e) {
                        // Ignore parse errors
                    }
                }
                return false;
            }
        });

        assertThat(refreshed).withFailMessage("Currency rates table did not auto-refresh within %d seconds", timeout.getSeconds()).isTrue();
    }

    /**
     * Helper for AssertJ closeTo with percentage tolerance.
     */
    private static org.assertj.core.data.Percentage withinPercentage(double percentage) {
        return org.assertj.core.data.Percentage.withPercentage(percentage * 100);
    }
}
