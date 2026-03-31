/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8926
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 15:03:39
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
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

/**
 * Integration test validating real-time UI updates via WebSocket for approval request status.
 * 
 * Preconditions:
 * - User is logged in
 * - WebSocket connection is active
 * - Approval requests are visible
 * 
 * Test Steps:
 * 1. Trigger status update from mocked backend
 * 2. Observe UI updates in real-time
 * 3. Verify no stale/conflicting data
 * 4. Verify WebSocket stability
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalRequestRealTimeUpdateTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";

    private static final String APPROVAL_REQUEST_ID = "AR-1001";

    private static TestWebSocketClient webSocketClient;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user profile to simulate logged-in user
        when(userProfileController.getCurrentUser()).thenReturn(
                new com.webapp.fpmapp.entities.User(1L, USERNAME, "Test User", "testuser@example.com"));

        // Mock currency conversion to return fixed rate
        when(currencyConvertionController.convert(any(), any(), any())).thenReturn(1.0);

        // Mock forecast and common controller responses as needed
        when(fpmForecastController.getForecastSummary(any())).thenReturn("Forecast Summary Data");
        when(fpmCommonController.getApprovalRequestStatus(APPROVAL_REQUEST_ID)).thenReturn("Pending");
    }

    @Test
    public void testRealTimeApprovalRequestStatusUpdate() throws Exception {
        // Step 0: Navigate to login page and login
        driver.get("http://localhost:" + port + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys(USERNAME);
        passwordInput.sendKeys(PASSWORD);
        loginButton.click();

        // Wait for dashboard or approval requests page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 1: Verify approval requests are visible
        WebElement approvalRequestList = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("approvalRequestList")));

        WebElement approvalRequestItem = approvalRequestList.findElement(By.cssSelector("li[data-approval-id='" + APPROVAL_REQUEST_ID + "']"));
        assertThat(approvalRequestItem).isNotNull();

        // Verify initial status is 'Pending'
        WebElement statusElement = approvalRequestItem.findElement(By.className("approval-status"));
        assertThat(statusElement.getText()).isEqualToIgnoringCase("Pending");

        // Step 2: Establish WebSocket connection
        String wsUri = "ws://localhost:" + port + "/ws/approval-updates?user=" + USERNAME;
        webSocketClient = new TestWebSocketClient(new URI(wsUri));
        webSocketClient.connectBlocking(5, TimeUnit.SECONDS);
        assertThat(webSocketClient.isOpen()).isTrue();

        // Step 3: Simulate backend sending a status update message
        // This simulates another user or system triggering a status update
        String newStatus = "Approved";
        String statusUpdateJson = String.format(
                "{\"approvalRequestId\":\"%s\", \"newStatus\":\"%s\", \"history\":[{\"status\":\"Pending\",\"timestamp\":\"2024-06-01T10:00:00Z\"},{\"status\":\"Approved\",\"timestamp\":\"2024-06-01T10:05:00Z\"}]}"
                , APPROVAL_REQUEST_ID, newStatus);

        // Inject the message into the client as if received from server
        webSocketClient.simulateIncomingMessage(statusUpdateJson);

        // Step 4: Wait and verify UI updates in real-time
        wait.until(ExpectedConditions.textToBePresentInElement(statusElement, newStatus));
        assertThat(statusElement.getText()).isEqualToIgnoringCase(newStatus);

        // Verify status history updated and visible
        WebElement historyElement = approvalRequestItem.findElement(By.className("approval-status-history"));
        wait.until(ExpectedConditions.visibilityOf(historyElement));
        String historyText = historyElement.getText();
        assertThat(historyText).contains("Pending").contains("Approved");

        // Step 5: Verify no stale or conflicting data
        // Check that only one status is shown as current
        long currentStatusCount = approvalRequestItem.findElements(By.className("approval-status-current")).stream().count();
        assertThat(currentStatusCount).isEqualTo(1);

        // Step 6: Verify WebSocket connection remains stable
        assertThat(webSocketClient.isOpen()).isTrue();

        // Close WebSocket
        webSocketClient.close();
    }

    /**
     * Simple WebSocket client for testing real-time updates.
     * Uses Jakarta WebSocket API.
     */
    @ClientEndpoint
    public static class TestWebSocketClient {

        private Session userSession = null;
        private final CountDownLatch messageLatch = new CountDownLatch(1);
        private String lastMessage = null;

        public TestWebSocketClient(URI endpointURI) {
            try {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.connectToServer(this, endpointURI);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @OnOpen
        public void onOpen(Session userSession) {
            this.userSession = userSession;
        }

        @OnClose
        public void onClose(Session userSession, CloseReason reason) {
            this.userSession = null;
        }

        @OnMessage
        public void onMessage(String message) {
            this.lastMessage = message;
            messageLatch.countDown();
        }

        @OnError
        public void onError(Session session, Throwable thr) {
            // Log error
            System.err.println("WebSocket error: " + thr.getMessage());
        }

        public boolean isOpen() {
            return this.userSession != null && this.userSession.isOpen();
        }

        public void connectBlocking(long timeout, TimeUnit unit) throws InterruptedException {
            // Wait for connection to open
            long waited = 0;
            while (!isOpen() && waited < unit.toMillis(timeout)) {
                Thread.sleep(100);
                waited += 100;
            }
            if (!isOpen()) {
                throw new RuntimeException("WebSocket connection failed to open within timeout");
            }
        }

        public void simulateIncomingMessage(String message) {
            // Directly call onMessage to simulate server push
            onMessage(message);

            // Also execute JS in browser to simulate UI update triggered by WS message
            if (driver instanceof JavascriptExecutor) {
                String script = "var event = new CustomEvent('approvalStatusUpdate', { detail: " + message + " });"
                        + "document.dispatchEvent(event);";
                ((JavascriptExecutor) driver).executeScript(script);
            }
        }

        public void close() throws Exception {
            if (userSession != null) {
                userSession.close();
            }
        }

        public String getLastMessage() {
            return lastMessage;
        }
    }
}
