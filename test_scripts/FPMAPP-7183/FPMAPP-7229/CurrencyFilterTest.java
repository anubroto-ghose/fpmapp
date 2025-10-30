/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7229
 * Epic: FPMAPP-7183
 * Generated on: 2025-10-30 17:58:43
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
        WebElement currencyFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyFilter")));
        currencyFilter.click();

        // Step 2: Use the currency filter to select "INR to JPY"
        WebElement inrToJpyOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//option[text()='INR to JPY']")));
        inrToJpyOption.click();

        // Apply filter
        WebElement applyFilterButton = driver.findElement(By.id("applyFilterButton"));
        applyFilterButton.click();

        // Verify results
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionList")));
        List<WebElement> transactions = driver.findElements(By.className("transaction-item"));

        for (WebElement transaction : transactions) {
            String transactionText = transaction.getText();
            assertTrue(transactionText.contains("INR to JPY"), "Transaction does not match the filter: " + transactionText);
        }

        // Ensure no other currency pairs are displayed
        assertEquals(transactions.size(), 5, "Expected 5 transactions for INR to JPY"); // Adjust based on expected count
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}