/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-32
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:44:50
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

public class FpmRequestStatusHistoryTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
        login();
    }

    private void login() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("testuser");
        passwordField.sendKeys("password");
        loginButton.click();
    }

    @Test
    public void testViewStatusHistory() {
        navigateToMyRequests();
        selectRequest();
        viewStatusHistory();
        assertStatusHistoryDisplayed();
    }

    private void navigateToMyRequests() {
        WebElement myRequestsLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("My Requests")));
        myRequestsLink.click();
    }

    private void selectRequest() {
        WebElement requestLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".request-item:first-child")));
        requestLink.click();
    }

    private void viewStatusHistory() {
        WebElement viewStatusHistoryButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("viewStatusHistoryButton")));
        viewStatusHistoryButton.click();
    }

    private void assertStatusHistoryDisplayed() {
        WebElement statusHistory = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("statusHistory")));
        assertTrue(statusHistory.isDisplayed(), "Status history should be displayed.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}