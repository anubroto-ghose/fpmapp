/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7877
 * Epic: FPMAPP-7747
 * Generated on: 2025-12-09 16:22:07
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CurrencyExchangeRateTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080"); // URL of the application
    }

    @Test
    public void testHistoricalExchangeRates() {
        // Navigate to the currency exchange rates page
        WebElement exchangeRatesLink = driver.findElement(By.linkText("Historical Exchange Rates"));
        exchangeRatesLink.click();

        // Input the date for which we want to query historical rates
        WebElement dateInput = driver.findElement(By.id("date"));
        dateInput.sendKeys("2023-01-01"); // Example date

        // Submit the request
        WebElement submitButton = driver.findElement(By.id("submit"));
        submitButton.click();

        // Wait for the response and check the results
        WebElement responseElement = driver.findElement(By.id("response"));
        String responseText = responseElement.getText();

        // Assertions to validate the response
        assertTrue(responseText.contains("currency_code"), "Response should contain currency_code");
        assertTrue(responseText.contains("exchange_rate"), "Response should contain exchange_rate");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}