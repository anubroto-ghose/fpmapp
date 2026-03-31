/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-9054
 * Epic: FPMAPP-9032
 * Generated on: 2026-03-31 16:46:53
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZoneOffset;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test for currency exchange rate API using Selenium WebDriver and Spring Boot context.
 * 
 * This test mocks CurrencyConvertionController service to simulate API responses.
 * It verifies latest and historical exchange rate queries and error handling for invalid timestamps.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyExchangeRateIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private String baseUrl;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
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
        baseUrl = "http://localhost:" + port + "/api/fpm/currency/rates";
    }

    /**
     * Helper method to mock the CurrencyConvertionController response.
     * 
     * @param currencyPair currency pair string like "USD_EUR"
     * @param timestamp ISO-8601 timestamp string or null for latest
     * @param rate exchange rate to return
     * @param status HTTP status to return
     */
    private void mockCurrencyRateResponse(String currencyPair, String timestamp, Double rate, HttpStatus status) {
        Map<String, Object> responseBody = new HashMap<>();
        if (rate != null) {
            responseBody.put("currencyPair", currencyPair);
            responseBody.put("timestamp", timestamp != null ? timestamp : ISO_FORMATTER.format(Instant.now()));
            responseBody.put("exchangeRate", rate);
        } else {
            responseBody.put("error", "No data available for the given parameters.");
        }

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseBody, status);

        when(currencyConvertionController.getCurrencyRate(eq(currencyPair), eq(timestamp))).thenReturn(responseEntity);
    }

    @Test
    public void testLatestExchangeRate() {
        String currencyPair = "USD_EUR";
        Double latestRate = 0.85;

        // Mock service to return latest rate when timestamp is null
        mockCurrencyRateResponse(currencyPair, null, latestRate, HttpStatus.OK);

        // Build URL without timestamp
        String url = baseUrl + "?currency_pair=" + currencyPair;

        driver.get(url);

        // Wait and parse response
        WebElement preElement = driver.findElement(By.tagName("pre"));
        String jsonResponse = preElement.getText();

        assertThat(jsonResponse).contains("exchangeRate");
        assertThat(jsonResponse).contains(currencyPair);
        assertThat(jsonResponse).contains(latestRate.toString());
    }

    @Test
    public void testHistoricalExchangeRate() {
        String currencyPair = "USD_EUR";
        Instant historicalInstant = Instant.parse("2023-01-01T00:00:00Z");
        String timestamp = ISO_FORMATTER.format(historicalInstant);
        Double historicalRate = 0.82;

        // Mock service to return historical rate
        mockCurrencyRateResponse(currencyPair, timestamp, historicalRate, HttpStatus.OK);

        String url = baseUrl + "?currency_pair=" + currencyPair + "&timestamp=" + timestamp;

        driver.get(url);

        WebElement preElement = driver.findElement(By.tagName("pre"));
        String jsonResponse = preElement.getText();

        assertThat(jsonResponse).contains("exchangeRate");
        assertThat(jsonResponse).contains(currencyPair);
        assertThat(jsonResponse).contains(historicalRate.toString());
        assertThat(jsonResponse).contains(timestamp);
    }

    @Test
    public void testInvalidFutureTimestamp() {
        String currencyPair = "USD_EUR";
        Instant futureInstant = Instant.now().plusSeconds(3600 * 24 * 365); // 1 year in future
        String futureTimestamp = ISO_FORMATTER.format(futureInstant);

        // Mock service to return error for future timestamp
        mockCurrencyRateResponse(currencyPair, futureTimestamp, null, HttpStatus.BAD_REQUEST);

        String url = baseUrl + "?currency_pair=" + currencyPair + "&timestamp=" + futureTimestamp;

        driver.get(url);

        WebElement preElement = driver.findElement(By.tagName("pre"));
        String jsonResponse = preElement.getText();

        assertThat(jsonResponse).contains("error");
        assertThat(jsonResponse).contains("No data available");
    }

    @Test
    public void testResponseTimeWithinLimits() {
        String currencyPair = "USD_EUR";
        Double latestRate = 0.85;

        mockCurrencyRateResponse(currencyPair, null, latestRate, HttpStatus.OK);

        String url = baseUrl + "?currency_pair=" + currencyPair;

        long startTime = System.currentTimeMillis();
        driver.get(url);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        // Assert response time is less than 2 seconds (2000 ms)
        assertThat(duration).isLessThan(2000);
    }
}
