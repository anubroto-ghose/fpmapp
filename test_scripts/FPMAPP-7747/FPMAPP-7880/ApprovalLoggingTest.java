/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7880
 * Epic: FPMAPP-7747
 * Generated on: 2025-12-09 16:19:59
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

public class ApprovalLoggingTest {

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
    public void testApprovalLogging() {
        // Log in as a director or manager
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("manager@example.com");
        passwordField.sendKeys("password123");
        loginButton.click();

        // Approve a request
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("approveRequestButton")));
        approveButton.click();

        // Check the approval logs
        driver.get("http://localhost:8080/logs");
        WebElement logEntry = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[contains(text(), 'Approved')]]")));

        // Validate the log entry
        assertNotNull(logEntry, "Log entry should exist");
        String logDetails = logEntry.getText();
        assertTrue(logDetails.contains("manager@example.com"), "Log should contain the approving user's email");
        assertTrue(logDetails.contains("Approved"), "Log should indicate approval action");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}