/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4863
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-19 06:15:59
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FpmDealsheetStatusTrackingTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
        loginAsRequester();
    }

    private void loginAsRequester() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("requestor@example.com");
        passwordField.sendKeys("password");
        loginButton.click();
    }

    @Test
    public void testStatusTrackingVisibility() {
        driver.get("http://localhost:8080/submissions");

        WebElement submittedRequest = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'My Submitted Request')]")));
        assertTrue(submittedRequest.isDisplayed(), "Submitted request should be visible.");

        WebElement statusTrackingSection = submittedRequest.findElement(By.className("status-tracking"));
        assertTrue(statusTrackingSection.isDisplayed(), "Status tracking section should be visible.");

        String currentStatus = statusTrackingSection.getText();
        assertEquals("Pending Approval", currentStatus, "The status should be 'Pending Approval'.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}