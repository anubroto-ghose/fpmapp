/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4621
 * Epic: BANK-4579
 * Generated on: 2025-07-30 04:58:39
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

public class RejectDealsheetTest {

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
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("approver@example.com");
        passwordField.sendKeys("password");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void testRejectDealsheet() {
        // Step 1: Access the list of pending dealsheets.
        driver.get("http://localhost:8080/dealsheets/pending");

        // Step 2: Select a dealsheet from the list.
        WebElement dealsheetLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".dealsheet-item:first-child")));
        dealsheetLink.click();

        // Step 3: Click on the 'Reject' button.
        WebElement rejectButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("rejectButton")));
        rejectButton.click();

        // Confirm rejection
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmRejectButton")));
        confirmButton.click();

        // Verify the dealsheet status is updated to 'Rejected'.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("statusMessage")));
        WebElement statusMessage = driver.findElement(By.id("statusMessage"));
        assertEquals("Status: Rejected", statusMessage.getText());

        // Verify an audit trail entry is created for the rejection action.
        WebElement auditTrail = driver.findElement(By.id("auditTrail"));
        assertTrue(auditTrail.getText().contains("Rejected by approver@example.com"));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}