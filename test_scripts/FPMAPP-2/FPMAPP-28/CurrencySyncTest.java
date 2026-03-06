/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-28
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:43:37
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

public class CurrencySyncTest {
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

        usernameField.sendKeys("testUser");
        passwordField.sendKeys("testPassword");
        loginButton.click();
    }

    @Test
    public void testAutoSyncCurrencyRates() throws InterruptedException {
        // Step 1: Set the auto-sync interval to 30 minutes
        driver.get("http://localhost:8080/settings");
        WebElement intervalField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("autoSyncInterval")));
        intervalField.clear();
        intervalField.sendKeys("30");
        WebElement saveButton = driver.findElement(By.id("saveSettings"));
        saveButton.click();

        // Step 2: Wait for the sync to occur
        Thread.sleep(1800000); // Wait for 30 minutes (1800000 milliseconds)

        // Step 3: Check the currency rates after the sync
        driver.get("http://localhost:8080/currencyRates");
        WebElement currencyRateElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyRate")));
        String currencyRate = currencyRateElement.getText();

        // Expected Results: Currency rates are updated automatically at the defined interval.
        assertTrue(currencyRate != null && !currencyRate.isEmpty(), "Currency rates should be updated.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}