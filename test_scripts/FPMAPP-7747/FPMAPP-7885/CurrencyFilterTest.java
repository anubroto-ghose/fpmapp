/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7885
 * Epic: FPMAPP-7747
 * Generated on: 2025-12-09 16:16:21
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
        WebElement option = driver.findElement(By.xpath("//option[text()='INR to JPY']"));
        option.click();

        // Wait for the results to update
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionList")));

        // Verify that only INR to JPY conversions are displayed
        List<WebElement> transactions = driver.findElements(By.cssSelector("#transactionList .transaction-item"));
        for (WebElement transaction : transactions) {
            String text = transaction.getText();
            assertTrue(text.contains("INR to JPY"), "Transaction does not match the selected currency filter: " + text);
        }

        // Verify that no other currency pairs are displayed
        assertFalse(transactions.stream().anyMatch(t -> t.getText().contains("INR to USD")), "INR to USD should not be displayed");
        assertFalse(transactions.stream().anyMatch(t -> t.getText().contains("USD to JPY")), "USD to JPY should not be displayed");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}