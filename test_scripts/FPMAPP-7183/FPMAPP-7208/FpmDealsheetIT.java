/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7208
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:13:54
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

public class FpmDealsheetIT {

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
    public void testPaginationForInrToJpyTransactions() {
        // Assuming user is logged in and has more than one page of INR to JPY transactions
        assertTrue(driver.getPageSource().contains("INR to JPY"));

        // Check the initial set of transactions
        var transactionElements = driver.findElements(By.cssSelector(".transaction-entry"));
        assertFalse(transactionElements.isEmpty(), "Initial page should not be empty");

        // Navigate to the next page
        driver.findElement(By.cssSelector(".pagination-next")).click();

        // Wait for the next page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".transaction-entry")));

        // Check that the next page loads correctly
        var nextPageTransactionElements = driver.findElements(By.cssSelector(".transaction-entry"));
        assertFalse(nextPageTransactionElements.isEmpty(), "Next page should not be empty");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
