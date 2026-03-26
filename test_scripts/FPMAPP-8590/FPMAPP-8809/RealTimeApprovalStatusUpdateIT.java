/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8809
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:43:55
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.services.FpmCommonController;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Integration test for real-time approval status update reflected in UI immediately.
 * 
 * Preconditions:
 * - User logged in with approval permissions.
 * - WebSocket connection established.
 * - Approval action triggered by another user.
 * 
 * This test uses Selenium WebDriver to simulate UI interaction and Spring Boot test context
 * with mocked services to simulate backend approval action and WebSocket message broadcasting.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RealTimeApprovalStatusUpdateIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:";

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private ApprovalWebSocketTestHelper webSocketTestHelper;

    private static final String TEST_APPROVAL_ID = "12345";

    private static final String PENDING_STATUS = "Pending";
    private static final String APPROVED_STATUS = "Approved";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
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
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock initial approval status as Pending
        when(fpmDealsheetController.getApprovalStatus(Long.parseLong(TEST_APPROVAL_ID))).thenReturn(PENDING_STATUS);

        // Mock user permissions
        when(fpmCommonController.hasApprovalPermission("testUser")).thenReturn(true);
    }

    /**
     * Test that approval status updates in real-time on the UI without page refresh.
     * 
     * Steps:
     * 1. Load approval page with pending status.
     * 2. Simulate approval action from another user via WebSocket message.
     * 3. Verify UI updates approval status immediately.
     */
    @Test
    public void testRealTimeApprovalStatusUpdate() throws Exception {
        // Step 1: User logs in and navigates to approval page
        driver.get(BASE_URL + port + "/approval/request/" + TEST_APPROVAL_ID);

        // Wait for page to load and show pending status
        WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approval-status")));
        assertThat(statusElement.getText()).isEqualTo(PENDING_STATUS);

        // Step 2: Establish WebSocket connection to listen for approval status updates
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> receivedStatus = new AtomicReference<>();

        webSocketTestHelper.connectAndSubscribe("/topic/approval-status/" + TEST_APPROVAL_ID, message -> {
            receivedStatus.set(message);
            latch.countDown();
        });

        // Step 3: Simulate approval action triggered by another user
        // Mock backend service to return approved status after action
        when(fpmDealsheetController.getApprovalStatus(Long.parseLong(TEST_APPROVAL_ID))).thenReturn(APPROVED_STATUS);

        // Simulate sending WebSocket message to clients
        webSocketTestHelper.sendApprovalStatusUpdate(TEST_APPROVAL_ID, APPROVED_STATUS);

        // Wait for WebSocket message to be received
        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);
        assertThat(messageReceived).isTrue();
        assertThat(receivedStatus.get()).isEqualTo(APPROVED_STATUS);

        // Step 4: Verify UI updates approval status without page refresh
        // Wait until the status element text updates to approved
        wait.until(ExpectedConditions.textToBe(By.id("approval-status"), APPROVED_STATUS));

        WebElement updatedStatusElement = driver.findElement(By.id("approval-status"));
        assertThat(updatedStatusElement.getText()).isEqualTo(APPROVED_STATUS);

        // Verify UI remains responsive by checking presence of approve/reject buttons disabled
        WebElement approveButton = driver.findElement(By.id("btn-approve"));
        WebElement rejectButton = driver.findElement(By.id("btn-reject"));

        // After approval, buttons should be disabled
        assertThat(approveButton.isEnabled()).isFalse();
        assertThat(rejectButton.isEnabled()).isFalse();
    }

    /**
     * Helper configuration and bean for WebSocket testing.
     */
    @Configuration
    static class TestConfig {

        @Bean
        public ApprovalWebSocketTestHelper approvalWebSocketTestHelper() {
            return new ApprovalWebSocketTestHelper();
        }
    }

    /**
     * Helper class to manage WebSocket STOMP client connection and messaging for tests.
     */
    public static class ApprovalWebSocketTestHelper {

        private WebSocketStompClient stompClient;
        private StompSession stompSession;

        public ApprovalWebSocketTestHelper() {
            this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
            this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        }

        /**
         * Connects to WebSocket endpoint and subscribes to a topic.
         * @param topic the topic to subscribe
         * @param messageHandler callback to handle received messages
         * @throws Exception if connection fails
         */
        public void connectAndSubscribe(String topic, java.util.function.Consumer<String> messageHandler) throws Exception {
            stompSession = stompClient.connect(new URI("ws://localhost:8080/ws-endpoint"), new StompSessionHandlerAdapter() {}).get(3, TimeUnit.SECONDS);

            stompSession.subscribe(topic, new StompFrameHandler() {

                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return String.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    messageHandler.accept((String) payload);
                }
            });
        }

        /**
         * Simulates sending an approval status update message to subscribed clients.
         * @param approvalId the approval request ID
         * @param status the new approval status
         */
        public void sendApprovalStatusUpdate(String approvalId, String status) {
            if (stompSession != null && stompSession.isConnected()) {
                String destination = "/topic/approval-status/" + approvalId;
                stompSession.send(destination, status.getBytes());
            }
        }

        /**
         * Disconnects the WebSocket session.
         */
        public void disconnect() {
            if (stompSession != null && stompSession.isConnected()) {
                stompSession.disconnect();
            }
        }
    }
}
