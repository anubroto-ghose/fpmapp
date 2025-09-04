/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5261
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:18:25
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.services.FpmCommonController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

/**
 * Integration test using Selenium WebDriver for Notification Preference Settings.
 * Preconditions: Logged in as an Approver with access to notification settings.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class NotificationPreferenceSettingsTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private FpmCommonController commonController; // just to show that service is injected (not used directly here)

    private final String baseUrl = "http://localhost:8080";

    @BeforeAll
    public static void setup() {
        // Setup ChromeDriver path or use WebDriverManager in real env
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
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
    @DisplayName("FPMAPP-5261: Approver can set notification preferences for email and in-app notifications")
    public void testNotificationPreferenceSettings() {
        // Mock the service response for saving preferences
        Mockito.when(fpmCommonController.saveNotificationPreferences(Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean()))
                .thenReturn(true);

        // 1. Navigate to login page
        driver.get(baseUrl + "/login");

        // 2. Login as approver
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("approverUser");
        passwordInput.sendKeys("SecurePa$$123");
        loginButton.click();

        // Wait for login to complete by checking presence of notification preferences link/button
        WebElement notificationPrefLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-notification-preferences")));
        notificationPrefLink.click();

        // 3. On notification preferences page, select email and in-app
        WebElement emailCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("notify-email")));
        WebElement inAppCheckbox = driver.findElement(By.id("notify-inapp"));

        if(!emailCheckbox.isSelected()) {
            emailCheckbox.click();
        }
        if(!inAppCheckbox.isSelected()) {
            inAppCheckbox.click();
        }

        // 4. Save changes
        WebElement saveButton = driver.findElement(By.id("save-notification-prefs"));
        saveButton.click();

        // Wait for confirmation message
        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-save-confirmation")));

        // 5. Assert confirmation message presence and text
        Assertions.assertTrue(confirmationMessage.isDisplayed(), "Confirmation message is not displayed");
        Assertions.assertEquals("Notification preferences saved successfully.", confirmationMessage.getText().trim(), 
                "Unexpected confirmation message text");

        // Additional sanity check: Preferences remain selected after save
        WebElement emailCheckboxAfter = driver.findElement(By.id("notify-email"));
        WebElement inAppCheckboxAfter = driver.findElement(By.id("notify-inapp"));
        Assertions.assertTrue(emailCheckboxAfter.isSelected(), "Email notification checkbox should remain selected after save");
        Assertions.assertTrue(inAppCheckboxAfter.isSelected(), "In-app notification checkbox should remain selected after save");
    }
}