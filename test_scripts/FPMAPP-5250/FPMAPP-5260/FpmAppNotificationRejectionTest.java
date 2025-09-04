/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5260
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:52:26
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

import com.webapp.fpmapp.controllers.FpmcamundaController;
import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
public class FpmAppNotificationRejectionTest {

    private static WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_APPROVER_USERNAME = "approverUser";
    private static final String TEST_APPROVER_PASSWORD = "Password123!";

    @MockBean
    private FpmCommonController mockFpmCommonController;

    @MockBean
    private FpmForecastController mockFpmForecastController;

    @MockBean
    private FpmDealsheetController mockFpmDealsheetController;

    @MockBean
    private FpmTravelController mockFpmTravelController;

    @MockBean
    private FpmUserProfileController mockFpmUserProfileController;

    @MockBean
    private FpmcamundaController mockFpmcamundaController;

    @BeforeAll
    public static void setUpClass() {
        // Setup ChromeDriver path here or configure via system property
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
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

        // Mock behavior for user login and request fetch
        User mockApprover = new User();
        mockApprover.setId(1001L);
        mockApprover.setUsername(TEST_APPROVER_USERNAME);

        // Mock login user profile
        when(mockFpmUserProfileController.getCurrentUser()).thenReturn(mockApprover);

        // Mock pending request retrieval
        when(mockFpmcamundaController.getPendingApprovalRequestForUser(TEST_APPROVER_USERNAME))
            .thenReturn(ApprovalTestData.pendingApprovalRequest());

        // Mock rejection processing
        when(mockFpmcamundaController.rejectRequest(any(Long.class), any(String.class)))
            .thenReturn(ApprovalTestData.rejectionResponse());

        // Mock notification generation
        when(mockFpmcamundaController.getInAppNotifications(TEST_APPROVER_USERNAME))
            .thenReturn(ApprovalTestData.rejectionNotification());
    }

    @Test
    public void testInAppNotificationOnRejection() {
        driver.get(BASE_URL + "/login");

        // Login as approver
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys(TEST_APPROVER_USERNAME);
        passwordInput.sendKeys(TEST_APPROVER_PASSWORD);
        loginButton.click();

        // Wait for redirect to dashboard
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to Approval Requests Page
        driver.get(BASE_URL + "/approvals/pending");

        // Locate the pending request row by unique request id
        String requestId = String.valueOf(ApprovalTestData.PENDING_REQUEST_ID);
        WebElement requestRow = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//tr[td[text()='" + requestId + "']]")
        ));

        // Click the Reject button on the pending request
        WebElement rejectButton = requestRow.findElement(By.cssSelector("button.reject-request"));
        rejectButton.click();

        // Confirm rejection modal - assuming a modal dialog appears
        WebElement confirmRejectButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("confirmRejectButton")
        ));
        confirmRejectButton.click();

        // Wait for the rejection to be processed with a success notification or UI element
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rejectionSuccessMessage")));

        // Navigate to In-App Notifications
        driver.get(BASE_URL + "/notifications/in-app");

        // Verify that a notification about the rejection exists
        // We look for a notification element containing the request ID and 'rejected' keyword
        boolean notificationFound = driver.findElements(By.cssSelector(".notification-item")).stream()
            .anyMatch(element -> {
                String text = element.getText();
                return text.contains(requestId) && text.toLowerCase().contains("rejected");
            });

        assertTrue(notificationFound, "Expected rejection notification not found in in-app notifications.");
    }

    /**
     * Helper inner class holding static test data and mock responses.
     */
    static class ApprovalTestData {

        static final long PENDING_REQUEST_ID = 5001L;

        static ApprovalRequest pendingApprovalRequest() {
            ApprovalRequest request = new ApprovalRequest();
            request.setId(PENDING_REQUEST_ID);
            request.setRequesterUsername("requesterUser");
            request.setStatus("PENDING_APPROVAL");
            request.setDetails("Travel request to NYC for client meeting.");
            return request;
        }

        static ApprovalResponse rejectionResponse() {
            ApprovalResponse response = new ApprovalResponse();
            response.setRequestId(PENDING_REQUEST_ID);
            response.setStatus("REJECTED");
            response.setMessage("Request has been rejected successfully.");
            return response;
        }

        static java.util.List<Notification> rejectionNotification() {
            Notification notification = new Notification();
            notification.setId(9001L);
            notification.setUserId(1001L);
            notification.setType("REJECTION");
            notification.setMessage("Your approval request #" + PENDING_REQUEST_ID + " was rejected.");
            notification.setTimestamp(java.time.Instant.now());
            return java.util.Collections.singletonList(notification);
        }
    }

    // Dummy DTOs for mocking
    static class ApprovalRequest {
        private long id;
        private String requesterUsername;
        private String status;
        private String details;

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }

        public String getRequesterUsername() { return requesterUsername; }
        public void setRequesterUsername(String requesterUsername) { this.requesterUsername = requesterUsername; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
    }

    static class ApprovalResponse {
        private long requestId;
        private String status;
        private String message;

        public long getRequestId() { return requestId; }
        public void setRequestId(long requestId) { this.requestId = requestId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    static class Notification {
        private long id;
        private long userId;
        private String type;
        private String message;
        private java.time.Instant timestamp;

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }

        public long getUserId() { return userId; }
        public void setUserId(long userId) { this.userId = userId; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public java.time.Instant getTimestamp() { return timestamp; }
        public void setTimestamp(java.time.Instant timestamp) { this.timestamp = timestamp; }
    }
}
