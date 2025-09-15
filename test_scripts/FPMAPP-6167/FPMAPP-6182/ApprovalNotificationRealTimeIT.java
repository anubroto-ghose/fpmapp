/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6182
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:10:41
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.services.FpmCommonController;

import java.lang.reflect.Type;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@Import({FpmDealsheetController.class, FpmCommonController.class})
public class ApprovalNotificationRealTimeIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebSocketStompClient stompClient;
    private static StompSession stompSession;

    @Autowired
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmCommonController fpmCommonController; // Mock service for approval status triggers

    private static final String WEBSOCKET_ENDPOINT = "/ws/approvals";
    private static final String TOPIC_APPROVAL_UPDATES = "/topic/approval-status";
    private static final ObjectMapper mapper = new ObjectMapper();

    private BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

    @BeforeAll
    public static void setup() throws Exception {
        // Setup Selenium ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Setup WebSocket stomp client
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    public void givenUserLoggedIn_whenApprovalStatusChanges_thenRealTimeNotificationDisplayed() throws Exception {
        // Mock backend service to respond with success on approval update
        Mockito.doNothing().when(fpmCommonController).notifyApprovalStatusChange(Mockito.anyLong(), Mockito.anyString());

        // Connect to websocket endpoint
        stompSession = stompClient.connect(
            new URI(String.format("ws://localhost:%d%s",  port, WEBSOCKET_ENDPOINT)),
            new WebSocketHttpHeaders(),
            new StompSessionHandlerAdapter() {}
        ).get();

        // Subscribe to the approval status topic to capture notification
        stompSession.subscribe(TOPIC_APPROVAL_UPDATES, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.offer((String) payload);
            }
        });

        // 1. Simulate user login in UI
        driver.get("http://localhost:" + port + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        usernameInput.sendKeys("alice.bankuser");
        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys("SecurePass123");
        WebElement loginBtn = driver.findElement(By.id("loginButton"));
        loginBtn.click();

        // Wait for redirection to dashboard or main approvals page
        Thread.sleep(2000);

        // Verify user is logged in by checking presence of username display or logout button
        WebElement userLabel = driver.findElement(By.id("loggedInUser"));
        assertThat(userLabel.getText(), containsString("alice.bankuser"));

        // Assuming notification area has id notificationArea
        WebElement notificationArea = driver.findElement(By.id("notificationArea"));

        // 2. Trigger approval status change via mocked controller method or REST API
        long approvalId = 1001L;
        String newStatus = "Approved";

        // We simulate server-side event trigger by invoking controller method
        dealsheetController.triggerApprovalStatusChange(approvalId, newStatus);

        // 3. Wait for notification WebSocket message
        String notificationJson = blockingQueue.poll(Duration.ofSeconds(5).toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
        assertThat("Notification received should not be null", notificationJson, notNullValue());

        // 4. Validate notification JSON details
        NotificationPayload notification = mapper.readValue(notificationJson, NotificationPayload.class);
        assertTrue(notification.getApprovalId() == approvalId,"Approval ID should match in notification");
        assertTrue(notification.getStatus().equalsIgnoreCase(newStatus),"Status in notification should be 'Approved'");

        // 5. Validate notification appears in UI without page refresh
        // Wait up to 5 seconds for notification element to appear inside notification area
        long startTime = System.currentTimeMillis();
        WebElement notifElement = null;
        while (System.currentTimeMillis() - startTime < 5000) {
            try {
                notifElement = notificationArea.findElement(By.cssSelector(".approval-notification[data-approval-id='" + approvalId + "']"));
                if (notifElement.isDisplayed()) {
                    break;
                }
            } catch (Exception e) {
                Thread.sleep(200);
            }
        }
        assertThat("Notification element displayed in UI", notifElement, notNullValue());

        // 6. Check notification content correctness
        String notifText = notifElement.getText();
        assertTrue(notifText.contains("Approval ID: 1001"), "Notification text must contain approval ID");
        assertTrue(notifText.contains("Status: Approved"), "Notification text must contain updated status");

        // 7. User can dismiss notification
        WebElement dismissBtn = notifElement.findElement(By.cssSelector(".dismiss-button"));
        dismissBtn.click();

        // Allow time for dismissal animation
        Thread.sleep(500);

        // Notification should be no longer visible
        assertTrue(!notifElement.isDisplayed(), "Notification should be dismissed and hidden after clicking dismiss");
    }

    // DTO to map notification JSON payload
    public static class NotificationPayload {
        private long approvalId;
        private String status;
        private String message;

        public long getApprovalId() {
            return approvalId;
        }

        public void setApprovalId(long approvalId) {
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
