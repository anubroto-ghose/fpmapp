/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-43
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:48:01
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

import java.time.Duration;

public class FpmStatusTrackingTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:8080/status-tracking");
    }

    @Test
    public void testStatusUpdateVisibility() {
        // Simulate a request submission
        WebElement submitButton = driver.findElement(By.id("submitRequestButton"));
        submitButton.click();

        // Wait for a status change to occur in the backend
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("statusMessage")));

        // Observe the status displayed on the UI
        WebElement statusMessage = driver.findElement(By.id("statusMessage"));
        String updatedStatus = statusMessage.getText();

        // Assert that the status is updated correctly
        assertTrue(updatedStatus.equals("Approved") || updatedStatus.equals("Rejected"),
                "Status should be either Approved or Rejected");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}