/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4669
 * Epic: BANK-4644
 * Generated on: 2025-07-30 16:59:00
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FpmDealsheetSubmissionTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
        loginAsFinancialAnalyst();
    }

    private void loginAsFinancialAnalyst() {
        driver.findElement(By.id("username")).sendKeys("analyst_user");
        driver.findElement(By.id("password")).sendKeys("secure_password");
        driver.findElement(By.id("loginButton")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void testSubmitDealsheetWithPendingApproval() {
        driver.findElement(By.linkText("Submit Dealsheet")).click();

        driver.findElement(By.id("dealsheetTitle")).sendKeys("Q1 Financial Overview");
        driver.findElement(By.id("amount")).sendKeys("10000");
        driver.findElement(By.id("description")).sendKeys("Overview of Q1 financials");

        driver.findElement(By.id("submitDealsheetButton")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmationMessage")));
        String confirmationMessage = driver.findElement(By.id("confirmationMessage")).getText();
        assertEquals("Dealsheet submitted for approval!", confirmationMessage);

        String approvalStatus = driver.findElement(By.id("dealsheetStatus")).getText();
        assertEquals("Pending Approval", approvalStatus);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}