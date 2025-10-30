/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7229
 * Epic: FPMAPP-7183
 * Generated on: 2025-10-30 17:58:38
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

public class CurrencyFilterTest {

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
    public void testCurrencyFilter() {
        // Step 1: Navigate to transaction history page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyFilter")));

        // Step 2: Use the currency filter to select "INR to JPY"
        WebElement currencyFilter = driver.findElement(By.id("currencyFilter"));
        currencyFilter.click();
        WebElement inrToJpyOption = driver.findElement(By.xpath("//option[text()='INR to JPY']"));
        inrToJpyOption.click();

        // Wait for the results to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionResults")));

        // Assert that only INR to JPY conversions are displayed
        WebElement resultsContainer = driver.findElement(By.id("transactionResults"));
        String resultsText = resultsContainer.getText();

        assertTrue(resultsText.contains("INR to JPY"), "INR to JPY conversions should be displayed");
        assertFalse(resultsText.contains("INR to USD"), "INR to USD conversions should not be displayed");
        assertFalse(resultsText.contains("INR to EUR"), "INR to EUR conversions should not be displayed");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}