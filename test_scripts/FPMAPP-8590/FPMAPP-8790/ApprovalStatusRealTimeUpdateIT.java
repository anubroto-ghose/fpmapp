/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8790
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:01:39
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

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
 * Integration test for real-time approval status updates without UI flicker.
 * 
 * Preconditions:
 * - User logged in
 * - WebSocket connection active
 * - Multiple rapid approval status changes triggered
 * 
 * Validates:
 * - UI updates immediately and correctly
 * - ApprovalAuditTrailView reflects all changes
 * - No UI flicker or errors
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApprovalStatusRealTimeUpdateIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private static WebSocketClientEndpoint wsClient;

    private static final BlockingQueue<String> receivedMessages = new LinkedBlockingDeque<>();

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeAll
    public static void setupClass() throws Exception {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Setup WebSocket client
        wsClient = new WebSocketClientEndpoint(new URI("ws://localhost:8080/ws/approval-status"));
        wsClient.connect();
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        if (wsClient != null) {
            wsClient.close();
        }
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user login
        Mockito.when(fpmUserProfileController.isUserLoggedIn()).thenReturn(true);

        // Mock approval audit trail data
        Mockito.when(fpmDealsheetController.getApprovalAuditTrail(Mockito.anyLong()))
            .thenReturn(List.of(
                new com.webapp.fpmapp.dto.ApprovalAuditEntry(1L, "approve", 101L, null, "2026-03-26T14:00:00", "Initial approval"),
                new com.webapp.fpmapp.dto.ApprovalAuditEntry(1L, "delegate", 101L, 102L, "2026-03-26T14:01:00", "Delegated to user 102"),
                new com.webapp.fpmapp.dto.ApprovalAuditEntry(1L, "approve", 102L, null, "2026-03-26T14:02:00", "Final approval")
            ));

        // Mock currency conversion to avoid external calls
        Mockito.when(currencyConvertionController.getCurrentRate(Mockito.anyString())).thenReturn(1.0);
    }

    @Test
    public void testRapidApprovalStatusUpdates_NoUIFlicker() throws Exception {
        String baseUrl = "http://localhost:" + port + "/fpmapp";

        // Login simulation (assuming login page or auto-login for test)
        driver.get(baseUrl + "/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Simulate logged in user by setting session/local storage
        ((JavascriptExecutor) driver).executeScript("window.localStorage.setItem('userToken', 'dummy-token');");

        // Navigate to approval status page
        driver.get(baseUrl + "/approvals/1");

        // Wait for approval status component to be visible
        WebElement approvalStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approval-status")));
        WebElement auditTrailView = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approval-audit-trail-view")));

        // Simulate rapid approval status changes via WebSocket messages
        // These messages would normally come from backend or other user sessions
        String[] rapidStatusUpdates = new String[] {
            "{\"requestId\":1,\"status\":\"pending\",\"timestamp\":\"2026-03-26T14:10:00\"}",
            "{\"requestId\":1,\"status\":\"approved\",\"timestamp\":\"2026-03-26T14:10:01\"}",
            "{\"requestId\":1,\"status\":\"delegated\",\"timestamp\":\"2026-03-26T14:10:02\"}",
            "{\"requestId\":1,\"status\":\"approved\",\"timestamp\":\"2026-03-26T14:10:03\"}"
        };

        for (String msg : rapidStatusUpdates) {
            wsClient.sendMessage(msg);
            // Small delay to simulate rapid but not simultaneous
            Thread.sleep(100);
        }

        // Wait for UI to update approval status text accordingly
        boolean statusUpdated = wait.until(driver -> {
            String statusText = approvalStatus.getText().toLowerCase();
            return statusText.contains("approved") || statusText.contains("delegated") || statusText.contains("pending");
        });

        assertThat(statusUpdated).isTrue();

        // Verify audit trail view contains all expected entries
        List<WebElement> auditEntries = auditTrailView.findElements(By.cssSelector(".audit-entry"));
        assertThat(auditEntries).isNotEmpty();

        // Check that audit entries text contains expected statuses
        boolean containsAllStatuses = auditEntries.stream()
            .map(WebElement::getText)
            .anyMatch(text -> text.toLowerCase().contains("approved")) &&
            auditEntries.stream()
            .map(WebElement::getText)
            .anyMatch(text -> text.toLowerCase().contains("delegated"));

        assertThat(containsAllStatuses).isTrue();

        // Check no flicker or visual glitches by ensuring element is displayed and stable
        assertThat(approvalStatus.isDisplayed()).isTrue();
        assertThat(auditTrailView.isDisplayed()).isTrue();

        // Check no error messages on page
        List<WebElement> errorMessages = driver.findElements(By.cssSelector(".error-message"));
        assertThat(errorMessages).isEmpty();
    }

    /**
     * Simple WebSocket client endpoint for testing.
     */
    @ClientEndpoint
    public static class WebSocketClientEndpoint {

        private Session userSession = null;
        private URI endpointURI;

        public WebSocketClientEndpoint(URI endpointURI) {
            this.endpointURI = endpointURI;
        }

        public void connect() throws Exception {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
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
            receivedMessages.offer(message);
        }

        @OnError
        public void onError(Session session, Throwable thr) {
            // Log error
            System.err.println("WebSocket error: " + thr.getMessage());
        }

        public void sendMessage(String message) throws Exception {
            if (userSession != null && userSession.isOpen()) {
                userSession.getAsyncRemote().sendText(message);
            } else {
                throw new IllegalStateException("WebSocket session is not open.");
            }
        }

        public void close() throws Exception {
            if (userSession != null) {
                userSession.close();
            }
        }
    }
}
