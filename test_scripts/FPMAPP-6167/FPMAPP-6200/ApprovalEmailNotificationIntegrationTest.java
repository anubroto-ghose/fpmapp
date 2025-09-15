/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6200
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:56:06
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration test that uses Selenium WebDriver to trigger an approval state change,
 * mocks the email notification API call, and asserts that the notification is sent properly.
 * 
 * Preconditions:
 * - SMTP integration is assumed configured and active.
 * - Mocked REST call to /notifications/send-email to verify payload and simulate response.
 * 
 * This test covers: FPMAPP-6169-TC02
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApprovalEmailNotificationIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @MockBean
    private NotificationApiClient notificationApiClient;

    @Autowired
    private FpmDealsheetController fpmDealsheetController;

    @Captor
    ArgumentCaptor<NotificationEmailPayload> notificationCaptor;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver options
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1200,800");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test Steps:
     * 1. Trigger approval state change via a simulated UI approve button click on dealsheet.
     * 2. Mock email notification API endpoint (/notifications/send-email).
     * 3. Verify POST payload is correct and response success returned.
     * 4. Confirm (via mock) notification sent.
     */
    @Test
    public void testEmailNotificationTriggeredOnApprovalStateChange() throws InterruptedException {
        // Arrange: Prepare a test deal sheet for approval
        final String testDealsheetId = "DS-12345";
        final String approverEmail = "approver@example.com";
        final String approverUserId = "user-approver-1";

        // Mock email API success response
        when(notificationApiClient.sendEmailNotification(any(NotificationEmailPayload.class)))
            .thenAnswer(invocation -> {
                NotificationEmailPayload payload = invocation.getArgument(0);
                // Validate payload essential fields
                assertThat(payload.getRecipientEmail()).isNotEmpty();
                assertThat(payload.getSubject()).contains("Approval Status Changed");
                assertThat(payload.getBody()).contains(testDealsheetId);
                // Simulate success messageId
                return new NotificationResponse("msg-1234567890", "success");
            });

        // Act: Navigate to approval page and perform approval
        String baseUrl = "http://localhost:" + port + "/fpm/dealsheets/" + testDealsheetId + "/approve";
        driver.get(baseUrl);

        // Wait for approve button and click
        WebElement approveButton = driver.findElement(By.id("approveBtn"));
        approveButton.click();

        // Allow asynchronous backend processing and notification
        Thread.sleep(3000); // Preferably replace with waiter until signal or WebDriverWait in real prod

        // Assert: Verify that the mocked notification API has been called exactly once
        verify(notificationApiClient, times(1)).sendEmailNotification(notificationCaptor.capture());

        NotificationEmailPayload sentPayload = notificationCaptor.getValue();
        assertThat(sentPayload).isNotNull();
        assertThat(sentPayload.getRecipientEmail()).isEqualTo(approverEmail);
        assertThat(sentPayload.getSubject()).contains("Approval Status Changed");
        assertThat(sentPayload.getBody()).contains(testDealsheetId);

        // Further assertions could include UI confirmation for notification sent message presence
        WebElement successMessage = driver.findElement(By.id("notificationSuccessMsg"));
        assertThat(successMessage.getText()).contains("Email notification sent successfully");
    }

    // --- Mocks and Helper Classes --- //

    /**
     * This would be your HTTP client/service used inside application to invoke external notification API.
     * Mocked here to verify invocation and payload.
     */
    public interface NotificationApiClient {
        NotificationResponse sendEmailNotification(NotificationEmailPayload payload);
    }

    /**
     * Sample DTO for notification request payload.
     */
    public static class NotificationEmailPayload {
        private String recipientEmail;
        private String subject;
        private String body;

        // Getters and setters

        public String getRecipientEmail() {
            return recipientEmail;
        }

        public void setRecipientEmail(String recipientEmail) {
            this.recipientEmail = recipientEmail;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    /**
     * Sample DTO for notification API response.
     */
    public static class NotificationResponse {
        private String messageId;
        private String status;

        public NotificationResponse(String messageId, String status) {
            this.messageId = messageId;
            this.status = status;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}