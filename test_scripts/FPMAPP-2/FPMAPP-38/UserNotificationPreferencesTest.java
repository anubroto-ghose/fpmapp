/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-38
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:46:33
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

public class UserNotificationPreferencesTest {

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
    public void testUpdateNotificationPreferences() {
        navigateToSettings();
        changeNotificationPreferences();
        saveChanges();
        verifyPreferencesUpdated();
    }

    private void navigateToSettings() {
        WebElement settingsMenu = wait.until(ExpectedConditions.elementToBeClickable(By.id("settingsMenu")));
        settingsMenu.click();
    }

    private void changeNotificationPreferences() {
        WebElement emailNotificationCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("emailNotification")));
        WebElement inAppNotificationCheckbox = driver.findElement(By.id("inAppNotification"));

        if (!emailNotificationCheckbox.isSelected()) {
            emailNotificationCheckbox.click();
        }
        if (!inAppNotificationCheckbox.isSelected()) {
            inAppNotificationCheckbox.click();
        }
    }

    private void saveChanges() {
        WebElement saveButton = driver.findElement(By.id("saveButton"));
        saveButton.click();
    }

    private void verifyPreferencesUpdated() {
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        assertTrue(successMessage.isDisplayed(), "Notification preferences were not updated successfully.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}