/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4866
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-19 06:12:31
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

public class CurrencyOverrideManagementTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/admin");
        loginAsFinanceAdmin();
    }

    private void loginAsFinanceAdmin() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("admin");
        passwordField.sendKeys("admin_password");
        loginButton.click();

        // Verify login success
        wait.until(ExpectedConditions.titleContains("Admin Dashboard"));
    }

    @Test
    public void testCurrencyOverrideManagement() {
        // Step 1: Navigate to currency overrides section
        driver.findElement(By.linkText("Currency Overrides")).click();

        // Step 2: Enter valid currency code and rate
        WebElement currencyCodeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyCode")));
        WebElement rateField = driver.findElement(By.id("currencyRate"));
        WebElement saveButton = driver.findElement(By.id("saveButton"));

        currencyCodeField.sendKeys("USD");
        rateField.sendKeys("1.25");

        // Step 3: Click on the 'Save' button
        saveButton.click();

        // Expected Results
        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmationMessage")));
        assertEquals("Currency override saved successfully!", confirmationMessage.getText());
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}