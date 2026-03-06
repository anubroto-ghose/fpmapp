/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-39
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:29:12
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

public class DealSheetApprovalTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
        loginAsFinancialManager();
    }

    private void loginAsFinancialManager() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("financial_manager");
        passwordField.sendKeys("password123");
        loginButton.click();
    }

    @Test
    public void testApproveDealSheet() {
        navigateToDealSheetApproval();
        selectPendingDealSheet();
        approveDealSheet();
        verifyApprovalSuccess();
    }

    private void navigateToDealSheetApproval() {
        WebElement approvalSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dealSheetApprovalSection")));
        approvalSection.click();
    }

    private void selectPendingDealSheet() {
        WebElement pendingDealSheet = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pending-deal-sheet")));
        pendingDealSheet.click();
    }

    private void approveDealSheet() {
        WebElement approveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approveButton")));
        approveButton.click();
    }

    private void verifyApprovalSuccess() {
        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmationMessage")));
        assertTrue(confirmationMessage.isDisplayed(), "Confirmation message should be displayed.");
        assertEquals("Deal sheet approved successfully!", confirmationMessage.getText());

        WebElement dealSheetStatus = driver.findElement(By.id("dealSheetStatus"));
        assertEquals("Approved", dealSheetStatus.getText(), "Deal sheet status should be 'Approved'.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}