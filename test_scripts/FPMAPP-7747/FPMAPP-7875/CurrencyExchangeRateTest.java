/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7875
 * Epic: FPMAPP-7747
 * Generated on: 2025-12-09 16:23:43
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CurrencyExchangeRateTest {

    private WebDriver driver;
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        restTemplate = new RestTemplate();
    }

    @Test
    public void testFetchCurrentExchangeRates() {
        // Step 1: Send a GET request to /api/currency/exchange-rates
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/api/currency/exchange-rates", String.class);

        // Step 2: Observe the response from the API
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected status code 200");

        // Parse the response body
        String responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");

        // Validate the JSON structure
        assertTrue(responseBody.contains("currency_code"), "Response should contain currency_code");
        assertTrue(responseBody.contains("exchange_rate"), "Response should contain exchange_rate");
    }

    @Test
    public void testWebPageCurrencyExchange() {
        driver.get("http://localhost:3000"); // Assuming the frontend is running on port 3000

        // Interact with the web page to fetch exchange rates
        WebElement fetchRatesButton = driver.findElement(By.id("fetch-rates-button"));
        fetchRatesButton.click();

        // Wait for the response to be displayed
        WebElement ratesDisplay = driver.findElement(By.id("rates-display"));
        assertNotNull(ratesDisplay.getText(), "Rates display should not be empty");

        // Validate the displayed rates
        assertTrue(ratesDisplay.getText().contains("currency_code"), "Displayed rates should contain currency_code");
        assertTrue(ratesDisplay.getText().contains("exchange_rate"), "Displayed rates should contain exchange_rate");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}