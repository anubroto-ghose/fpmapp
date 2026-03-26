/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8798
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:34:49
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.services.CurrencyConvertionController;

import reactor.core.publisher.Mono;

/**
 * Integration test for validating API access to historical currency rates.
 * 
 * Uses Spring Boot test context and Selenium WebDriver to simulate API calls and validate responses.
 * Mocks CurrencyConvertionController service to provide controlled test data.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class HistoricalCurrencyRatesApiIntegrationTest {

    private static WebDriver driver;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI environments)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
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
        // Mock historical currency rates response
        List<Map<String, Object>> mockRates = Arrays.asList(
            Map.of("date", "2026-03-20", "currencyPair", "USD/EUR", "rate", 0.92),
            Map.of("date", "2026-03-21", "currencyPair", "USD/EUR", "rate", 0.93),
            Map.of("date", "2026-03-22", "currencyPair", "USD/EUR", "rate", 0.91)
        );

        when(currencyConvertionController.getHistoricalRates(eq("USD/EUR"), eq("2026-03-20"), eq("2026-03-22")))
            .thenReturn(mockRates);
    }

    @Test
    public void testHistoricalCurrencyRatesApi() {
        // Construct the API URL with query parameters
        String currencyPair = "USD/EUR";
        String startDate = "2026-03-20";
        String endDate = "2026-03-22";

        String apiUrl = String.format("%s/api/currency/historical?currencyPair=%s&startDate=%s&endDate=%s",
                BASE_URL, currencyPair, startDate, endDate);

        // Use Selenium WebDriver to send GET request and capture response
        // Since Selenium is primarily for UI, we simulate by navigating to a test page that calls the API
        // For demonstration, we will use WebTestClient for direct API call validation

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/currency/historical")
                .queryParam("currencyPair", currencyPair)
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Map.class)
            .value(responseList -> {
                // Assert response size matches mocked data
                assertThat(responseList).hasSize(3);

                // Assert each entry matches requested currency pair and date range
                for (Map<String, Object> entry : responseList) {
                    String dateStr = (String) entry.get("date");
                    String pair = (String) entry.get("currencyPair");
                    Double rate = (Double) entry.get("rate");

                    assertThat(pair).isEqualTo(currencyPair);

                    LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                    LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
                    LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);

                    assertThat(date).isBetween(start, end);
                    assertThat(rate).isNotNull();
                    assertThat(rate).isGreaterThan(0);
                }
            });
    }
}