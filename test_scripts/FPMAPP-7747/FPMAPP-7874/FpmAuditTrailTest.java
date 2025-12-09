/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7874
 * Epic: FPMAPP-7747
 * Generated on: 2025-12-09 16:24:25
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

import java.time.Duration;

public class FpmAuditTrailTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:8080/login");
    }

    @Test
    public void testAuditTrailLogging() {
        // Step 1: Log in as auditor
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("auditorUser");
        passwordField.sendKeys("auditorPassword");
        loginButton.click();

        // Step 2: Submit a request
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submitRequestButton"))).click();

        // Step 3: Make changes to the request after submission
        WebElement editRequestButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editRequestButton")));
        editRequestButton.click();

        WebElement requestDetailsField = driver.findElement(By.id("requestDetails"));
        requestDetailsField.clear();
        requestDetailsField.sendKeys("Updated request details");
        driver.findElement(By.id("saveChangesButton")).click();

        // Step 4: Access the audit trail logs
        WebElement auditTrailButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailButton")));
        auditTrailButton.click();

        // Verify that the changes are logged with user details and timestamps
        WebElement auditLogEntry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Updated request details')]")));
        assertNotNull(auditLogEntry, "Audit log entry should be present");

        String logDetails = auditLogEntry.getText();
        assertTrue(logDetails.contains("auditorUser"), "Log should contain the user details");
        assertTrue(logDetails.contains("timestamp"), "Log should contain the timestamp");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}