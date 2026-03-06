/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-40
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:29:28
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

public class DealSheetApprovalTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
        loginAsStaffMember();
    }

    private void loginAsStaffMember() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("staff_member");
        passwordField.sendKeys("password");
        loginButton.click();
    }

    @Test
    public void testDenialOfDealSheetApprovalByUnauthorizedUser() {
        navigateToDealSheetApproval();
        selectPendingDealSheet();
        clickApproveButton();

        String errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage"))).getText();
        assertEquals("Insufficient permissions to approve this deal sheet.", errorMessage);

        String dealSheetStatus = driver.findElement(By.id("dealSheetStatus")).getText();
        assertEquals("Pending", dealSheetStatus);
    }

    private void navigateToDealSheetApproval() {
        WebElement dealSheetApprovalLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dealSheetApprovalLink")));
        dealSheetApprovalLink.click();
    }

    private void selectPendingDealSheet() {
        WebElement pendingDealSheet = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pending-deal-sheet")));
        pendingDealSheet.click();
    }

    private void clickApproveButton() {
        WebElement approveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approveButton")));
        approveButton.click();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}