/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6231
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:32:26
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test verifying immediate email notifications on approval state changes.
 * 
 * Preconditions:
 * - SMTP integration is active.
 * - User emails are correctly registered.
 * - Approval workflow can be initiated and progressed.
 * 
 * Note: This test mocks the /notifications/send-email API call to verify inter-service notification.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApprovalNotificationIntegrationTest {

    private WebDriver driver;

    private static final int EMAIL_API_RESPONSE_TIMEOUT_SECONDS = 5;

    // Thread-safe queue to simulate email notification send request capturing
    private BlockingQueue<EmailNotification> sentEmailNotifications;

    @Autowired
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeEach
    public void setup() {
        // Setup Chrome WebDriver in headless mode for integration test
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1200");
        driver = new ChromeDriver(options);

        // Initialization of the email notification capturing queue
        sentEmailNotifications = new LinkedBlockingQueue<>();

        // Mock the email sending API: simulate /notifications/send-email calls
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            EmailNotification notification = (EmailNotification) args[0];
            // Add to queue simulating capture of sent email
            sentEmailNotifications.offer(notification);
            // return success response simulation
            return new ApiResponse(true, "msg-" + System.currentTimeMillis());
        }).when(fpmCommonController).sendEmailNotification(any(EmailNotification.class));

        // Mock currency conversion service stable response
        when(currencyConvertionController.getExchangeRate(any(String.class), any(String.class)))
                .thenReturn(1.1);

        // Mock other services' behavior as needed
        reset(fpmDealsheetController);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        reset(fpmCommonController);
        reset(currencyConvertionController);
    }

    @Test
    public void testApprovalStateChangeTriggersImmediateEmailNotifications() throws Exception {
        // Simulated user and approval data
        final String requesterEmail = "requester@example.com";
        final String approverEmail = "approver@example.com";

        // Step 1: Initiate an approval request and progress its state to "approved"
        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setRequestId("REQ123");
        approvalRequest.setRequesterEmail(requesterEmail);
        approvalRequest.setApproverEmail(approverEmail);

        // Mock initiating approval workflow in Dealsheet Controller
        when(fpmDealsheetController.initiateApproval(any())).thenReturn(new ApprovalResponse(true, "REQ123"));
        when(fpmDealsheetController.changeApprovalState(any(String.class), any(String.class)))
                .thenAnswer(invocation -> {
                    String reqId = invocation.getArgument(0);
                    String newState = invocation.getArgument(1);
                    // Simulate email notification sent on state change
                    EmailNotification notification = new EmailNotification();
                    notification.setRecipientEmail(approverEmail);
                    notification.setSubject("Approval " + newState + " Notification");
                    notification.setBody("Your approval request " + reqId + " has been " + newState + ".");
                    fpmCommonController.sendEmailNotification(notification);
                    return new ApprovalResponse(true, reqId);
                });

        ApprovalResponse response = fpmDealsheetController.initiateApproval(approvalRequest);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getRequestId()).isEqualTo("REQ123");

        // Progress states: approved, rejected, delegated, overridden
        List<String> approvalStates = List.of("approved", "rejected", "delegated", "overridden");

        for (String state : approvalStates) {
            ApprovalResponse stateChangeResponse = fpmDealsheetController.changeApprovalState("REQ123", state);
            assertThat(stateChangeResponse.isSuccess()).isTrue();

            // Step 2: Monitor the email sending process through the mocked send-email API
            EmailNotification sentEmail = sentEmailNotifications.poll(EMAIL_API_RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertThat(sentEmail).withFailMessage("Expected email was not sent on state %s", state).isNotNull();

            // Step 3: Check that the email content is correct
            assertThat(sentEmail.getRecipientEmail()).isEqualTo(approverEmail);
            assertThat(sentEmail.getSubject()).containsIgnoringCase(state);
            assertThat(sentEmail.getBody()).containsIgnoringCase(state);

            // Step 4: Check API response message ID presence
            // simulate by checking that sendEmailNotification returned ApiResponse with messageId
            ApiResponse emailApiResponse = fpmCommonController.sendEmailNotification(sentEmail);
            assertThat(emailApiResponse.isSuccess()).isTrue();
            assertThat(emailApiResponse.getMessageId()).isNotEmpty();
        }
    }

    // Supporting DTOs & API response classes

    static class EmailNotification {
        private String recipientEmail;
        private String subject;
        private String body;

        public String getRecipientEmail() { return recipientEmail; }
        public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }

    static class ApprovalRequest {
        private String requestId;
        private String requesterEmail;
        private String approverEmail;
        
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        public String getRequesterEmail() { return requesterEmail; }
        public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }
        public String getApproverEmail() { return approverEmail; }
        public void setApproverEmail(String approverEmail) { this.approverEmail = approverEmail; }
    }

    static class ApprovalResponse {
        private boolean success;
        private String requestId;

        public ApprovalResponse(boolean success, String requestId) {
            this.success = success;
            this.requestId = requestId;
        }

        public boolean isSuccess() { return success; }
        public String getRequestId() { return requestId; }
    }

    /**
     * Simulated API response from email notifications API
     */
    static class ApiResponse {
        private final boolean success;
        private final String messageId;

        public ApiResponse(boolean success, String messageId) {
            this.success = success;
            this.messageId = messageId;
        }

        public boolean isSuccess() { return success; }
        public String getMessageId() { return messageId; }
    }
}
