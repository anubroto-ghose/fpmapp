/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-34
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:45:27
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

public class FpmAuditTrailTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
    }

    @Test
    public void testAuditTrailLoggingOnRejection() {
        // Step 1: Log in as an authorized user
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("authorizedUser");
        passwordField.sendKeys("password123");
        loginButton.click();

        // Step 2: Reject a request
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("requestsPage")));
        WebElement rejectButton = driver.findElement(By.id("rejectRequestButton"));
        rejectButton.click();

        // Confirm rejection
        WebElement confirmButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmRejectButton")));
        confirmButton.click();

        // Step 3: Check the audit log for the rejection entry
        driver.get("http://localhost:8080/audit-log");
        WebElement auditLogEntry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[contains(text(), 'Rejection')]]")));

        assertNotNull(auditLogEntry, "Audit log entry for rejection should exist.");
        assertTrue(auditLogEntry.getText().contains("authorizedUser"), "Audit log should contain user details.");
        assertTrue(auditLogEntry.getText().contains("Rejection"), "Audit log should indicate rejection action.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}