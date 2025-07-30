/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4620
 * Epic: BANK-4579
 * Generated on: 2025-07-30 04:59:26
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

public class ApproveDealsheetTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
        loginAsApprover();
    }

    private void loginAsApprover() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("approver@example.com");
        passwordField.sendKeys("password123");
        loginButton.click();
    }

    @Test
    public void testApproveDealsheet() {
        // Step 1: Access the list of pending dealsheets
        driver.get("http://localhost:8080/dealsheets/pending");

        // Step 2: Select a dealsheet from the list
        WebElement dealsheet = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='dealsheet'][1]")));
        dealsheet.click();

        // Step 3: Click on the 'Approve' button
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("approveButton")));
        approveButton.click();

        // Verify the expected results
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        assertEquals("Dealsheet approved successfully!", successMessage.getText());

        // Verify the dealsheet status
        WebElement status = driver.findElement(By.id("dealsheetStatus"));
        assertEquals("Approved", status.getText());

        // Verify audit trail entry
        WebElement auditTrail = driver.findElement(By.id("auditTrail"));
        assertTrue(auditTrail.getText().contains("Approved by approver@example.com"));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}