/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4662
 * Epic: BANK-4644
 * Generated on: 2025-07-30 17:01:02
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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrencyOverrideTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/admin");
        loginAsAdmin();
    }

    @Test
    public void testManualCurrencyOverride() {
        // Step 1: Perform a manual override of a currency rate
        WebElement currencyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyCode")));
        currencyInput.sendKeys("USD");

        WebElement rateInput = driver.findElement(By.id("newRate"));
        rateInput.sendKeys("1.25");

        WebElement overrideButton = driver.findElement(By.id("overrideButton"));
        overrideButton.click();

        // Step 2: Check the system logs for the override entry
        driver.get("http://localhost:8080/admin/logs");
        WebElement logEntry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[contains(., 'USD') and contains(., '1.25')]")));

        // Assert that the log entry contains the correct information
        assertTrue(logEntry.isDisplayed(), "Log entry for currency override not found.");
    }

    private void loginAsAdmin() {
        WebElement usernameInput = driver.findElement(By.id("username"));
        usernameInput.sendKeys("admin");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys("adminPass");

        WebElement loginButton = driver.findElement(By.id("loginButton"));
        loginButton.click();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}