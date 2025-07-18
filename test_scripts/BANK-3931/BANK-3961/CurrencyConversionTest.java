/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-3961
 * Epic: BANK-3931
 * Generated on: 2025-07-18 13:40:26
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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.RestController;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
public class CurrencyConversionTest {

    private WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @BeforeEach
    public void setUp() {
        // Initialize the WebDriver
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/currency-conversion");
        
        // Mock the service response
        when(currencyConvertionController.convertCurrency(anyString(), anyString(), any())).thenReturn(new CurrencyConversionResponse(82.65, "USD", "INR", "Live Rate", "2025-07-18 08:00 IST"));
    }

    @Test
    public void testRealTimeCurrencyConversion() {
        // Trigger currency rate synchronization job
        WebElement syncButton = driver.findElement(By.id("syncRate"));
        syncButton.click();

        // Perform currency conversion
        WebElement baseCurrencyInput = driver.findElement(By.id("baseCurrency"));
        WebElement targetCurrencyInput = driver.findElement(By.id("targetCurrency"));
        WebElement amountInput = driver.findElement(By.id("amount"));
        WebElement convertButton = driver.findElement(By.id("convert"));

        baseCurrencyInput.sendKeys("USD");
        targetCurrencyInput.sendKeys("INR");
        amountInput.sendKeys("100");
        convertButton.click();

        // Verify the exchange rate used for conversion
        WebElement resultLabel = driver.findElement(By.id("result"));
        String resultText = resultLabel.getText();
        assertEquals("Converted Amount: 8265.00 INR (Rate Used: 1 USD = 82.65 INR as of 2025-07-18 08:00 IST)", resultText);
    }

    @AfterEach
    public void tearDown() {
        // Close the WebDriver
        driver.quit();
    }
}
