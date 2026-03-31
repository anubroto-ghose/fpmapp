/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8929
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 15:00:42
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
import java.util.HashMap;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test for Currency Conversion APIs using Selenium WebDriver and Spring Boot context.
 * 
 * Validates querying current and historical currency rates via APIs.
 * 
 * Preconditions:
 * - Currency exchange rates data (current and historical) is mocked and available.
 * - User has valid API access credentials (simulated).
 * 
 * This test mocks the CurrencyConvertionController responses and uses Selenium WebDriver
 * to simulate API calls and verify UI/API response rendering.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyConversionApiIntegrationTest {

    private static WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in system PATH)
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
    public void setupMocks() {
        // Mock current exchange rates response
        Map<String, Double> currentRates = new HashMap<>();
        currentRates.put("USD", 1.0);
        currentRates.put("EUR", 0.85);
        currentRates.put("JPY", 110.0);

        when(currencyConvertionController.getCurrentRates())
            .thenReturn(ResponseEntity.ok(currentRates));

        // Mock historical exchange rates response for date range 2023-01-01 to 2023-01-05
        Map<String, Map<String, Double>> historicalRates = new HashMap<>();
        historicalRates.put("2023-01-01", Map.of("USD", 1.0, "EUR", 0.83, "JPY", 109.5));
        historicalRates.put("2023-01-02", Map.of("USD", 1.0, "EUR", 0.84, "JPY", 109.7));
        historicalRates.put("2023-01-03", Map.of("USD", 1.0, "EUR", 0.85, "JPY", 110.0));
        historicalRates.put("2023-01-04", Map.of("USD", 1.0, "EUR", 0.86, "JPY", 110.2));
        historicalRates.put("2023-01-05", Map.of("USD", 1.0, "EUR", 0.87, "JPY", 110.5));

        when(currencyConvertionController.getHistoricalRates(eq("2023-01-01"), eq("2023-01-05")))
            .thenReturn(ResponseEntity.ok(historicalRates));

        // Mock invalid date range response
        when(currencyConvertionController.getHistoricalRates(eq("2023-01-10"), eq("2023-01-05")))
            .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid date range: startDate must be before endDate")));

        // Mock malformed request response
        when(currencyConvertionController.getHistoricalRates(any(), eq(null)))
            .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Malformed request: endDate is missing")));
    }

    @Test
    public void testQueryCurrentCurrencyRates() {
        driver.get(BASE_URL + "/api/currency/current");

        // Simulate API call via Selenium by fetching page source (assuming API returns JSON text)
        String pageSource = driver.findElement(By.tagName("body")).getText();

        assertThat(pageSource).contains("USD");
        assertThat(pageSource).contains("EUR");
        assertThat(pageSource).contains("JPY");

        // Basic validation of values
        assertThat(pageSource).contains("1.0");
        assertThat(pageSource).contains("0.85");
        assertThat(pageSource).contains("110.0");
    }

    @Test
    public void testQueryHistoricalCurrencyRates_ValidRange() {
        String startDate = "2023-01-01";
        String endDate = "2023-01-05";
        driver.get(BASE_URL + "/api/currency/historical?startDate=" + startDate + "&endDate=" + endDate);

        String pageSource = driver.findElement(By.tagName("body")).getText();

        // Verify all dates and rates are present
        for (int day = 1; day <= 5; day++) {
            String date = String.format("2023-01-%02d", day);
            assertThat(pageSource).contains(date);
        }

        assertThat(pageSource).contains("0.83");
        assertThat(pageSource).contains("0.87");
        assertThat(pageSource).contains("110.5");
    }

    @Test
    public void testQueryHistoricalCurrencyRates_InvalidDateRange() {
        String startDate = "2023-01-10";
        String endDate = "2023-01-05";
        driver.get(BASE_URL + "/api/currency/historical?startDate=" + startDate + "&endDate=" + endDate);

        String pageSource = driver.findElement(By.tagName("body")).getText();

        assertThat(pageSource).contains("Invalid date range");
        assertThat(pageSource).contains("400"); // Assuming error code is shown
    }

    @Test
    public void testQueryHistoricalCurrencyRates_MalformedRequest() {
        String startDate = "2023-01-01";
        // Missing endDate param
        driver.get(BASE_URL + "/api/currency/historical?startDate=" + startDate);

        String pageSource = driver.findElement(By.tagName("body")).getText();

        assertThat(pageSource).contains("Malformed request");
        assertThat(pageSource).contains("400");
    }
}
