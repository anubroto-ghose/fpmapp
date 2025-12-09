/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7876
 * Epic: FPMAPP-7747
 * Generated on: 2025-12-09 16:22:59
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CurrencyExchangeRateSyncTest {

    private WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080"); // URL of the application
    }

    @Test
    public void testAutoSyncExchangeRates() throws InterruptedException {
        // Mocking the response from the currency conversion service
        when(currencyConvertionController.getLatestExchangeRates()).thenReturn(getMockedExchangeRates());

        // Wait for 10 minutes (600 seconds) to simulate the auto-sync interval
        Thread.sleep(600000);

        // Check the CurrencyExchangeRates table for updated rates
        driver.navigate().refresh();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyExchangeRatesTable")));

        // Verify that the table reflects the latest exchange rates
        String updatedRates = driver.findElement(By.id("currencyExchangeRatesTable")).getText();
        assertTrue(updatedRates.contains("Latest Rate"), "The exchange rates table should reflect the latest rates.");
    }

    private String getMockedExchangeRates() {
        // Mocked response for exchange rates
        return "USD: 1.0, EUR: 0.85, GBP: 0.75";
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}