/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4863
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-18 14:18:34
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
import static org.junit.jupiter.api.Assertions.*;

public class FpmStatusTrackingTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");

        // Log in as a requestor
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("requestor_username");
        passwordField.sendKeys("requestor_password");
        loginButton.click();
    }

    @Test
    public void testStatusTrackingVisibility() {
        // Step 1: Navigate to the submissions page
        driver.get("http://localhost:8080/submissions");

        // Step 2: Locate the submitted request
        WebElement submittedRequest = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='request' and contains(text(), 'Your Submitted Request Title')]")));
        assertNotNull(submittedRequest, "Submitted request should be visible");

        // Step 3: Check the status tracking section
        WebElement statusTrackingSection = submittedRequest.findElement(By.className("status-tracking"));
        assertTrue(statusTrackingSection.isDisplayed(), "Status tracking section should be visible");

        // Verify the current status
        String currentStatus = statusTrackingSection.getText();
        assertNotNull(currentStatus, "Current status should not be null");
        assertFalse(currentStatus.isEmpty(), "Current status should not be empty");
        System.out.println("Current Status: " + currentStatus);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}