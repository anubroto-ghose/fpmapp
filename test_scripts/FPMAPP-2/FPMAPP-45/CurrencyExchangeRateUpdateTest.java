/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-45
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:30:49
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyExchangeRateUpdateTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private WebApplicationContext context;

    @Mock
    private CurrencyConvertionController currencyConvertionController;

    @InjectMocks
    private FpmForecastController fpmForecastController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080");
    }

    @Test
    public void testCurrencyExchangeRateUpdate() throws InterruptedException {
        // Mocking the service response
        when(currencyConvertionController.getLatestExchangeRates()).thenReturn(getMockedExchangeRates());

        // Wait for the defined interval (simulate waiting)
        Thread.sleep(60000); // Wait for 1 minute (defined interval)

        // Check the database for updated exchange rates
        driver.findElement(By.id("fetchRatesButton")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("exchangeRatesTable")));

        // Validate the updated exchange rates
        String updatedRate = driver.findElement(By.id("rateUSD"))
                .getText();
        assertTrue(updatedRate.contains("1.25"), "Exchange rate should be updated to 1.25");
    }

    private String getMockedExchangeRates() {
        return "{\"USD\": 1.25, \"EUR\": 0.85}";
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}