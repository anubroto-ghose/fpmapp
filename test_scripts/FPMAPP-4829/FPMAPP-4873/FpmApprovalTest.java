/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4873
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-19 06:04:10
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FpmApprovalTest {

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
    public void testManagerApprovalForMidTierRequest() {
        // Log in as Manager
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("managerUser");
        passwordField.sendKeys("managerPassword");
        loginButton.click();

        // Attempt to approve a mid-tier request
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("midTierRequestButton"))).click();
        WebElement approveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approveButton")));
        approveButton.click();

        // Check the approval status
        WebElement approvalStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatus")));
        String statusText = approvalStatus.getText();

        // Assertions
        assertEquals("Approved", statusText);
        assertTrue(statusText.contains("Approved"), "The request should be approved successfully.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}