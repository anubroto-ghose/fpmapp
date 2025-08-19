/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4868
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-19 06:10:08
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

public class CurrencyRateOverrideAuditLogTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testAuditLogEntryForCurrencyRateChange() {
        // Step 1: Navigate to the currency overrides section in the admin UI.
        driver.get("http://localhost:8080/admin/currency/overrides");

        // Step 2: Modify an existing currency rate.
        WebElement currencyRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[contains(., 'USD')]")));
        WebElement editButton = currencyRow.findElement(By.xpath(".//button[contains(text(), 'Edit')]"));
        editButton.click();

        WebElement rateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyRate")));
        String oldRate = rateInput.getAttribute("value");
        String newRate = String.valueOf(Double.parseDouble(oldRate) * 1.1); // Increase by 10%
        rateInput.clear();
        rateInput.sendKeys(newRate);

        WebElement saveButton = driver.findElement(By.id("saveButton"));
        saveButton.click();

        // Step 3: Save the changes.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));

        // Step 4: Access the audit logs.
        driver.get("http://localhost:8080/admin/audit-logs");
        WebElement auditLogEntry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[contains(., 'USD')]")));

        // Assertions
        assertNotNull(auditLogEntry);
        assertTrue(auditLogEntry.getText().contains("Old Rate: " + oldRate));
        assertTrue(auditLogEntry.getText().contains("New Rate: " + newRate));
        assertTrue(auditLogEntry.getText().contains("Currency Code: USD"));
        assertTrue(auditLogEntry.getText().contains("Timestamp:"));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}