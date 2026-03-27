/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8630
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:53:47
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import java.sql.*;

/**
 * Integration Selenium test for verifying email notification triggers correctly on approval request assignment.
 * 
 * Preconditions:
 * - Approval request is created and assigned to an approver.
 * - SMTP email service is configured and operational.
 * 
 * Test Steps:
 * 1. Trigger an approval request assignment to a valid approver.
 * 2. Monitor the SMTP service for outgoing email.
 * 3. Verify the email is sent to the assigned approver's email address.
 * 4. Check the email content for relevant approval details and delegation information.
 * 5. Confirm the notification event is logged in the notification_logs database table.
 * 
 * Expected Results:
 * - An email notification is sent promptly to the assigned approver.
 * - Email content includes all relevant approval details and delegation info.
 * - The notification event is recorded in the notification_logs with correct recipient_id, notification_type, timestamp, and status.
 * - No errors occur during the email sending process.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalRequestEmailNotificationTest {

    private static WebDriver driver;

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private FpmCommonController fpmCommonController;

    private static BlockingQueue<MimeMessage> emailQueue = new LinkedBlockingQueue<>();

    private static final String BASE_URL = "http://localhost:8080";

    private static Connection dbConnection;

    @BeforeAll
    public static void setupClass() throws Exception {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Setup DB connection for verification
        String url = "jdbc:mysql://localhost:3306/fpmdb_test";
        String username = "testuser";
        String password = "testpass";
        dbConnection = DriverManager.getConnection(url, username, password);
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        if (driver != null) {
            driver.quit();
        }
        if (dbConnection != null && !dbConnection.isClosed()) {
            dbConnection.close();
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        // Clear email queue before each test
        emailQueue.clear();

        // Mock mailSender to capture sent emails
        doAnswer(invocation -> {
            MimeMessage message = invocation.getArgument(0);
            emailQueue.offer(message);
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Reset DB notification_logs for test user
        try (PreparedStatement stmt = dbConnection.prepareStatement("DELETE FROM notification_logs WHERE recipient_id = ?")) {
            stmt.setLong(1, 1001L); // test approver user id
            stmt.executeUpdate();
        }
    }

    @Test
    public void testEmailNotificationOnApprovalRequestAssignment() throws Exception {
        // Step 1: Trigger an approval request assignment to a valid approver
        // For test, simulate via UI or direct service call

        // Navigate to login page
        driver.get(BASE_URL + "/login");

        // Login as admin or user with permission to assign approval
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("adminpass");
        loginButton.click();

        // Wait for redirect to dashboard
        Thread.sleep(2000);

        // Navigate to approval request creation page
        driver.get(BASE_URL + "/approval-requests/create");

        // Fill approval request form
        WebElement approverInput = driver.findElement(By.id("approverEmail"));
        WebElement requestDetailsInput = driver.findElement(By.id("requestDetails"));
        WebElement submitButton = driver.findElement(By.id("submitApprovalRequest"));

        String approverEmail = "approver@example.com";
        approverInput.sendKeys(approverEmail);
        requestDetailsInput.sendKeys("Request approval for project budget increase.");
        submitButton.click();

        // Wait for processing
        Thread.sleep(3000);

        // Step 2: Monitor the SMTP service for outgoing email
        MimeMessage sentEmail = emailQueue.poll();
        assertThat(sentEmail).as("Email should be sent").isNotNull();

        // Step 3: Verify the email is sent to the assigned approver's email address
        String[] recipients = sentEmail.getAllRecipients() != null ?
                java.util.Arrays.stream(sentEmail.getAllRecipients())
                    .map(r -> r.toString())
                    .toArray(String[]::new) : new String[0];
        assertThat(recipients).contains(approverEmail);

        // Step 4: Check the email content for relevant approval details and delegation information
        MimeMessageHelper helper = new MimeMessageHelper(sentEmail, true);
        String subject = sentEmail.getSubject();
        String content = (String) sentEmail.getContent();

        assertThat(subject).containsIgnoringCase("Approval Request");
        assertThat(content).contains("Request approval for project budget increase");
        assertThat(content).contains("delegation").or().contains("delegate").as("Email should mention delegation info if applicable");

        // Step 5: Confirm the notification event is logged in the notification_logs database table
        try (PreparedStatement stmt = dbConnection.prepareStatement(
                "SELECT recipient_id, notification_type, timestamp, status FROM notification_logs WHERE recipient_id = ? ORDER BY timestamp DESC LIMIT 1")) {
            stmt.setLong(1, 1001L); // Approver user id
            try (ResultSet rs = stmt.executeQuery()) {
                assertThat(rs.next()).as("Notification log entry should exist").isTrue();
                long recipientId = rs.getLong("recipient_id");
                String notificationType = rs.getString("notification_type");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                String status = rs.getString("status");

                assertThat(recipientId).isEqualTo(1001L);
                assertThat(notificationType).isEqualToIgnoringCase("EMAIL_APPROVAL_REQUEST");
                assertThat(timestamp).isNotNull();
                assertThat(status).isEqualToIgnoringCase("SENT");
            }
        }
    }
}
