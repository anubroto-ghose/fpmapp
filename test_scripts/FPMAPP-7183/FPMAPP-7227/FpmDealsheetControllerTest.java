/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7227
 * Epic: FPMAPP-7183
 * Generated on: 2025-10-30 17:59:22
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FpmDealsheetControllerTest {

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
    public void testNoInrToJpyTransactions() {
        driver.get("http://localhost:8080/transaction-history");

        WebElement filterDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyFilter")));
        filterDropdown.sendKeys("INR to JPY");

        WebElement filterButton = driver.findElement(By.id("filterButton"));
        filterButton.click();

        WebElement messageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noTransactionsMessage")));
        String message = messageElement.getText();

        assertEquals("No INR to JPY transactions found", message);

        // Verify no erroneous transactions are shown
        WebElement transactionsTable = driver.findElement(By.id("transactionsTable"));
        assertTrue(transactionsTable.getText().isEmpty(), "Transactions table should be empty");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}