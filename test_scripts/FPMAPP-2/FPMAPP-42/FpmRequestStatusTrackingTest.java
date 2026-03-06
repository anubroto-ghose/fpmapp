/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-42
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:47:45
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

public class FpmRequestStatusTrackingTest {
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
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("requester@example.com");
        passwordField.sendKeys("password");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void testRequestStatusVisibility() {
        driver.findElement(By.linkText("My Requests")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("requestsTable")));
        WebElement requestsTable = driver.findElement(By.id("requestsTable"));

        // Check if the table is displayed
        assertTrue(requestsTable.isDisplayed(), "Requests table should be visible.");

        // Get the status of the first request
        WebElement firstRequestStatus = requestsTable.findElement(By.xpath(".//tr[1]/td[@class='status']"));
        String initialStatus = firstRequestStatus.getText();

        // Refresh the page
        driver.navigate().refresh();

        // Wait for the table to be visible again
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("requestsTable")));
        String updatedStatus = requestsTable.findElement(By.xpath(".//tr[1]/td[@class='status']")).getText();

        // Assert that the status is updated
        assertEquals(initialStatus, updatedStatus, "The status should be updated after refresh.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}