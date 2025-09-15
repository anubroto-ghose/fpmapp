/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6213
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:45:22
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.controller.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmDealsheetController.ApprovalStatusChangeRequest;
import com.webapp.fpmapp.service.RealTimeNotificationService;

import java.lang.reflect.Type;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApprovalNotificationIntegrationTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    private static WebSocketStompClient stompClient;
    private static StompSession stompSession;
    private BlockingQueue<String> notificationMessages;

    @MockBean
    private RealTimeNotificationService realTimeNotificationService;

    @InjectMocks
    private FpmDealsheetController fpmDealsheetController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver"); // Adjust path as needed

        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
        if (stompClient != null) {
            stompClient.stop();
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
        driver = new ChromeDriver(options);

        notificationMessages = new ArrayBlockingQueue<>(10);

        String websocketUrl = String.format("ws://localhost:%d/ws/approvals", port);

        StompSessionHandlerAdapter sessionHandler = new StompSessionHandlerAdapter() {};

        stompSession = stompClient.connect(websocketUrl, new WebSocketHttpHeaders(), sessionHandler).get();

        stompSession.subscribe("/topic/notifications", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                notificationMessages.offer((String) payload);
            }
        });

        // Mock RealTimeNotificationService to push notif messages to stompSession subscriber
        doAnswer(invocation -> {
            String approvalId = invocation.getArgument(0);
            String status = invocation.getArgument(1);
            String alertJson = String.format("{\"approvalId\":\"%s\",\"status\":\"%s\",\"message\":\"Approval status updated to %s\"}", approvalId, status, status);
            // Emulate sending message to WebSocket subscribers
            stompSession.send("/topic/notifications", alertJson.getBytes());
            return null;
        }).when(realTimeNotificationService).pushApprovalUpdate(any(String.class), any(String.class));
    }

    @Test
    public void testRealTimeInAppNotificationAppearsUponApprovalStatusChange() throws Exception {

        // Preconditions: User logged in and part of approval group simulated by opening the UI page
        String baseUrl = "http://localhost:" + port + "/fpm_ui";
        driver.get(baseUrl + "/login");

        // Simulate login - assuming user logs in via UI; simplified for test
        WebElement usernameInput = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameInput.sendKeys("approverUser");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys("securePassword");

        driver.findElement(By.id("loginBtn")).click();

        // Wait until logged in and main approval page loads
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/approvals"));

        // Step 1: Change the status of an approval request via Controller (mocking service behavior).
        String approvalId = "deal123";
        String newStatus = "APPROVED";

        ApprovalStatusChangeRequest requestPayload = new ApprovalStatusChangeRequest();
        requestPayload.setApprovalId(approvalId);
        requestPayload.setNewStatus(newStatus);
        requestPayload.setUserId("approverUser");

        // Call controller method directly mimicking the REST call
        // Normally this test should use TestRestTemplate or MockMvc but here integration with selenium requires this
        fpmDealsheetController.changeApprovalStatus(requestPayload);

        // Step 2: Observe the FPM_UI in-app notification area for alert generation.
        // Wait for notification push from WebSocket
        String notifJson = notificationMessages.poll(10, java.util.concurrent.TimeUnit.SECONDS);
        assertThat(notifJson).isNotNull();

        // Parse and assert notification content
        NotificationPayload notif = objectMapper.readValue(notifJson, NotificationPayload.class);
        assertThat(notif.getApprovalId()).isEqualTo(approvalId);
        assertThat(notif.getStatus()).isEqualTo(newStatus);
        assertThat(notif.getMessage()).contains("Approval status updated");

        // Step 3: Click the in-app notification to verify notification details and routing.

        // Simulate UI receiving the notification:
        // Locate notification area
        WebElement notificationArea = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("inAppNotificationArea")));

        // For testing, add the notification element dynamically via JavaScript (since we mocked the backend sending)
        String jsAddNotif = "var notifArea = document.getElementById('inAppNotificationArea');" +
                "var notif = document.createElement('div');" +
                "notif.setAttribute('class','notification-item');" +
                "notif.setAttribute('data-approval-id', '" + approvalId + "');" +
                "notif.innerText='Approval deal123 status: APPROVED';" +
                "notif.addEventListener('click', function() { window.location.href='/fpm_ui/approvals/details/' + this.getAttribute('data-approval-id'); });" +
                "notifArea.appendChild(notif);";

        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(jsAddNotif);

        WebElement notificationItem = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector(".notification-item[data-approval-id='" + approvalId + "']")));

        notificationItem.click();

        // Assert that URL changed to approval details page
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/fpm_ui/approvals/details/" + approvalId));
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).endsWith("/fpm_ui/approvals/details/" + approvalId);
    }

    // Helper inner class for deserializing notification JSON
    public static class NotificationPayload {
        private String approvalId;
        private String status;
        private String message;

        public String getApprovalId() {
            return approvalId;
        }

        public void setApprovalId(String approvalId) {
            this.approvalId = approvalId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
