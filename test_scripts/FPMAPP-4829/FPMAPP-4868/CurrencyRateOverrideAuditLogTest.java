/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4868
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-18 14:13:57
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

public class CurrencyRateOverrideAuditLogTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/admin/login");

        // Login as finance administrator
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("admin");
        passwordField.sendKeys("adminPassword");
        loginButton.click();
    }

    @Test
    public void testAuditLogEntryForCurrencyRateChange() {
        // Step 1: Navigate to the currency overrides section
        driver.findElement(By.linkText("Currency Overrides")).click();

        // Step 2: Modify an existing currency rate
        WebElement currencyRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[text()='USD']]/td[3]/input")));
        String oldRate = currencyRow.getAttribute("value");
        currencyRow.clear();
        currencyRow.sendKeys("1.25"); // New rate

        // Step 3: Save the changes
        WebElement saveButton = driver.findElement(By.id("saveButton"));
        saveButton.click();

        // Wait for success message
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));

        // Step 4: Access the audit logs
        driver.findElement(By.linkText("Audit Logs")).click();

        // Verify the audit log entry
        WebElement logEntry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[text()='Currency Rate Change']]")));
        assertNotNull(logEntry);

        // Check if the log entry contains the expected details
        String logDetails = logEntry.getText();
        assertTrue(logDetails.contains("USD"));
        assertTrue(logDetails.contains(oldRate));
        assertTrue(logDetails.contains("1.25"));
        assertTrue(logDetails.contains("timestamp")); // Replace with actual timestamp verification if needed
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}