/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-41
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:29:45
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

public class TravelRequestApprovalTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
        loginAsTravelManager();
    }

    private void loginAsTravelManager() {
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("travel_manager");
        passwordField.sendKeys("password123");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void testApproveTravelRequest() {
        navigateToTravelRequestApproval();
        selectPendingTravelRequest();
        approveTravelRequest();

        String status = getTravelRequestStatus();
        assertEquals("Approved", status);

        assertTrue(isNotificationDisplayed("Your travel request has been approved."));
    }

    private void navigateToTravelRequestApproval() {
        WebElement travelRequestMenu = driver.findElement(By.id("travelRequestMenu"));
        travelRequestMenu.click();

        WebElement approvalSection = driver.findElement(By.id("approvalSection"));
        approvalSection.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pendingRequests")));
    }

    private void selectPendingTravelRequest() {
        WebElement pendingRequest = driver.findElement(By.xpath("//div[@class='request' and @data-status='Pending'][1]"));
        pendingRequest.click();
    }

    private void approveTravelRequest() {
        WebElement approveButton = driver.findElement(By.id("approveButton"));
        approveButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalConfirmation")));
    }

    private String getTravelRequestStatus() {
        WebElement statusElement = driver.findElement(By.id("requestStatus"));
        return statusElement.getText();
    }

    private boolean isNotificationDisplayed(String message) {
        WebElement notification = driver.findElement(By.id("notification"));
        return notification.getText().contains(message);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}