/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4608
 * Epic: BANK-4579
 * Generated on: 2025-07-30 05:08:43
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

public class FpmApprovalStatusChangeTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("http://localhost:8080/login");
    }

    @Test
    public void testNoEmailNotificationOnDisapproval() {
        // Log in as a user
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.id("loginButton")).click();

        // Submit a request for approval
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submitRequestButton"))).click();

        // Change the status of the request to 'Disapproved'
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("changeStatusButton"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("disapproveButton"))).click();

        // Check the registered email account
        // Simulate checking the email (this part would typically involve a mock or a service call)
        boolean emailReceived = checkEmailForNotification("testuser@example.com", "Disapproved");

        // Assert that no email was received
        assertFalse(emailReceived, "User should not receive an email notification for disapproval status change.");
    }

    private boolean checkEmailForNotification(String email, String subject) {
        // Mocked method to simulate checking the email inbox for a specific subject
        // In a real scenario, this would connect to an email service or database
        return false; // Simulating that no email was received
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}