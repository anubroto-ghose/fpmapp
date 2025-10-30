/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7226
 * Epic: FPMAPP-7183
 * Generated on: 2025-10-30 17:59:47
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
import static org.junit.jupiter.api.Assertions.*;

public class FpmDealsheetSeleniumTest {

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
    public void testINRToJPYConversionEntry() {
        // Wait for the transaction history to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

        // Locate the INR to JPY conversion entry
        String conversionEntryXPath = "//tr[td[contains(text(), 'INR') and contains(text(), 'JPY')]]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(conversionEntryXPath)));

        // Validate the date format
        String date = driver.findElement(By.xpath(conversionEntryXPath + "/td[1]")).getText();
        assertTrue(date.matches("\d{2}-\d{2}-\d{4}"), "Date format is incorrect");

        // Validate the amount in INR
        String amountInINR = driver.findElement(By.xpath(conversionEntryXPath + "/td[2]")).getText();
        assertTrue(amountInINR.matches("^₹\d+(,\d{3})*$"), "Amount in INR is not formatted correctly");

        // Validate the amount in JPY
        String amountInJPY = driver.findElement(By.xpath(conversionEntryXPath + "/td[3]")).getText();
        assertNotNull(amountInJPY, "Amount in JPY should not be null");

        // Validate the status
        String status = driver.findElement(By.xpath(conversionEntryXPath + "/td[4]")).getText();
        assertEquals("Completed", status, "Status is not displayed correctly");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}