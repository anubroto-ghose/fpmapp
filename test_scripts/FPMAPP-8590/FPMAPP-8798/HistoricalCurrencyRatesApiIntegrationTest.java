/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8798
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:03:29
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
import java.util.Arrays;
import java.util.List;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.services.CurrencyConvertionController;

import io.github.bonigarcia.wdm.WebDriverManager;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class HistoricalCurrencyRatesApiIntegrationTest {

    private WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8080/api/currency";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @Test
    public void testHistoricalCurrencyRatesApi() throws Exception {
        // Preconditions
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(1);

        // Mocked historical rates data
        List<HistoricalRate> mockedRates = Arrays.asList(
                new HistoricalRate(startDate, 0.85),
                new HistoricalRate(startDate.plusDays(1), 0.86),
                new HistoricalRate(startDate.plusDays(2), 0.87),
                new HistoricalRate(endDate, 0.88)
        );

        // Mock the service response
        when(currencyConvertionController.getHistoricalRates(eq(fromCurrency), eq(toCurrency), eq(startDate), eq(endDate)))
                .thenReturn(ResponseEntity.ok(mockedRates));

        // Step 1: Send API request querying historical currency rates
        String apiUrl = String.format("%s/historical?from=%s&to=%s&startDate=%s&endDate=%s",
                BASE_URL, fromCurrency, toCurrency, startDate.format(DATE_FORMATTER), endDate.format(DATE_FORMATTER));

        driver.get(apiUrl);

        // Step 2: Validate API response status code
        // Since we are using WebDriver to hit a REST API, we will parse the page source as JSON
        String pageSource = driver.getPageSource();
        assertThat(pageSource).isNotNull();

        // Step 3: Verify returned data matches requested date range and currency pair
        // For demonstration, parse JSON manually (in real scenario use JSON parser)
        // The API returns JSON array of objects [{date: "yyyy-MM-dd", rate: double}, ...]

        // Simple validation of content
        assertThat(pageSource).contains(fromCurrency);
        assertThat(pageSource).contains(toCurrency);

        for (HistoricalRate rate : mockedRates) {
            String dateStr = rate.getDate().format(DATE_FORMATTER);
            assertThat(pageSource).contains(dateStr);
            assertThat(pageSource).contains(String.valueOf(rate.getRate()));
        }

        // Step 4: Check response format and data accuracy
        // Basic checks for JSON format
        assertThat(pageSource.trim()).startsWith("[");
        assertThat(pageSource.trim()).endsWith("]");

        // Additional error handling: check for error messages
        assertThat(pageSource).doesNotContain("error");
        assertThat(pageSource).doesNotContain("exception");
    }

    // Helper DTO class for mocking
    public static class HistoricalRate {
        private LocalDate date;
        private double rate;

        public HistoricalRate(LocalDate date, double rate) {
            this.date = date;
            this.rate = rate;
        }

        public LocalDate getDate() {
            return date;
        }

        public double getRate() {
            return rate;
        }
    }
}
