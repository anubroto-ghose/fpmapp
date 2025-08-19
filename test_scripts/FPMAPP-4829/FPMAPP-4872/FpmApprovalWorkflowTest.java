/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4872
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-19 06:05:19
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

public class FpmApprovalWorkflowTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
    }

    @Test
    public void testApprovalAsDirector() {
        // Log in as Director
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("director@example.com");
        passwordField.sendKeys("password");
        loginButton.click();

        // Navigate to approval page
        wait.until(ExpectedConditions.urlContains("/approval"));

        // Attempt to approve a high-value request
        WebElement requestToApprove = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("highValueRequest")));
        WebElement approveButton = requestToApprove.findElement(By.className("approveButton"));
        approveButton.click();

        // Check approval status
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatus")));
        WebElement statusElement = driver.findElement(By.id("approvalStatus"));
        String statusText = statusElement.getText();

        // Assert that the request is approved
        assertEquals("Approved", statusText);
        assertTrue(statusText.contains("Approved"));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}