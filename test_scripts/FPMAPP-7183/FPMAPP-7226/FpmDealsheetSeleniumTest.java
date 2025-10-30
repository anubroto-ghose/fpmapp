/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7226
 * Epic: FPMAPP-7183
 * Generated on: 2025-10-30 17:59:57
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
        // Locate the INR to JPY conversion entry
        WebElement conversionEntry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[contains(text(), 'INR to JPY')]]")));

        // Check Date format
        WebElement dateElement = conversionEntry.findElement(By.xpath("td[1]"));
        String dateText = dateElement.getText();
        assertTrue(dateText.matches("\d{2}-\d{2}-\d{4}"), "Date format is incorrect");

        // Check Amount in INR
        WebElement amountInRupees = conversionEntry.findElement(By.xpath("td[2]"));
        String amountText = amountInRupees.getText();
        assertTrue(amountText.startsWith("₹"), "Amount in INR is not formatted correctly");

        // Check Amount in JPY
        WebElement amountInYen = conversionEntry.findElement(By.xpath("td[3]"));
        String yenText = amountInYen.getText();
        assertNotNull(yenText, "Amount in JPY should not be null");

        // Check Status
        WebElement statusElement = conversionEntry.findElement(By.xpath("td[4]"));
        String statusText = statusElement.getText();
        assertEquals("Completed", statusText, "Status is not displayed correctly");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}