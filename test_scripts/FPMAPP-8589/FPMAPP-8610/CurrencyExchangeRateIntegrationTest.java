/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8610
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 08:07:24
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
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
 * Integration test for currency exchange rate API.
 * 
 * Preconditions:
 * - Currency exchange rates have been synchronized and stored with historical versions.
 * - API endpoint GET /fpm/currency/rates is accessible.
 * 
 * This test uses Selenium WebDriver to simulate user interaction with the API endpoint.
 * It mocks the CurrencyConvertionController service to provide controlled responses.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CurrencyExchangeRateIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private String baseUrl;

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
        baseUrl = "http://localhost:" + port + "/fpm/currency/rates";
    }

    /**
     * Test fetching latest exchange rate without timestamp.
     */
    @Test
    public void testGetLatestExchangeRate() {
        String currencyPair = "USD_EUR";
        double latestRate = 0.85;

        Map<String, Object> latestResponse = new HashMap<>();
        latestResponse.put("currency_pair", currencyPair);
        latestResponse.put("rate", latestRate);
        latestResponse.put("timestamp", Instant.now().toString());

        when(currencyConvertionController.getExchangeRate(eq(currencyPair), eq(null)))
            .thenReturn(ResponseEntity.ok(latestResponse));

        // Build URL with currency_pair param only
        String url = baseUrl + "?currency_pair=" + currencyPair;

        driver.get(url);

        // Wait and parse response body
        WebElement preElement = driver.findElement(By.tagName("pre"));
        String jsonResponse = preElement.getText();

        assertThat(jsonResponse).contains(currencyPair);
        assertThat(jsonResponse).contains(String.valueOf(latestRate));

        // Additional JSON parsing and validation
        // (Assuming response is JSON formatted)
        // Using simple contains checks here for brevity
    }

    /**
     * Test fetching historical exchange rate with valid timestamp.
     */
    @Test
    public void testGetHistoricalExchangeRate() {
        String currencyPair = "USD_EUR";
        Instant historicalTimestamp = Instant.parse("2023-01-01T12:00:00Z");
        double historicalRate = 0.82;

        Map<String, Object> historicalResponse = new HashMap<>();
        historicalResponse.put("currency_pair", currencyPair);
        historicalResponse.put("rate", historicalRate);
        historicalResponse.put("timestamp", historicalTimestamp.toString());

        when(currencyConvertionController.getExchangeRate(eq(currencyPair), eq(historicalTimestamp.toString())))
            .thenReturn(ResponseEntity.ok(historicalResponse));

        // Build URL with currency_pair and timestamp params
        String url = baseUrl + "?currency_pair=" + currencyPair + "&timestamp=" + historicalTimestamp.toString();

        driver.get(url);

        WebElement preElement = driver.findElement(By.tagName("pre"));
        String jsonResponse = preElement.getText();

        assertThat(jsonResponse).contains(currencyPair);
        assertThat(jsonResponse).contains(String.valueOf(historicalRate));
        assertThat(jsonResponse).contains(historicalTimestamp.toString());
    }
}
