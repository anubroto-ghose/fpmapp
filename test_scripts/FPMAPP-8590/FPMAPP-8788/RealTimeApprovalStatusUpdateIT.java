/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8788
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:12:45
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.services.FpmCommonController;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RealTimeApprovalStatusUpdateIT {

    private static WebDriver driver;
    private static WebSocketStompClient stompClient;
    private static StompSession stompSession;

    private static final String WS_URI = "ws://localhost:8080/fpmapp-websocket";
    private static final String APPROVAL_STATUS_TOPIC = "/topic/approval-status";

    private static final CountDownLatch latch = new CountDownLatch(1);
    private static final AtomicReference<String> receivedApprovalStatus = new AtomicReference<>();

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeAll
    public static void setup() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Setup WebSocket STOMP client
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        try {
            stompSession = stompClient.connect(WS_URI, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {
            }).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to WebSocket server", e);
        }
    }

    @AfterAll
    public static void tearDown() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testRealTimeApprovalStatusUpdateWithoutFullPageRefresh() throws Exception {
        // Preconditions: User is logged in and WebSocket connection is active
        driver.get("http://localhost:8080/fpmapp/login");

        // Simulate login
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for redirect to main page
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/fpmapp/home"));

        // Navigate to ApprovalAuditTrailView component page
        driver.get("http://localhost:8080/fpmapp/approval-audit-trail");

        // Subscribe to approval status updates via WebSocket
        stompSession.subscribe(APPROVAL_STATUS_TOPIC, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ApprovalStatusUpdate.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                ApprovalStatusUpdate update = (ApprovalStatusUpdate) payload;
                receivedApprovalStatus.set(update.getStatus());
                latch.countDown();
            }
        });

        // Mock backend service to simulate approval status change triggered by another user
        String approvalRequestId = "12345";
        String newStatus = "APPROVED";

        Mockito.when(fpmDealsheetController.getApprovalStatus(approvalRequestId)).thenReturn(newStatus);

        // Simulate backend sending approval status update via WebSocket
        ApprovalStatusUpdate simulatedUpdate = new ApprovalStatusUpdate(approvalRequestId, newStatus);
        stompSession.send(APPROVAL_STATUS_TOPIC, simulatedUpdate);

        // Wait for the UI to receive the update (max 5 seconds)
        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);
        assertThat(messageReceived).as("Approval status update received via WebSocket").isTrue();

        // Verify UI component updated without full page reload
        WebElement approvalStatusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatus-" + approvalRequestId)));

        String displayedStatus = approvalStatusElement.getText();
        assertThat(displayedStatus).isEqualToIgnoringCase(newStatus);

        // Verify no page reload occurred
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("/approval-audit-trail");

        // Verify no error messages displayed
        boolean errorPresent = driver.findElements(By.className("error-message")).size() > 0;
        assertThat(errorPresent).isFalse();
    }

    // DTO for approval status update message
    public static class ApprovalStatusUpdate {
        private String approvalRequestId;
        private String status;

        public ApprovalStatusUpdate() {
        }

        public ApprovalStatusUpdate(String approvalRequestId, String status) {
            this.approvalRequestId = approvalRequestId;
            this.status = status;
        }

        public String getApprovalRequestId() {
            return approvalRequestId;
        }

        public void setApprovalRequestId(String approvalRequestId) {
            this.approvalRequestId = approvalRequestId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
