/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-3966
 * Epic: BANK-3931
 * Generated on: 2025-07-18 13:35:40
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
import java.time.Duration;

public class FpmReportFilterTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:8080/login");
    }

    @Test
    public void testReportFilteringByStatus() {
        // Log in as a financial analyst
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("analyst@bank.com");
        passwordField.sendKeys("password123");
        loginButton.click();

        // Navigate to the report section
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reportSection"))).click();

        // Apply filter for approval status
        WebElement filterDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("statusFilter")));
        filterDropdown.click();
        WebElement pendingOption = driver.findElement(By.xpath("//option[text()='Pending']"));
        pendingOption.click();

        // Trigger report generation
        WebElement generateReportButton = driver.findElement(By.id("generateReportButton"));
        generateReportButton.click();

        // Validate results
        WebElement reportTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reportTable")));
        // Check if entries are filtered correctly
        boolean hasPending = reportTable.getText().contains("Pending");
        boolean hasApproved = reportTable.getText().contains("Approved");
        boolean hasRejected = reportTable.getText().contains("Rejected");

        assertTrue(hasPending, "Report should show 'Pending' status entries.");
        assertFalse(hasApproved, "Report should NOT show 'Approved' status entries.");
        assertFalse(hasRejected, "Report should NOT show 'Rejected' status entries.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}