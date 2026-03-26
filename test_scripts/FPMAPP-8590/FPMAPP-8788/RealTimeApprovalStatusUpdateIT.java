/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8788
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:28:15
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

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

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.services.AuditTrailService;
import com.webapp.fpmapp.services.CurrencySyncService;

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
 * Integration test for real-time approval status update without full page refresh.
 * 
 * Preconditions:
 * - User logged in
 * - WebSocket connection active
 * - Approval action triggered externally
 * 
 * Validates immediate UI update and audit trail view update.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RealTimeApprovalStatusUpdateIT {

    private static WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080";

    private static Session webSocketSession;
    private static CountDownLatch messageLatch;

    @MockBean
    private AuditTrailService auditTrailService;

    @MockBean
    private CurrencySyncService currencySyncService;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @BeforeAll
    public static void setup() throws Exception {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Open the application login page
        driver.get(BASE_URL + "/login");

        // Perform login
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("TestPassword123!");
        loginButton.click();

        // Wait for redirect to dashboard
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.urlContains("/dashboard"));

        // Establish WebSocket connection for real-time updates
        messageLatch = new CountDownLatch(1);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        webSocketSession = container.connectToServer(TestWebSocketClient.class, new URI("ws://localhost:8080/ws/approvals"));

        // Wait for WebSocket open
        if (!messageLatch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("WebSocket connection not established in time");
        }
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

    /**
     * Test WebSocket client endpoint to listen for messages.
     */
    @ClientEndpoint
    public static class TestWebSocketClient {

        @OnOpen
        public void onOpen(Session session) {
            messageLatch.countDown();
        }

        @OnMessage
        public void onMessage(String message) {
            // No-op for this test, but could log or validate message format
        }

        @OnClose
        public void onClose(Session session, CloseReason reason) {
            // No-op
        }

        @OnError
        public void onError(Session session, Throwable thr) {
            thr.printStackTrace();
        }
    }

    @Test
    public void testRealTimeApprovalStatusUpdate() throws Exception {
        // Navigate to approvals page
        driver.get(BASE_URL + "/approvals");

        // Wait for approval list to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalList")));

        // Locate a specific approval item by test data id
        WebElement approvalItem = driver.findElement(By.cssSelector("#approvalList .approval-item[data-approval-id='12345']"));
        assertThat(approvalItem).isNotNull();

        // Get current status text
        WebElement statusElement = approvalItem.findElement(By.className("approval-status"));
        String initialStatus = statusElement.getText();
        assertThat(initialStatus).isNotEmpty();

        // Simulate backend approval status change by sending WebSocket message
        // This simulates an external user or system triggering the update
        String updateMessage = "{\"approvalId\":\"12345\",\"newStatus\":\"Approved\",\"timestamp\":\"2026-03-26T15:30:00Z\"}";
        webSocketSession.getAsyncRemote().sendText(updateMessage);

        // Wait up to 5 seconds for UI to update approval status
        boolean updated = wait.until(driver -> {
            WebElement updatedStatusElement = driver.findElement(By.cssSelector("#approvalList .approval-item[data-approval-id='12345'] .approval-status"));
            String updatedStatus = updatedStatusElement.getText();
            return "Approved".equalsIgnoreCase(updatedStatus);
        });

        assertThat(updated).isTrue();

        // Verify ApprovalAuditTrailView component updated
        WebElement auditTrailView = driver.findElement(By.id("ApprovalAuditTrailView"));
        assertThat(auditTrailView).isNotNull();

        // Check audit trail contains the new approval action
        boolean auditEntryFound = auditTrailView.getText().contains("Approved") && auditTrailView.getText().contains("2026-03-26");
        assertThat(auditEntryFound).isTrue();

        // Verify no page reload occurred by checking URL remains the same
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).endsWith("/approvals");

        // Verify no error messages displayed
        boolean errorVisible = driver.findElements(By.className("error-message")).stream().anyMatch(WebElement::isDisplayed);
        assertThat(errorVisible).isFalse();

        // Verify no flicker or delay beyond expected latency (max 5 seconds already waited)
        // Additional check: ensure DOM stability by checking status text remains stable for 2 seconds
        Thread.sleep(2000);
        String stableStatus = driver.findElement(By.cssSelector("#approvalList .approval-item[data-approval-id='12345'] .approval-status")).getText();
        assertThat(stableStatus).isEqualTo("Approved");
    }
}
