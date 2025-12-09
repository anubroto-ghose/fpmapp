/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7883
 * Epic: FPMAPP-7747
 * Generated on: 2025-12-09 16:17:50
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FpmAppTransactionHistoryTest {

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
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.id("loginButton")).click();
    }

    @Test
    public void testNoInrToJpyTransactions() {
        driver.get("http://localhost:8080/transaction-history");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filterDropdown"))).click();
        driver.findElement(By.xpath("//option[text()='INR to JPY']")).click();
        driver.findElement(By.id("filterButton")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noTransactionsMessage")));
        String message = driver.findElement(By.id("noTransactionsMessage")).getText();
        assertEquals("No INR to JPY transactions found", message);

        // Verify no erroneous transactions are shown
        assertTrue(driver.findElements(By.className("transactionRow")).isEmpty(), "There should be no transaction rows displayed.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}