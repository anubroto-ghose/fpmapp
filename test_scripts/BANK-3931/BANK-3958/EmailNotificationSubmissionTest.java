/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-3958
 * Epic: BANK-3931
 * Generated on: 2025-07-18 13:44:15
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

public class EmailNotificationSubmissionTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void testEmailNotificationOnSubmission() {
        // Navigate to login page
        driver.get("http://localhost:8080/login");

        // Log in as user
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("testuser");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password"))).sendKeys("password");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("loginBtn"))).click();

        // Navigate to the financial entry submission form
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submitFinancialEntry"))).click();

        // Fill in financial entry details
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("entryDescription"))).sendKeys("Travel Expense");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("entryAmount"))).sendKeys("1000");

        // Submit financial entry
        wait.until(ExpectedConditions.elementToBeClickable(By.id("submitEntryBtn"))).click();

        // Wait for success message and assert email sent
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        assertTrue(driver.findElement(By.id("successMessage")).getText().contains("Submission successful! An email notification has been sent."));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}