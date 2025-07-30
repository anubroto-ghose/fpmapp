/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4663
 * Epic: BANK-4644
 * Generated on: 2025-07-30 17:00:45
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyRateIntegrationTest {

    private WebDriver driver;

    @Mock
    private CurrencyConvertionController currencyConvertionController;

    @InjectMocks
    @Autowired
    private FpmCommonController fpmCommonController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
    }

    @Test
    public void testFetchRealTimeCurrencyRates() {
        // Mocking the response from the CurrencyConvertionController
        when(currencyConvertionController.fetchRealTimeRates()).thenReturn(new ResponseEntity<>("{" +
                "\"USD\":\"1.00\",\"EUR\":\"0.85\"}", HttpStatus.OK));

        // Navigate to the currency rates page
        driver.get("http://localhost:8080/currency/rates");

        // Trigger the API call to fetch real-time currency rates
        driver.findElement(By.id("fetchRatesButton")).click();

        // Observe the response from the API
        String responseText = driver.findElement(By.id("responseOutput")).getText();

        // Assert the expected results
        assertEquals("{" +
                "\"USD\":\"1.00\",\"EUR\":\"0.85\"}", responseText);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}