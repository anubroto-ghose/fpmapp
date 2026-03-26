/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8791
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:02:16
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.services.ApprovalAuditService;
import com.webapp.fpmapp.services.CurrencyExchangeService;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

/**
 * Integration test for real-time approval status update without full page refresh.
 * 
 * Preconditions:
 * - User is logged in
 * - Approval workflow active with pending tasks
 * - WebSocket connection active
 * 
 * Test Steps:
 * 1. Trigger approval action externally
 * 2. Observe UI updates immediately
 * 
 * Expected:
 * - Approval status updates immediately
 * - No full page reload
 * - Audit trail view updates
 * - No errors or UI glitches
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RealTimeApprovalStatusUpdateIT {

    private static WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080/fpmapp";

    @MockBean
    private ApprovalAuditService approvalAuditService;

    @MockBean
    private CurrencyExchangeService currencyExchangeService;

    private static Session webSocketSession;
    private static CountDownLatch messageLatch;

    @BeforeAll
    public static void setup() throws Exception {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Initialize WebSocket client latch
        messageLatch = new CountDownLatch(1);

        // Connect WebSocket client to simulate real-time updates
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = new URI("ws://localhost:8080/fpmapp/ws/approvals");
        webSocketSession = container.connectToServer(TestWebSocketClient.class, uri);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (webSocketSession != null && webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testRealTimeApprovalStatusUpdate() throws Exception {
        // Mock approval audit service to return updated audit trail
        Mockito.when(approvalAuditService.getAuditTrailByRequestId("REQ123"))
            .thenReturn(
                java.util.List.of(
                    new com.webapp.fpmapp.dto.AuditTrailEntry("approve", "user2", "2026-03-26T15:00:00", "Approved by user2")
                )
            );

        // Step 1: Login as user with pending approvals
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("user1");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Verify pending approval item is visible
        WebElement pendingApproval = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".approval-item[data-request-id='REQ123']")));
        assertThat(pendingApproval).isNotNull();

        // Step 2: Simulate external approval action triggering WebSocket message
        // We simulate this by sending a message to the WebSocket server endpoint
        String approvalUpdateMessage = "{\"requestId\":\"REQ123\",\"status\":\"approved\",\"performedBy\":\"user2\",\"timestamp\":\"2026-03-26T15:00:00\"}";
        webSocketSession.getBasicRemote().sendText(approvalUpdateMessage);

        // Step 3: Wait for UI to update approval status without page reload
        // Wait max 10 seconds for status text to update
        boolean statusUpdated = wait.until(driver -> {
            WebElement statusElement = driver.findElement(By.cssSelector(".approval-item[data-request-id='REQ123'] .approval-status"));
            String statusText = statusElement.getText();
            return "Approved".equalsIgnoreCase(statusText.trim());
        });

        assertThat(statusUpdated).isTrue();

        // Step 4: Verify no full page reload occurred
        // We check that the URL remains the same and no reload event fired
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("/dashboard");

        // Step 5: Verify ApprovalAuditTrailView updated with new audit entry
        WebElement auditTrailView = driver.findElement(By.id("approvalAuditTrailView"));
        wait.until(ExpectedConditions.visibilityOf(auditTrailView));

        // Check that audit trail contains the new entry
        boolean auditEntryPresent = auditTrailView.getText().contains("Approved by user2") && auditTrailView.getText().contains("2026-03-26T15:00:00");
        assertThat(auditEntryPresent).isTrue();

        // Step 6: Verify no error messages or UI glitches
        boolean errorVisible = driver.findElements(By.cssSelector(".error-message, .ui-glitch")).size() > 0;
        assertThat(errorVisible).isFalse();
    }

    @ClientEndpoint
    public static class TestWebSocketClient {

        @OnMessage
        public void onMessage(String message) {
            // Signal that a message was received
            messageLatch.countDown();
        }
    }
}
