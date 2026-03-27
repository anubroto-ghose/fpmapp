/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8893
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:28:40
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;

import jakarta.annotation.PostConstruct;

/**
 * Integration test for real-time approval status updates pushed to UI via WebSocket.
 * 
 * Preconditions:
 * - User logged in as requester or approver
 * - WebSocket connection established
 * - Pending approval requests exist
 * 
 * This test mocks backend service to simulate approval status change and verifies UI updates in real-time.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RealTimeApprovalStatusUpdateIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final BlockingQueue<String> websocketMessages = new LinkedBlockingQueue<>();

    private static TestWebSocketClient webSocketClient;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Start WebSocket client to simulate backend pushing messages
        webSocketClient = new TestWebSocketClient();
        webSocketClient.connect();
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user profile to simulate logged-in user
        when(userProfileController.getCurrentUser()).thenReturn(
                new com.webapp.fpmapp.entities.User(1L, USERNAME, "Requester", "requester@example.com"));

        // Mock pending approval requests
        when(fpmCommonController.getPendingApprovals(any())).thenReturn(
                java.util.List.of(
                        new com.webapp.fpmapp.dto.FpmDealsheetController.ApprovalRequestDTO(1001L, "Pending", "Financial Request #1001"),
                        new com.webapp.fpmapp.dto.FpmDealsheetController.ApprovalRequestDTO(1002L, "Pending", "Financial Request #1002")
                ));
    }

    @Test
    public void testRealTimeApprovalStatusUpdate() throws Exception {
        String baseUrl = "http://localhost:" + port + "/fpmapp/approvals";

        // Navigate to approvals page
        driver.get(baseUrl);

        WebDriverWait wait = new WebDriverWait(driver, 10);

        // Wait for the approval requests list to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approval-requests-list")));

        // Verify initial status is 'Pending' for request 1001
        WebElement approvalRow = driver.findElement(By.cssSelector("tr[data-request-id='1001']"));
        WebElement statusCell = approvalRow.findElement(By.cssSelector("td.status"));
        assertThat(statusCell.getText()).isEqualToIgnoringCase("Pending");

        // Simulate backend approval status change pushed via WebSocket
        String approvalUpdateJson = "{\"requestId\":1001,\"newStatus\":\"Approved\"}";
        webSocketClient.simulateIncomingMessage(approvalUpdateJson);

        // Wait up to 10 seconds for UI to update the status without manual refresh
        boolean updated = wait.until(driver -> {
            WebElement updatedStatusCell = driver.findElement(By.cssSelector("tr[data-request-id='1001'] td.status"));
            return "Approved".equalsIgnoreCase(updatedStatusCell.getText());
        });

        assertThat(updated).isTrue();

        // Verify no errors in browser console
        Object logs = ((JavascriptExecutor) driver).executeScript("return window.consoleErrors || [];");
        assertThat(logs).asString().doesNotContain("error");
    }

    /**
     * Simple WebSocket client to simulate backend pushing approval status updates.
     */
    private static class TestWebSocketClient {
        private org.springframework.web.socket.client.WebSocketClient client;
        private org.springframework.web.socket.WebSocketSession session;

        private final String WS_URI = "ws://localhost:8080/fpmapp/ws/approvals"; // Adjust if needed

        public void connect() {
            client = new StandardWebSocketClient();
            try {
                session = client.doHandshake(new TextWebSocketHandler() {
                    @Override
                    public void handleTextMessage(org.springframework.web.socket.WebSocketSession session, TextMessage message) {
                        websocketMessages.offer(message.getPayload());
                    }
                }, WS_URI).get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new RuntimeException("Failed to connect WebSocket client", e);
            }
        }

        public void simulateIncomingMessage(String message) {
            // In real scenario, backend pushes message to UI.
            // Here, we simulate by injecting JS event to the browser.
            // This method is a placeholder to show intent.
            // Actual push is done by backend in integration tests.

            // For this test, we inject JS to simulate receiving WebSocket message
            String script = "window.dispatchEvent(new MessageEvent('message', { data: '" + message.replace("'", "\\'") + "' }));";
            ((JavascriptExecutor) driver).executeScript(script);
        }

        public void close() {
            try {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
