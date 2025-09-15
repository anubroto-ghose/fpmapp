/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6225
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:36:47
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Complete integration test using Selenium WebDriver with mocked services for approval notification workflow.
 * 
 * Preconditions:
 * - SMTP mail service and UI notification service are active (mocked here).
 * - Approval routing triggers notification for approver.
 * 
 * Verifies both email and in-app notifications.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApprovalNotificationIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @Autowired
    private FpmDealsheetController dealsheetController;

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    private static final String APPROVER_C_USERNAME = "approverC";
    private static final String APPROVER_C_EMAIL = "approverC@bank.com";

    private final List<SimpleMailMessage> sentEmails = new ArrayList<>();
    private final AtomicBoolean inAppNotificationReceived = new AtomicBoolean(false);

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver in headless mode
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver"); // Adjust path accordingly
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void teardownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testApproverReceivesInAppAndEmailNotificationOnTaskAssignment() throws InterruptedException {

        // Mock sending email to capture sent messages
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            if (args.length > 0 && args[0] instanceof SimpleMailMessage) {
                SimpleMailMessage mail = (SimpleMailMessage) args[0];
                sentEmails.add(mail);
            }
            return null;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        // Mock FpmCommonController to simulate notification triggered successfully
        doAnswer(invocation -> {
            // Simulate in-app notification push
            inAppNotificationReceived.set(true);
            return null;
        }).when(fpmCommonController).sendInAppNotification(any(String.class), any(String.class));

        // Prepare test data: approval request assigned to approver C
        User approverC = new User();
        approverC.setUsername(APPROVER_C_USERNAME);
        approverC.setEmail(APPROVER_C_EMAIL);

        // Simulate routing approval request to Approver C which triggers notifications
        // Assuming dealsheetController.routeApproval handles routing and notification
        // Use realistic approval data

        FpmDealsheetController.ApprovalRoutingRequest routingRequest = new FpmDealsheetController.ApprovalRoutingRequest();
        routingRequest.setApprovalId(123456L);
        routingRequest.setApproverUserId(APPROVER_C_USERNAME);
        routingRequest.setDealSheetReference("DS-2025-0001");

        // Mock any dependent service calls inside routeApproval if needed
        when(fpmCommonController.isNotificationEnabled(APPROVER_C_USERNAME)).thenReturn(true);

        dealsheetController.routeApproval(routingRequest);

        // Confirm that in-app notification is received
        assertThat(inAppNotificationReceived.get())
            .as("In-app notification should be triggered for approver C")
            .isTrue();

        // Confirm that email was sent
        assertThat(sentEmails).as("Email notification list should contain at least one email").isNotEmpty();

        // Find email sent to Approver C
        SimpleMailMessage approverEmail = sentEmails.stream()
                .filter(msg -> msg.getTo() != null &&
                        List.of(msg.getTo()).contains(APPROVER_C_EMAIL))
                .findFirst()
                .orElse(null);

        assertThat(approverEmail)
                .as("An email must have been sent to Approver C's registered email")
                .isNotNull();

        // Validate email content references the deal sheet
        assertThat(approverEmail.getSubject())
                .as("Email subject must contain deal sheet reference")
                .contains("DS-2025-0001");
        assertThat(approverEmail.getText())
                .as("Email body must reference the approval task and deal sheet")
                .contains("new approval task")
                .contains("DS-2025-0001");

        // Selenium UI verification - simulate Approver C login and notification center check
        try {
            driver.get("http://localhost:" + port + "/login");

            // Login as Approver C - assuming page elements for username/password exist
            WebElement usernameInput = driver.findElement(By.id("username"));
            usernameInput.clear();
            usernameInput.sendKeys(APPROVER_C_USERNAME);

            WebElement passwordInput = driver.findElement(By.id("password"));
            passwordInput.clear();
            passwordInput.sendKeys("password123"); // assuming known test password

            WebElement loginButton = driver.findElement(By.id("loginButton"));
            loginButton.click();

            // Wait for redirection to dashboard
            Thread.sleep(2000);

            driver.get("http://localhost:" + port + "/notifications");

            // Check notification center for the task notification
            WebElement notificationList = driver.findElement(By.id("notificationList"));

            boolean foundNotification = notificationList.findElements(By.className("notification-item")).stream()
                    .anyMatch(el -> el.getText().contains("DS-2025-0001") && el.getText().contains("approval task assigned"));

            assertThat(foundNotification)
                .as("In-app notification center should show the new approval task notification")
                .isTrue();

        } catch (Exception e) {
            throw new AssertionError("Selenium UI verification failed", e);
        }
    }
}
