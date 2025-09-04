/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5261
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:51:23
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.dtos.User;
import com.webapp.fpmapp.services.FpmCommonController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class NotificationPreferenceSettingsTest {

    private static WebDriver driver;

    @LocalServerPort
    private int port;

    private String baseUrl;

    private static WebDriverWait wait;

    // Mock FpmUserProfileController to simulate saving preferences
    @MockBean
    private FpmUserProfileController userProfileController;

    @Autowired
    private FpmCommonController commonController;

    @BeforeAll
    public static void setupClass() {
        // Assuming chromedriver is on PATH
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testSaveNotificationPreferencesAsApprover() {
        // Arrange
        baseUrl = "http://localhost:" + port;

        // Mock logged in user as approver
        User mockUser = new User();
        mockUser.setId(101L);
        mockUser.setUsername("approverUser");
        mockUser.setRole("APPROVER");

        // Mock controller save preferences behavior
        when(userProfileController.saveNotificationPreferences(mockUser.getId(), true, true))
            .thenReturn(true);

        // Simulate user login - simplified as setting a cookie or session attribute is possible
        driver.get(baseUrl + "/login");

        // Fill login form - assuming login page has username and password fields
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("approverUser");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("loginBtn")).click();

        // Wait for redirect to user dashboard/home page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Act
        // Navigate to notification preferences page
        driver.get(baseUrl + "/user/notification-preferences");
        wait.until(ExpectedConditions.titleContains("Notification Preferences"));

        // Select email and in-app notification checkboxes
        WebElement emailCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("notifyEmail")));
        WebElement inAppCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("notifyInApp")));

        if (!emailCheckbox.isSelected()) {
            emailCheckbox.click();
        }
        if (!inAppCheckbox.isSelected()) {
            inAppCheckbox.click();
        }

        // Click save button
        WebElement saveButton = driver.findElement(By.id("saveNotificationPrefsBtn"));
        saveButton.click();

        // Wait for confirmation message
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notificationSaveSuccess")));

        // Assert
        assertTrue(confirmation.getText().contains("Preferences saved successfully"),
            "The notification preferences save confirmation message was not displayed.");

        // Additional backend verification could be done by mocking the saveNotificationPreferences call
        // (already done above) or verifying database state if integration tests have DB access.
    }
}