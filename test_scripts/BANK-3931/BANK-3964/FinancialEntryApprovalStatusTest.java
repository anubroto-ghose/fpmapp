/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-3964
 * Epic: BANK-3931
 * Generated on: 2025-07-18 13:37:21
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FinancialEntryApprovalStatusTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        // Set path to WebDriver executable if not a global installation
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.manage().window().maximize();
    }

    @Test
    public void testApprovalStatusDisplayed() {
        // Navigate to Financial Entry page
        driver.get("http://localhost:8080/financial-entry");

        // Wait until the approval status element is present
        WebElement approvalStatusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatus")));

        // Check that the approval status is displayed
        String approvalStatusText = approvalStatusElement.getText();
        assertTrue(approvalStatusText != null && !approvalStatusText.isEmpty(), "Approval status should be displayed.");

        // Clean up
        driver.quit();
    }
}
