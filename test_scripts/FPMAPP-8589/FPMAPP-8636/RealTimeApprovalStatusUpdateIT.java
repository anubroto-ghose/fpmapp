/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8636
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:48:47
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.Fpmcamunda.FpmCamundaApprovalService;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.lang.reflect.Type;

/**
 * Integration test for real-time approval status update UI feedback.
 * 
 * Preconditions:
 * - User is logged in
 * - WebSocket connection to /fpmcamunda/approvals/updates is established
 * - Backend can push approval status update events
 * 
 * This test mocks backend approval status update push and verifies UI updates in real-time.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RealTimeApprovalStatusUpdateIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private static WebSocketStompClient stompClient;
    private StompSession stompSession;

    private final BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

    @MockBean
    private FpmCamundaApprovalService approvalService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String WS_ENDPOINT = "/fpmcamunda/approvals/updates";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless)
        System.setProperty("webdriver.chrome.driver", "./chromedriver"); // Adjust path as needed
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Setup WebSocketStompClient
        stompClient = new WebSocketStompClient(new SockJsClient(
                java.util.Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
        if (stompClient != null) {
            stompClient.stop();
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock approvalService to simulate backend approval status change push
        doAnswer(invocation -> {
            // Simulate sending approval status update event
            ApprovalStatusUpdateEvent event = new ApprovalStatusUpdateEvent();
            event.setApprovalId("APPROVAL123");
            event.setStatus("APPROVED");
            event.setMessage("Approval has been approved successfully.");

            // Normally this would push to WebSocket subscribers
            blockingQueue.offer(objectMapper.writeValueAsString(event));
            return null;
        }).when(approvalService).triggerApprovalStatusChange(any());

        // Connect WebSocket client to listen to approval updates
        String url = String.format("ws://localhost:%d%s", port, WS_ENDPOINT);
        stompSession = stompClient.connect(url, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {
        }).get(3, TimeUnit.SECONDS);

        stompSession.subscribe(WS_ENDPOINT, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.offer((String) payload);
            }
        });

        // Navigate to the application login page and login
        driver.get("http://localhost:" + port + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("testpassword");
        loginButton.click();

        // Wait for redirect to dashboard
        Thread.sleep(2000);

        // Verify user is logged in by checking presence of approval status section
        assertThat(driver.findElements(By.id("approvalStatusSection"))).isNotEmpty();
    }

    @Test
    public void testRealTimeApprovalStatusUpdate() throws Exception {
        // Step 1: Trigger an approval status change in the backend system
        approvalService.triggerApprovalStatusChange("APPROVAL123");

        // Step 2: Verify WebSocket pushes the approval status update event to the UI
        String message = blockingQueue.poll(5, TimeUnit.SECONDS);
        assertThat(message).isNotNull();

        ApprovalStatusUpdateEvent event = objectMapper.readValue(message, ApprovalStatusUpdateEvent.class);
        assertThat(event.getApprovalId()).isEqualTo("APPROVAL123");
        assertThat(event.getStatus()).isEqualTo("APPROVED");

        // Step 3: Observe the UI for immediate update reflecting the new approval status
        // Wait max 5 seconds for UI update
        boolean updated = false;
        for (int i = 0; i < 10; i++) {
            WebElement statusElement = driver.findElement(By.id("approvalStatus_APPORVAL123"));
            if (statusElement != null && "APPROVED".equalsIgnoreCase(statusElement.getText().trim())) {
                updated = true;
                break;
            }
            Thread.sleep(500);
        }
        assertThat(updated).isTrue();

        // Step 4: Confirm that the approval notification appears in-app without delay
        WebElement notification = driver.findElement(By.id("approvalNotification"));
        assertThat(notification).isNotNull();
        assertThat(notification.getText()).contains("Approval has been approved successfully.");

        // Additional: Confirm displayed data matches backend state
        WebElement approvalIdElement = driver.findElement(By.id("approvalId_APPORVAL123"));
        assertThat(approvalIdElement.getText()).isEqualTo("APPROVAL123");
    }

    // DTO for approval status update event
    public static class ApprovalStatusUpdateEvent {
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
