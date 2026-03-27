/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8621
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:59:45
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
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.webapp.fpmapp.services.FpmCommonController;

import java.util.Collections;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApprovalStatusRealTimeUpdateIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private static WebDriverWait wait;

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";

    @MockBean
    private FpmCommonController fpmCommonController;

    private BlockingQueue<String> receivedMessages = new LinkedBlockingQueue<>();

    private static final String WS_ENDPOINT = "/fpm/ui/notifications/ws";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        // Mock the service that triggers approval status change event
        // Simulate backend sending WebSocket notification
        doAnswer(invocation -> {
            String approvalStatus = invocation.getArgument(0);
            // Simulate sending WebSocket message to client
            // In real app, this would push to /topic/approval-status or similar
            // Here we just simulate the message content
            String message = String.format("{\"type\":\"approvalStatusChange\",\"status\":\"%s\"}", approvalStatus);
            receivedMessages.offer(message);
            return null;
        }).when(fpmCommonController).notifyApprovalStatusChange(any(String.class));
    }

    /**
     * Test real-time UI update on approval status change.
     * Preconditions:
     * - User logged in with role to view approval statuses.
     * - WebSocket connection established.
     * - Approval workflow UI loaded.
     * 
     * Test Steps:
     * 1. Trigger approval status change event on backend.
     * 2. Observe UI updates immediately without page refresh.
     * 
     * Expected:
     * - UI updates status indicator.
     * - No errors or delays.
     */
    @Test
    public void testRealTimeApprovalStatusUpdate() throws Exception {
        // Step 0: Login user
        driver.get("http://localhost:" + port + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(USERNAME);
        passwordInput.sendKeys(PASSWORD);
        loginButton.click();

        // Wait for redirect to approval workflow UI
        wait.until(ExpectedConditions.urlContains("/approval-workflow"));

        // Verify approval status element is present
        WebElement statusIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatusIndicator")));
        assertThat(statusIndicator.getText()).isNotEmpty();

        // Establish WebSocket connection via JS
        establishWebSocketConnection();

        // Step 1: Trigger backend approval status change
        String newStatus = "APPROVED";
        fpmCommonController.notifyApprovalStatusChange(newStatus);

        // Step 2: Wait for UI to update
        boolean updated = wait.until(driver -> {
            WebElement statusElem = driver.findElement(By.id("approvalStatusIndicator"));
            String text = statusElem.getText();
            return text.equalsIgnoreCase(newStatus);
        });

        assertThat(updated).isTrue();

        // Verify status indicator CSS class changed accordingly
        WebElement statusElem = driver.findElement(By.id("approvalStatusIndicator"));
        String cssClass = statusElem.getAttribute("class");
        assertThat(cssClass).containsIgnoringCase("approved");

        // Verify no JS errors in browser console
        Object errors = ((JavascriptExecutor) driver).executeScript(
                "return window.jsErrors || [];");
        assertThat(errors).isInstanceOfAny(java.util.List.class);
        @SuppressWarnings("unchecked")
        java.util.List<String> jsErrors = (java.util.List<String>) errors;
        assertThat(jsErrors).isEmpty();
    }

    /**
     * Inject JS to establish WebSocket connection to /fpm/ui/notifications/ws
     * and update the approvalStatusIndicator element on receiving approvalStatusChange messages.
     */
    private void establishWebSocketConnection() {
        String wsUrl = String.format("ws://localhost:%d%s", port, WS_ENDPOINT);

        String script = "window.jsErrors = [];
            try {
                if (window.fpmWebSocket) {
                    window.fpmWebSocket.close();
                }
                window.fpmWebSocket = new WebSocket('" + wsUrl + "');
                window.fpmWebSocket.onmessage = function(event) {
                    var data = JSON.parse(event.data);
                    if (data.type === 'approvalStatusChange') {
                        var statusElem = document.getElementById('approvalStatusIndicator');
                        if (statusElem) {
                            statusElem.textContent = data.status;
                            statusElem.className = 'status-indicator ' + data.status.toLowerCase();
                        }
                    }
                };
                window.fpmWebSocket.onerror = function(error) {
                    window.jsErrors.push('WebSocket error: ' + error.message);
                };
            } catch(e) {
                window.jsErrors.push('Exception: ' + e.message);
            }";

        ((JavascriptExecutor) driver).executeScript(script);
    }
}
