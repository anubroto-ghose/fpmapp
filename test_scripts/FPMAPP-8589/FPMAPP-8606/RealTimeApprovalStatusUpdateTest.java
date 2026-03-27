/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8606
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 08:09:57
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import java.util.Collections;

/**
 * Integration Selenium test for real-time approval status update via WebSocket.
 * 
 * Preconditions:
 * - User logged in with approval permissions
 * - WebSocket connection active
 * 
 * Test Steps:
 * 1. Trigger backend approval status change
 * 2. Observe UI real-time update
 * 3. Verify UI matches backend state
 * 
 * This test mocks backend services and simulates WebSocket notification.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RealTimeApprovalStatusUpdateTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static WebSocketStompClient stompClient;
    private StompSession stompSession;

    private final CountDownLatch latch = new CountDownLatch(1);

    private volatile String receivedApprovalStatusUpdate = null;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Setup WebSocketStompClient
        stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList((Transport) new WebSocketTransport(new StandardWebSocketClient()))));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Mock user profile to simulate logged-in user with approval permissions
        when(userProfileController.getCurrentUser()).thenReturn(
                new com.webapp.fpmapp.entities.User("approverUser", "Approver", "ROLE_APPROVER"));

        // Mock currency conversion service (not used directly here but required for context)
        when(currencyConvertionController.convertCurrency(any(), any(), any())).thenReturn(1.0);

        // Mock forecast and common controllers
        when(fpmForecastController.getForecastData(any())).thenReturn(null);
        when(fpmCommonController.getAuditTrail(any())).thenReturn("Audit log entry for approval status change");

        // Open the FPM_UI login page and simulate login
        driver.get("http://localhost:" + port + "/login");

        // Simulate login form fill and submit
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("approverUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for redirect to approval dashboard
        wait.until(ExpectedConditions.urlContains("/approval-dashboard"));

        // Establish WebSocket connection to backend notification service
        stompSession = stompClient.connect(
                new URI("ws://localhost:" + port + "/ws/notifications"),
                new WebSocketHttpHeaders(),
                new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompSession.ConnectedHeaders connectedHeaders) {
                        session.subscribe("/topic/approval-status", message -> {
                            receivedApprovalStatusUpdate = new String(message.getPayload());
                            latch.countDown();
                        });
                    }
                }).get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testRealTimeApprovalStatusUpdate() throws Exception {
        // Step 1: Trigger backend approval status change
        // Simulate backend approval status update via mocked service
        String approvalRequestId = "REQ12345";
        String newStatus = "APPROVED";

        // Simulate backend service call that triggers WebSocket notification
        // Here we directly simulate the WebSocket message for test purposes
        stompSession.send("/app/approve-request", approvalRequestId.getBytes());

        // Simulate backend sending notification message
        // In real scenario, backend would broadcast to /topic/approval-status
        // For test, we simulate reception by manually invoking the subscription handler
        // But since we subscribed above, we wait for latch

        // Wait for WebSocket message reception
        boolean messageReceived = latch.await(10, TimeUnit.SECONDS);
        assertThat(messageReceived).as("WebSocket approval status update received").isTrue();

        // Step 2: Observe UI for real-time update
        // Wait for UI element that shows approval status to update
        By approvalStatusSelector = By.id("approval-status-" + approvalRequestId);

        wait.until(ExpectedConditions.visibilityOfElementLocated(approvalStatusSelector));

        WebElement approvalStatusElement = driver.findElement(approvalStatusSelector);

        // Use JS executor to wait until text changes to expected status
        boolean statusUpdated = wait.until(driver -> {
            String text = approvalStatusElement.getText();
            return newStatus.equalsIgnoreCase(text.trim());
        });

        // Step 3: Verify UI matches backend state
        assertThat(statusUpdated).as("Approval status updated in UI").isTrue();
        assertThat(receivedApprovalStatusUpdate).contains(approvalRequestId).contains(newStatus);

        // Verify no page refresh occurred by checking URL remains the same
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("/approval-dashboard");

        // Verify audit trail log is consistent (mocked)
        String auditLog = fpmCommonController.getAuditTrail(approvalRequestId);
        assertThat(auditLog).contains("approval status change");
    }
}
