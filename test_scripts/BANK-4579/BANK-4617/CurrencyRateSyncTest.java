/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4617
 * Epic: BANK-4579
 * Generated on: 2025-07-30 05:01:47
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

public class CurrencyRateSyncTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");

        // Log in with insufficient permissions
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("user_with_insufficient_permissions");
        passwordField.sendKeys("password");
        loginButton.click();
    }

    @Test
    public void testSyncCurrencyRatesWithInsufficientPermissions() {
        // Navigate to the currency sync section
        driver.get("http://localhost:8080/admin/currency/sync");

        // Click on the 'Sync Currency Rates' button
        WebElement syncButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("syncCurrencyRatesButton")));
        syncButton.click();

        // Verify the error message
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        assertTrue(errorMessage.isDisplayed(), "Error message should be displayed.");
        assertEquals("Insufficient permissions to perform this action.", errorMessage.getText());
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}