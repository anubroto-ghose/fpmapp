/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8816
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:51:57
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.services.CurrencyConvertionController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyHistoricalRatesIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private String baseUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI environments)
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

    @BeforeEach
    public void setup() {
        baseUrl = "http://localhost:" + port;
    }

    /**
     * Mock response DTO for currency historical rate
     */
    public static class CurrencyRateDTO {
        private String currencyCode;
        private double exchangeRate;
        private String rateTimestamp;
        private boolean isHistorical;
        private boolean isOverride;

        public CurrencyRateDTO(String currencyCode, double exchangeRate, String rateTimestamp, boolean isHistorical, boolean isOverride) {
            this.currencyCode = currencyCode;
            this.exchangeRate = exchangeRate;
            this.rateTimestamp = rateTimestamp;
            this.isHistorical = isHistorical;
            this.isOverride = isOverride;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public double getExchangeRate() {
            return exchangeRate;
        }

        public String getRateTimestamp() {
            return rateTimestamp;
        }

        public boolean isHistorical() {
            return isHistorical;
        }

        public boolean isOverride() {
            return isOverride;
        }
    }

    /**
     * Helper method to generate mock historical currency rates for a date range
     */
    private List<CurrencyRateDTO> generateMockRates(LocalDate startDate, LocalDate endDate) {
        List<CurrencyRateDTO> rates = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            // For simplicity, generate one currency rate per day for USD
            rates.add(new CurrencyRateDTO(
                    "USD",
                    1.10 + current.getDayOfMonth() * 0.001, // some varying rate
                    current.atStartOfDay().toString(),
                    true,
                    false
            ));
            current = current.plusDays(1);
        }
        return rates;
    }

    @Test
    public void testValidDateRangeReturnsHistoricalRates() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);

        List<CurrencyRateDTO> mockRates = generateMockRates(startDate, endDate);

        // Mock the service call
        when(currencyConvertionController.getHistoricalRates(eq(startDate.toString()), eq(endDate.toString())))
                .thenReturn(ResponseEntity.ok(mockRates));

        // Use Selenium to simulate user interaction with the UI or API test page
        // For this example, assume a simple UI page exists at /currency/historical-test
        driver.get(baseUrl + "/currency/historical-test?start_date=2026-01-01&end_date=2026-01-31");

        // Wait for results to load (simple implicit wait)
        Thread.sleep(1000);

        // Validate page title or header
        WebElement header = driver.findElement(By.tagName("h1"));
        assertThat(header.getText()).contains("Historical Currency Rates");

        // Validate table rows for each date
        List<WebElement> rows = driver.findElements(By.cssSelector("table#ratesTable tbody tr"));
        assertThat(rows.size()).isEqualTo(mockRates.size());

        for (int i = 0; i < rows.size(); i++) {
            WebElement row = rows.get(i);
            CurrencyRateDTO expected = mockRates.get(i);

            List<WebElement> cols = row.findElements(By.tagName("td"));
            assertThat(cols.get(0).getText()).isEqualTo(expected.getCurrencyCode());
            assertThat(Double.parseDouble(cols.get(1).getText())).isEqualTo(expected.getExchangeRate());
            assertThat(cols.get(2).getText()).isEqualTo(expected.getRateTimestamp());
            assertThat(Boolean.parseBoolean(cols.get(3).getText())).isTrue(); // isHistorical
            assertThat(Boolean.parseBoolean(cols.get(4).getText())).isEqualTo(expected.isOverride());
        }
    }

    @Test
    public void testInvalidDateRangeReturnsBadRequest() throws Exception {
        String invalidStartDate = "2026-02-01";
        String invalidEndDate = "2026-01-01"; // start_date after end_date

        // Mock the service to return 400 Bad Request
        when(currencyConvertionController.getHistoricalRates(eq(invalidStartDate), eq(invalidEndDate)))
                .thenReturn(ResponseEntity.badRequest().body(null));

        // Use Selenium to simulate user interaction with the UI or API test page
        driver.get(baseUrl + "/currency/historical-test?start_date=2026-02-01&end_date=2026-01-01");

        Thread.sleep(1000);

        // Validate error message displayed
        WebElement errorDiv = driver.findElement(By.id("errorMessage"));
        assertThat(errorDiv.getText()).contains("Invalid date range");
    }
}
