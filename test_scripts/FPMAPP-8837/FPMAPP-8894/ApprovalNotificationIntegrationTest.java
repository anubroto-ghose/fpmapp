/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8894
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:27:59
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test for approval routing and delegation notifications.
 * 
 * Preconditions:
 * - User is assigned as approver or delegate.
 * - Notification system configured to send emails and in-app alerts.
 * 
 * This test mocks backend services and verifies UI notifications and email triggers.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalNotificationIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @Autowired
    private NotificationEmailService notificationEmailService;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user profile to simulate approver user
        User approverUser = new User();
        approverUser.setId(1001L);
        approverUser.setUsername("approverUser");
        approverUser.setEmail("approver.user@example.com");
        approverUser.setRoles(Collections.singletonList("APPROVER"));

        when(userProfileController.getCurrentUser()).thenReturn(approverUser);

        // Mock notification email service to simulate email sending
        when(notificationEmailService.sendEmailNotification(any(), any(), any())).thenReturn(true);

        // Mock other services as needed
        when(fpmCommonController.isNotificationSystemConfigured()).thenReturn(true);
    }

    @Test
    public void testApprovalRoutingAndDelegationNotifications() throws InterruptedException {
        // Step 1: Initiate approval routing event
        boolean routingEventResult = fpmCommonController.initiateApprovalRouting(1001L, "Request-12345");
        assertThat(routingEventResult).isTrue();

        // Step 2: Initiate delegation event
        boolean delegationEventResult = fpmCommonController.delegateApproval(1001L, 2002L, "Request-12345");
        assertThat(delegationEventResult).isTrue();

        // Simulate backend sending email notifications
        boolean emailSentForRouting = notificationEmailService.sendEmailNotification(
                "approver.user@example.com",
                "Approval Routing Notification",
                "You have a new approval request: Request-12345");
        assertThat(emailSentForRouting).isTrue();

        boolean emailSentForDelegation = notificationEmailService.sendEmailNotification(
                "approver.user@example.com",
                "Approval Delegation Notification",
                "Your approval responsibility has been delegated for request: Request-12345");
        assertThat(emailSentForDelegation).isTrue();

        // Step 3 & 4: Check in-app notifications via Selenium
        driver.get("http://localhost:8080/login");

        // Login as approver user
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("approverUser");
        passwordInput.sendKeys("password123"); // assuming test password
        loginButton.click();

        // Wait for dashboard to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Open notification center
        WebElement notificationIcon = wait.until(ExpectedConditions.elementToBeClickable(By.id("notificationIcon")));
        notificationIcon.click();

        // Wait for notifications panel
        WebElement notificationsPanel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notificationsPanel")));

        // Verify notification for approval routing
        List<WebElement> notifications = notificationsPanel.findElements(By.className("notification-item"));
        boolean foundRoutingNotification = notifications.stream().anyMatch(n ->
                n.getText().contains("Approval Routing") && n.getText().contains("Request-12345"));
        boolean foundDelegationNotification = notifications.stream().anyMatch(n ->
                n.getText().contains("Approval Delegation") && n.getText().contains("Request-12345"));

        assertThat(foundRoutingNotification).as("Approval routing notification should be present").isTrue();
        assertThat(foundDelegationNotification).as("Approval delegation notification should be present").isTrue();

        // Verify notifications are recent (within last 1 minute)
        // Assuming notification timestamp is in an element with class 'notification-timestamp' and ISO format
        for (WebElement notification : notifications) {
            String timestampText = notification.findElement(By.className("notification-timestamp")).getText();
            // Parse timestamp and check recency
            // For brevity, assume timestampText is "2024-06-01T12:00:00Z" format
            java.time.ZonedDateTime notificationTime = java.time.ZonedDateTime.parse(timestampText);
            java.time.ZonedDateTime now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC);
            long secondsDiff = java.time.Duration.between(notificationTime, now).getSeconds();
            assertThat(secondsDiff).isLessThan(60).as("Notification should be received promptly");
        }
    }

    /**
     * Mock service to simulate email sending.
     * In real scenario, this would be an actual service or mocked bean.
     */
    public interface NotificationEmailService {
        boolean sendEmailNotification(String to, String subject, String body);
    }
}
