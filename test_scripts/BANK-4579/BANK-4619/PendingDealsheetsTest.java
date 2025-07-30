/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4619
 * Epic: BANK-4579
 * Generated on: 2025-07-30 05:00:11
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

public class PendingDealsheetsTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver"); // Set path to your chromedriver
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login"); // Adjust the URL as needed

        // Log in as an approver
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("approverUser"); // Use a valid approver username
        passwordField.sendKeys("password123"); // Use the correct password
        loginButton.click();
    }

    @Test
    public void testAccessPendingDealsheets() {
        // Navigate to the dealsheets section
        WebElement dealsheetsTab = wait.until(ExpectedConditions.elementToBeClickable(By.id("dealsheetsTab")));
        dealsheetsTab.click();

        // Click on the 'Pending Dealsheets' tab
        WebElement pendingDealsheetsTab = wait.until(ExpectedConditions.elementToBeClickable(By.id("pendingDealsheetsTab")));
        pendingDealsheetsTab.click();

        // Verify that the list of pending dealsheets is displayed
        WebElement pendingDealsheetsList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pendingDealsheetsList")));
        assertTrue(pendingDealsheetsList.isDisplayed(), "Pending dealsheets list should be displayed.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}