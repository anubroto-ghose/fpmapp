/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7881
 * Epic: FPMAPP-7747
 * Generated on: 2025-12-09 16:19:13
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

public class TransactionHistoryTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
        login();
    }

    private void login() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("testuser");
        passwordField.sendKeys("password");
        loginButton.click();
    }

    @Test
    public void testTransactionHistoryForINRtoJPY() {
        driver.get("http://localhost:8080/transaction-history");

        // Assuming there is a filter for currency pairs
        WebElement currencyFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyFilter")));
        currencyFilter.sendKeys("INR to JPY");

        // Wait for the transactions to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionList")));

        // Verify the transaction entries
        WebElement transactionList = driver.findElement(By.id("transactionList"));
        assertTrue(transactionList.isDisplayed(), "Transaction list should be displayed");

        // Check for specific transaction details
        WebElement firstTransaction = transactionList.findElement(By.xpath(".//tr[1]"));
        String date = firstTransaction.findElement(By.xpath(".//td[1]")).getText();
        String amountInINR = firstTransaction.findElement(By.xpath(".//td[2]")).getText();
        String amountInJPY = firstTransaction.findElement(By.xpath(".//td[3]")).getText();
        String status = firstTransaction.findElement(By.xpath(".//td[4]")).getText();

        assertNotNull(date, "Date should not be null");
        assertNotNull(amountInINR, "Amount in INR should not be null");
        assertNotNull(amountInJPY, "Amount in JPY should not be null");
        assertTrue(status.equals("Completed") || status.equals("Failed"), "Status should be either Completed or Failed");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}