/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5260
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:18:50
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.Duration;

import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.dto.FpmUserProfileController;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration Selenium test for verifying successful in-app notification on request rejection.
 *
 * Preconditions:
 * - User logged in as approver with at least one pending request.
 *
 * Test:
 * - Reject a pending request
 * - Verify in-app notification is displayed with correct details
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class InAppNotificationRejectionTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @BeforeAll
    public static void setUpClass() {
        // Setup ChromeDriver (Assuming chromedriver is on PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() throws Exception {
        // Mock the user as logged in approver with pending requests
        when(fpmUserProfileController.getLoggedInUser())
            .thenReturn(new com.webapp.fpmapp.entities.User(1001L, "approverUser", "Approver", "ROLE_APPROVER"));

        // Mock pending request availability
        when(fpmCommonController.hasPendingRequest(anyLong())).thenReturn(true);

        // Mock the rejection process
        when(fpmCommonController.rejectRequest(anyLong(), anyLong())).thenReturn(true);

        // Mock in-app notification retrieval for the approver
        when(fpmCommonController.getInAppNotifications(anyLong()))
            .thenReturn(java.util.List.of(
                new com.webapp.fpmapp.dto.NotificationDTO(
                    5001L,
                    "Request #12345 has been rejected",
                    "Your request #12345 was rejected by approverUser",
                    "REJECTION",
                    "2024-06-10T12:30:00Z"
                )
            ));
    }

    @Test
    public void testSuccessfulInAppNotificationOnRejection() {
        try {
            driver.get(BASE_URL + "/login");

            // Login as approver
            WebElement usernameInput = wait.until(visibilityOfElementLocated(By.id("username")));
            usernameInput.clear();
            usernameInput.sendKeys("approverUser");

            WebElement passwordInput = driver.findElement(By.id("password"));
            passwordInput.clear();
            passwordInput.sendKeys("SecurePass123!");

            WebElement loginButton = driver.findElement(By.id("loginButton"));
            loginButton.click();

            // Wait for dashboard page load
            wait.until(visibilityOfElementLocated(By.id("dashboard")));

            // Navigate to pending requests page
            driver.get(BASE_URL + "/approvals/pending");

            // Identify a pending request element
            WebElement pendingRequest = wait.until(visibilityOfElementLocated(By.cssSelector(".request-item[data-request-id='12345']")));
            assertNotNull(pendingRequest, "Pending request #12345 should be present");

            // Click on reject button
            WebElement rejectButton = pendingRequest.findElement(By.cssSelector("button.reject-request"));
            rejectButton.click();

            // Confirm rejection modal
            WebElement confirmRejectBtn = wait.until(visibilityOfElementLocated(By.id("confirmReject")));
            confirmRejectBtn.click();

            // Wait for rejection to process and page to update
            wait.until(visibilityOfElementLocated(By.id("notificationIcon")));

            // Open in-app notification panel
            WebElement notificationIcon = driver.findElement(By.id("notificationIcon"));
            notificationIcon.click();

            // Wait for notifications to load
            WebElement notificationPanel = wait.until(visibilityOfElementLocated(By.id("notificationPanel")));
            assertTrue(notificationPanel.isDisplayed(), "Notification panel should be visible");

            // Validate the rejection notification exists
            java.util.List<WebElement> notifications = notificationPanel.findElements(By.cssSelector(".notification-item"));
            boolean rejectionNotificationFound = notifications.stream().anyMatch(n -> {
                String title = n.findElement(By.cssSelector(".notification-title")).getText();
                return title.contains("rejected") && title.contains("12345");
            });

            assertTrue(rejectionNotificationFound, "Rejection notification for request #12345 should be present");

        } catch (Exception e) {
            fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }
}
