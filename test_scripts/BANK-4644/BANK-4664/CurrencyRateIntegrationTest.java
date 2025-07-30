/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4664
 * Epic: BANK-4644
 * Generated on: 2025-07-30 17:00:27
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CurrencyRateIntegrationTest {

    private WebDriver driver;

    @MockBean
    private RestTemplate restTemplate;

    @InjectMocks
    @Autowired
    private CurrencyConvertionController currencyConvertionController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080"); // Adjust the URL as needed
    }

    @Test
    public void testInvalidApiKeyError() {
        // Mocking the response for invalid API key
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(new ResponseEntity<>("Invalid API Key", HttpStatus.UNAUTHORIZED));

        // Trigger the API call to fetch real-time currency rates
        driver.findElement(By.id("fetchCurrencyRatesButton")).click(); // Assuming there's a button to fetch rates

        // Observe the response from the API
        String errorMessage = driver.findElement(By.id("errorMessage")).getText(); // Assuming there's an element to display error messages

        // Assert the expected result
        assertEquals("Invalid API Key", errorMessage);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}