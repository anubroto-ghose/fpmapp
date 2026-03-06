/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-31
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:44:32
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

public class RequestStatusTrackingTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:8080/login");
        login();
    }

    private void login() {
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("requester@example.com");
        passwordField.sendKeys("password123");
        loginButton.click();
    }

    @Test
    public void testRequestStatusUpdates() {
        navigateToMyRequests();
        selectRequest();
        waitForStatusUpdate();
        refreshRequestStatus();
        verifyStatusUpdate();
    }

    private void navigateToMyRequests() {
        WebElement myRequestsLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("myRequestsLink")));
        myRequestsLink.click();
    }

    private void selectRequest() {
        WebElement request = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".request-item:first-child")));
        request.click();
    }

    private void waitForStatusUpdate() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("statusUpdateMessage")));
    }

    private void refreshRequestStatus() {
        WebElement refreshButton = driver.findElement(By.id("refreshStatusButton"));
        refreshButton.click();
    }

    private void verifyStatusUpdate() {
        WebElement statusElement = driver.findElement(By.id("requestStatus"));
        String statusText = statusElement.getText();
        assertTrue(statusText.equals("Approved") || statusText.equals("Rejected"), "Status should be updated to Approved or Rejected");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}