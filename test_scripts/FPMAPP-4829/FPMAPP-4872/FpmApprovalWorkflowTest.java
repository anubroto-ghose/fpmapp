/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4872
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-18 14:09:59
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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FpmApprovalWorkflowTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testDirectorApproval() {
        // Step 1: Log in as a Director
        driver.get("http://localhost:8080/login");
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("director@example.com");
        passwordField.sendKeys("password123");
        loginButton.click();

        // Step 2: Attempt to approve a high-value request
        driver.get("http://localhost:8080/approval-requests");
        WebElement approveButton = driver.findElement(By.id("approveRequest1"));
        approveButton.click();

        // Step 3: Check the approval status after the action
        WebElement statusElement = driver.findElement(By.id("requestStatus1"));
        String status = statusElement.getText();

        // Expected Results
        assertEquals("Approved", status, "The request should be approved successfully.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}