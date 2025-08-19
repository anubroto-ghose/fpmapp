/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4874
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-19 06:03:38
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

public class FpmApprovalAuditLogTest {
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
    public void testAuditLogCreationOnApproval() {
        // Step 1: Log in as a Director.
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("director@example.com");
        passwordField.sendKeys("password");
        loginButton.click();

        // Step 2: Approve a high-value request.
        WebElement approvalRequest = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("highValueRequest")));
        approvalRequest.click();
        WebElement approveButton = driver.findElement(By.id("approveButton"));
        approveButton.click();

        // Step 3: Access the audit log.
        WebElement auditLogLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditLogLink")));
        auditLogLink.click();

        // Step 4: Verify the entry for the approval action.
        WebElement auditLogEntry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[contains(text(), 'Approved')]]")));
        assertNotNull(auditLogEntry, "Audit log entry should exist.");
        assertTrue(auditLogEntry.getText().contains("director@example.com"), "Audit log should contain the approver's email.");
        assertTrue(auditLogEntry.getText().contains("Approved"), "Audit log should contain the approval action.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}