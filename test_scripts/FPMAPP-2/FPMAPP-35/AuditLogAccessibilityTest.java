/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-35
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:45:42
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

public class AuditLogAccessibilityTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        loginAsAuthorizedUser();
    }

    private void loginAsAuthorizedUser() {
        driver.get("http://localhost:8080/login");
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("authorizedUser");
        passwordField.sendKeys("password123");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void testAuditLogAccessibility() {
        driver.get("http://localhost:8080/audit-logs");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditLogTable")));
        WebElement auditLogTable = driver.findElement(By.id("auditLogTable"));

        assertTrue(auditLogTable.isDisplayed(), "Audit log table should be visible to authorized user.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}