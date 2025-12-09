/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7882
 * Epic: FPMAPP-7747
 * Generated on: 2025-12-09 16:18:35
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FpmAppCurrencyConversionTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/transaction-history");
    }

    @Test
    public void testINRToJPYConversion() {
        // Wait for the transaction history to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

        // Locate the INR to JPY conversion entry
        WebElement conversionEntry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[contains(text(), 'INR to JPY')]]")));

        // Check the date format
        WebElement dateElement = conversionEntry.findElement(By.xpath("td[1]"));
        String dateText = dateElement.getText();
        assertTrue(dateText.matches("\d{2}-\d{2}-\d{4}"), "Date format is incorrect");

        // Check the amount in INR
        WebElement amountInInrElement = conversionEntry.findElement(By.xpath("td[2]"));
        String amountInInrText = amountInInrElement.getText();
        assertEquals("₹5000", amountInInrText, "Amount in INR is incorrect");

        // Check the amount in JPY
        WebElement amountInJpyElement = conversionEntry.findElement(By.xpath("td[3]"));
        String amountInJpyText = amountInJpyElement.getText();
        assertEquals("¥75000", amountInJpyText, "Amount in JPY is incorrect"); // Assuming conversion rate is 15

        // Check the status
        WebElement statusElement = conversionEntry.findElement(By.xpath("td[4]"));
        String statusText = statusElement.getText();
        assertEquals("Completed", statusText, "Status is incorrect");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}