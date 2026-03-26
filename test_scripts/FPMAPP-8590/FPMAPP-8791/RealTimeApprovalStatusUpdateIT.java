/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8791
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:14:36
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Integration test for real-time approval status update without full page refresh.
 * 
 * Preconditions:
 * - User logged in
 * - Approval workflow active with pending tasks
 * - WebSocket connection active
 * 
 * This test mocks backend services and simulates a real-time approval update via WebSocket.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RealTimeApprovalStatusUpdateIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private WebDriverWait wait;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final String WS_TOPIC_APPROVAL_STATUS = "/topic/approval-status";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
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

        // Mock user login and pending approvals
        User mockUser = new User();
        mockUser.setId(1001L);
        mockUser.setUsername("testuser");

        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock pending approval tasks
        when(fpmDealsheetController.getPendingApprovalsForUser(mockUser.getId()))
            .thenReturn(List.of(
                new com.webapp.fpmapp.entities.UserApprovalTask(2001L, "Pending", "Deal Sheet #123")
            ));

        // Mock audit trail initial state
        when(fpmCommonController.getAuditTrail(2001L))
            .thenReturn(List.of(
                new com.webapp.fpmapp.entities.AuditTrailEntry("Created", "testuser", "2026-03-26T14:00:00Z")
            ));
    }

    @Test
    public void testRealTimeApprovalStatusUpdateWithoutFullPageRefresh() throws Exception {
        String baseUrl = "http://localhost:" + port + "/fpmapp";

        // Navigate to the approval page
        driver.get(baseUrl + "/approvals");

        // Wait for page to load and user to be logged in
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approval-list")));

        // Verify initial approval status is 'Pending'
        WebElement approvalStatus = driver.findElement(By.cssSelector("#approval-list .approval-status"));
        assertThat(approvalStatus.getText()).isEqualToIgnoringCase("Pending");

        // Verify audit trail initial entry
        WebElement auditTrailView = driver.findElement(By.id("ApprovalAuditTrailView"));
        assertThat(auditTrailView.getText()).contains("Created");

        // Setup latch to wait for WebSocket message processing
        CountDownLatch latch = new CountDownLatch(1);

        // Simulate backend sending a WebSocket message for approval update
        new Thread(() -> {
            try {
                Thread.sleep(2000); // simulate delay
                // Simulate approval status update message
                ApprovalStatusUpdateMessage updateMessage = new ApprovalStatusUpdateMessage();
                updateMessage.setApprovalRequestId(2001L);
                updateMessage.setNewStatus("Approved");
                updateMessage.setAuditTrailEntry(new com.webapp.fpmapp.entities.AuditTrailEntry("Approved", "approverUser", "2026-03-26T15:00:00Z"));

                messagingTemplate.convertAndSend(WS_TOPIC_APPROVAL_STATUS, updateMessage);

                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // Wait for the UI to update approval status without page refresh
        boolean updated = wait.until(driver -> {
            WebElement statusElement = driver.findElement(By.cssSelector("#approval-list .approval-status"));
            return "Approved".equalsIgnoreCase(statusElement.getText());
        });

        assertThat(updated).isTrue();

        // Verify audit trail view updated with new entry
        boolean auditUpdated = wait.until(driver -> {
            WebElement auditView = driver.findElement(By.id("ApprovalAuditTrailView"));
            return auditView.getText().contains("Approved") && auditView.getText().contains("approverUser");
        });

        assertThat(auditUpdated).isTrue();

        // Verify no full page reload occurred by checking URL remains the same
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).isEqualTo(baseUrl + "/approvals");

        // Verify no error messages or UI glitches
        List<WebElement> errorElements = driver.findElements(By.cssSelector(".error-message, .ui-glitch"));
        assertThat(errorElements).isEmpty();
    }

    /**
     * DTO for approval status update message sent over WebSocket.
     */
    public static class ApprovalStatusUpdateMessage {
        private Long approvalRequestId;
        private String newStatus;
        private com.webapp.fpmapp.entities.AuditTrailEntry auditTrailEntry;

        public Long getApprovalRequestId() {
            return approvalRequestId;
        }

        public void setApprovalRequestId(Long approvalRequestId) {
            this.approvalRequestId = approvalRequestId;
        }

        public String getNewStatus() {
            return newStatus;
        }

        public void setNewStatus(String newStatus) {
            this.newStatus = newStatus;
        }

        public com.webapp.fpmapp.entities.AuditTrailEntry getAuditTrailEntry() {
            return auditTrailEntry;
        }

        public void setAuditTrailEntry(com.webapp.fpmapp.entities.AuditTrailEntry auditTrailEntry) {
            this.auditTrailEntry = auditTrailEntry;
        }
    }
}
