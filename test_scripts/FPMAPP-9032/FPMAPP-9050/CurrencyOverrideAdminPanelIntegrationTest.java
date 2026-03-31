/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-9050
 * Epic: FPMAPP-9032
 * Generated on: 2026-03-31 16:50:30
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test for Currency Override Admin Panel UI.
 * 
 * Preconditions:
 * - User logged in as finance admin (mocked login)
 * - Backend service mocked to provide current and historical currency rates
 * 
 * Tests:
 * - Current currency rates display
 * - Historical currency rates display with timestamps
 * - Data correctness and no UI errors
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CurrencyOverrideAdminPanelIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock current currency rates
        Map<String, Double> currentRates = Map.of(
            "USD", 1.0,
            "EUR", 0.85,
            "JPY", 110.0
        );

        // Mock historical currency rates with timestamps
        List<CurrencyRateRecord> historicalRates = Arrays.asList(
            new CurrencyRateRecord("USD", 1.0, LocalDateTime.now().minusDays(1)),
            new CurrencyRateRecord("EUR", 0.84, LocalDateTime.now().minusDays(1)),
            new CurrencyRateRecord("JPY", 109.5, LocalDateTime.now().minusDays(1)),
            new CurrencyRateRecord("USD", 1.0, LocalDateTime.now().minusDays(7)),
            new CurrencyRateRecord("EUR", 0.83, LocalDateTime.now().minusDays(7)),
            new CurrencyRateRecord("JPY", 108.9, LocalDateTime.now().minusDays(7))
        );

        when(currencyConvertionController.getCurrentRates()).thenReturn(currentRates);
        when(currencyConvertionController.getHistoricalRates()).thenReturn(historicalRates);
    }

    @Test
    public void testViewCurrentAndHistoricalCurrencyRates() {
        // Simulate login as finance admin by navigating directly to the panel
        String baseUrl = "http://localhost:" + port + "/currency-override-admin";
        driver.get(baseUrl);

        // Wait for current rates table to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currentRatesTable")));

        // Verify current currency rates displayed
        WebElement currentRatesTable = driver.findElement(By.id("currentRatesTable"));
        assertThat(currentRatesTable).isNotNull();

        // Check each currency and rate
        for (Map.Entry<String, Double> entry : currencyConvertionController.getCurrentRates().entrySet()) {
            String currency = entry.getKey();
            Double expectedRate = entry.getValue();

            List<WebElement> rows = currentRatesTable.findElements(By.tagName("tr"));
            boolean found = false;
            for (WebElement row : rows) {
                List<WebElement> cols = row.findElements(By.tagName("td"));
                if (cols.size() >= 2) {
                    String currencyText = cols.get(0).getText().trim();
                    String rateText = cols.get(1).getText().trim();
                    if (currencyText.equals(currency)) {
                        found = true;
                        double actualRate = Double.parseDouble(rateText);
                        assertThat(actualRate).isEqualTo(expectedRate);
                        break;
                    }
                }
            }
            assertThat(found).as("Currency %s should be displayed in current rates", currency).isTrue();
        }

        // Navigate to historical rates section
        WebElement historicalTab = driver.findElement(By.id("historicalRatesTab"));
        historicalTab.click();

        // Wait for historical rates table
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("historicalRatesTable")));

        WebElement historicalRatesTable = driver.findElement(By.id("historicalRatesTable"));
        assertThat(historicalRatesTable).isNotNull();

        // Verify historical rates data
        List<WebElement> histRows = historicalRatesTable.findElements(By.tagName("tr"));
        List<CurrencyRateRecord> expectedHistoricalRates = currencyConvertionController.getHistoricalRates();

        // For each expected record, verify it is displayed
        for (CurrencyRateRecord expectedRecord : expectedHistoricalRates) {
            boolean found = false;
            for (WebElement row : histRows) {
                List<WebElement> cols = row.findElements(By.tagName("td"));
                if (cols.size() >= 3) {
                    String currencyText = cols.get(0).getText().trim();
                    String rateText = cols.get(1).getText().trim();
                    String timestampText = cols.get(2).getText().trim();

                    if (currencyText.equals(expectedRecord.getCurrency())) {
                        double actualRate = Double.parseDouble(rateText);
                        LocalDateTime actualTimestamp = LocalDateTime.parse(timestampText, formatter);

                        if (Double.compare(actualRate, expectedRecord.getRate()) == 0 &&
                            actualTimestamp.equals(expectedRecord.getTimestamp())) {
                            found = true;
                            break;
                        }
                    }
                }
            }
            assertThat(found).as("Historical rate record for %s at %s should be displayed",
                    expectedRecord.getCurrency(), expectedRecord.getTimestamp().format(formatter)).isTrue();
        }

        // Verify no error messages
        List<WebElement> errorElements = driver.findElements(By.className("error-message"));
        assertThat(errorElements).isEmpty();
    }

    /**
     * Helper DTO for historical currency rate record.
     */
    public static class CurrencyRateRecord {
        private String currency;
        private double rate;
        private LocalDateTime timestamp;

        public CurrencyRateRecord(String currency, double rate, LocalDateTime timestamp) {
            this.currency = currency;
            this.rate = rate;
            this.timestamp = timestamp;
        }

        public String getCurrency() {
            return currency;
        }

        public double getRate() {
            return rate;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
