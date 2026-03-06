/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-39
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:46:50
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
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("financial_manager");
        passwordField.sendKeys("password123");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void testApproveDealSheet() {
        navigateToDealSheetApproval();
        selectPendingDealSheet();
        approveDealSheet();

        String statusMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("statusMessage"))).getText();
        assertEquals("Deal sheet approved successfully!", statusMessage);

        String dealSheetStatus = driver.findElement(By.id("dealSheetStatus")).getText();
        assertEquals("Approved", dealSheetStatus);
    }

    private void navigateToDealSheetApproval() {
        WebElement approvalSection = driver.findElement(By.id("dealSheetApprovalSection"));
        approvalSection.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pendingDealSheets")));
    }

    private void selectPendingDealSheet() {
        WebElement pendingDealSheet = driver.findElement(By.xpath("//div[@class='dealSheet' and @data-status='Pending'][1]"));
        pendingDealSheet.click();
    }

    private void approveDealSheet() {
        WebElement approveButton = driver.findElement(By.id("approveButton"));
        approveButton.click();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}