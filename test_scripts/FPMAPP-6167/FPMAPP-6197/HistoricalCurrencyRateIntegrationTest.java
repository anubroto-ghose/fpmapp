/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6197
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:58:04
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import com.webapp.fpmapp.service.CurrencyConvertionController;
import com.webapp.fpmapp.dto.CurrencyRateDto;
import com.webapp.fpmapp.entity.User;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

/**
 * Integration test for historical currency rate retrieval.
 * Uses embedded web environment and mocks services.
 * Selenium WebDriver used to simulate Browser API call UI.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HistoricalCurrencyRateIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private static final String BASE_PATH = "/currency/rates/historical";

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("FPMAPP-6170-TC02: Retrieve historical currency rates with valid date range")
    public void testRetrieveHistoricalCurrencyRates() throws Exception {
        // Arrange - Prepare mock historical currency rate data for USD from 2025-01-01 to 2025-01-10
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 10);

        List<CurrencyRateDto> mockRates = new ArrayList<>();
        double baseRate = 1.0;
        for (int i = 0; !startDate.plusDays(i).isAfter(endDate); i++) {
            LocalDate date = startDate.plusDays(i);
            // Simulate some rate variation, e.g. 1.0 + i*0.01
            mockRates.add(new CurrencyRateDto(date.format(formatter), baseRate + i * 0.01));
        }

        when(currencyConvertionController.getHistoricalRates(eq("USD"), eq(startDate.format(formatter)), eq(endDate.format(formatter))))
            .thenReturn(mockRates);

        // Build the full URL
        String url = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(port)
            .path(BASE_PATH)
            .queryParam("currencyCode", "USD")
            .queryParam("startDate", startDate.format(formatter))
            .queryParam("endDate", endDate.format(formatter))
            .build()
            .toUriString();

        // Act - Use Selenium WebDriver to load the URL and extract data
        driver.get(url);

        // Wait for response to be loaded inside <pre> tag or body
        WebDriverWait wait = new WebDriverWait(driver, 10);

        WebElement body = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        String responseText = body.getText();
        // The API likely returns JSON directly

        // Parse JSON response
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        // Expect JSON array
        CurrencyRateDto[] returnedRates = objectMapper.readValue(responseText, CurrencyRateDto[].class);

        // Assert
        assertThat(returnedRates, is(notNullValue()));
        assertThat(returnedRates.length, is(mockRates.size()));

        // Assert chronological order and matching rates
        for (int i = 0; i < returnedRates.length; i++) {
            CurrencyRateDto expected = mockRates.get(i);
            CurrencyRateDto actual = returnedRates[i];

            assertThat(actual.getDate(), is(expected.getDate()));
            assertThat(actual.getRate(), closeTo(expected.getRate(), 0.0001));
        }

        // Also assert response status 200 (confirm via TestRestTemplate for example)
        ResponseEntity<CurrencyRateDto[]> response = restTemplate.getForEntity(url, CurrencyRateDto[].class);
        assertThat(response.getStatusCodeValue(), is(200));

        // Performance Note: For demo purposes, test duration check - should complete quickly
        // (Omitted timing code as this is a sample; production tests might add timing assertions)
    }
}
