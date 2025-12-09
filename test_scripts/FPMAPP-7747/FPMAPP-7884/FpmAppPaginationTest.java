/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7884
 * Epic: FPMAPP-7747
 * Generated on: 2025-12-09 16:17:13
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

public class FpmAppPaginationTest {

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
        List<WebElement> initialTransactions = driver.findElements(By.cssSelector(".transaction-row.inr-to-jpy"));
        assertFalse(initialTransactions.isEmpty(), "Initial transactions list should not be empty");

        // Click on the pagination control to go to the next page
        WebElement nextPageButton = driver.findElement(By.cssSelector(".pagination-next"));
        nextPageButton.click();

        // Wait for the new transactions to load
        wait.until(ExpectedConditions.stalenessOf(initialTransactions.get(0)));

        // Get the new list of transactions
        List<WebElement> newTransactions = driver.findElements(By.cssSelector(".transaction-row.inr-to-jpy"));
        assertFalse(newTransactions.isEmpty(), "New transactions list should not be empty after pagination");

        // Verify that the user can navigate back to the previous page
        WebElement previousPageButton = driver.findElement(By.cssSelector(".pagination-prev"));
        previousPageButton.click();

        // Wait for the previous transactions to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

        // Verify that the initial transactions are still present
        List<WebElement> returnedTransactions = driver.findElements(By.cssSelector(".transaction-row.inr-to-jpy"));
        assertEquals(initialTransactions.size(), returnedTransactions.size(), "Returned transactions should match the initial transactions count");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}