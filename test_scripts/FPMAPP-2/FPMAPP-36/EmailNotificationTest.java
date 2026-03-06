/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-36
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:45:59
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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailNotificationTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void testEmailNotificationUponRequestSubmission() {
        // Step 1: Log in as a user
        driver.get("http://localhost:8080/login");
        driver.findElement(By.id("username")).sendKeys("approver@example.com");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.id("loginButton")).click();

        // Step 2: Submit an approval request
        driver.get("http://localhost:8080/approval-request");
        driver.findElement(By.id("requestDetails")).sendKeys("Request for approval");
        driver.findElement(By.id("submitRequestButton")).click();

        // Step 3: Check email inbox (mocked for testing)
        // In a real scenario, you would integrate with an email testing service or mock the email sending.
        // Here we will simulate the check by waiting for a notification on the UI.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notificationMessage")));

        // Step 4: Verify that an email notification has been received
        String notificationMessage = driver.findElement(By.id("notificationMessage")).getText();
        assertTrue(notificationMessage.contains("New approval request submitted"), "Email notification not received");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}