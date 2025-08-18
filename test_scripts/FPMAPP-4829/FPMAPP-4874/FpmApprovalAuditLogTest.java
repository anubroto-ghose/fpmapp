/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4874
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-18 14:08:04
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FpmApprovalAuditLogTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void testAuditLogCreationOnApproval() {
        // Step 1: Log in as a Director
        driver.get("http://localhost:8080/login");
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("director@example.com");
        passwordField.sendKeys("password");
        loginButton.click();

        // Step 2: Approve a high-value request
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("highValueRequestButton"))).click();
        WebElement approveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approveButton")));
        approveButton.click();

        // Step 3: Access the audit log
        driver.get("http://localhost:8080/audit-log");

        // Step 4: Verify the entry for the approval action
        String auditLogEntry = driver.findElement(By.id("auditLogEntry")).getText();
        assertTrue(auditLogEntry.contains("director@example.com"));
        assertTrue(auditLogEntry.contains("Approved high-value request"));
        assertTrue(auditLogEntry.contains("timestamp")); // Replace with actual timestamp check
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}