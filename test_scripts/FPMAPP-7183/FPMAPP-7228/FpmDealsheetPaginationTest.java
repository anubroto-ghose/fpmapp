/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7228
 * Epic: FPMAPP-7183
 * Generated on: 2025-10-30 17:59:01
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

import java.util.List;

public class FpmDealsheetPaginationTest {

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
        // Wait for the transaction history to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

        // Get the initial list of transactions
        List<WebElement> transactions = driver.findElements(By.cssSelector(".transaction-row"));
        assertFalse(transactions.isEmpty(), "Transaction list should not be empty");

        // Scroll to the bottom or click on pagination controls
        WebElement nextPageButton = driver.findElement(By.id("nextPageButton"));
        nextPageButton.click();

        // Wait for the new transactions to load
        wait.until(ExpectedConditions.stalenessOf(transactions.get(0)));

        // Get the new list of transactions
        transactions = driver.findElements(By.cssSelector(".transaction-row"));
        assertFalse(transactions.isEmpty(), "Transaction list should not be empty on the next page");

        // Verify that the transactions are still INR to JPY
        for (WebElement transaction : transactions) {
            String currency = transaction.findElement(By.className("currency")).getText();
            assertEquals("INR to JPY", currency, "Transaction currency should be INR to JPY");
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}