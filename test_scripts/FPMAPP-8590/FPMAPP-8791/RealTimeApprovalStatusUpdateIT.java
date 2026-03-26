/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8791
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:30:08
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
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
import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

import org.springframework.boot.test.web.server.LocalManagementPort;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for real-time approval status update without full page refresh.
 * 
 * Preconditions:
 * - User is logged in
 * - Approval workflow active with pending tasks
 * - WebSocket connection established
 * 
 * This test mocks backend services and simulates a WebSocket message to verify UI updates.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RealTimeApprovalStatusUpdateIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private static WebSocketStompClient stompClient;

    private StompSession stompSession;

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Setup WebSocketStompClient
        stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));
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
        // Mock user login and pending approval tasks
        User mockUser = new User();
        mockUser.setId(1001L);
        mockUser.setUsername("testuser");
        mockUser.setRole("manager");

        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock pending approval tasks
        when(fpmDealsheetController.getPendingApprovalsForUser(Mockito.anyLong()))
                .thenReturn(Collections.singletonList(
                        new com.webapp.fpmapp.entities.User() // Using User entity as placeholder for approval task
                ));

        // Mock currency conversion and forecast services as needed
        when(currencyConvertionController.getCurrentRates()).thenReturn(Collections.emptyMap());
        when(fpmForecastController.getForecastData()).thenReturn(Collections.emptyMap());

        // Open the application login page and simulate login
        driver.get("http://localhost:" + port + "/login");

        // Simulate login form fill and submit
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for redirect to dashboard
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/dashboard"));

        // Verify user is logged in by checking presence of logout button
        assertTrue(driver.findElement(By.id("logoutButton")).isDisplayed());

        // Establish WebSocket connection to simulate real-time updates
        stompSession = stompClient.connect(
                new URI("ws://localhost:" + port + "/ws-endpoint"),
                new WebSocketHttpHeaders(),
                new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS);

        assertNotNull(stompSession);
    }

    @Test
    public void testRealTimeApprovalStatusUpdateWithoutFullPageRefresh() throws Exception {
        // Navigate to approvals page
        driver.get("http://localhost:" + port + "/approvals");

        // Wait for approval list to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalList")));

        // Find a pending approval item
        WebElement pendingApproval = driver.findElement(By.cssSelector(".approval-item.pending"));
        assertNotNull(pendingApproval, "Pending approval item should be present");

        // Simulate an approval action triggered externally (e.g., by another user or system)
        // We simulate this by sending a WebSocket message to the client

        // Prepare a mock approval update message payload
        String approvalUpdateJson = "{" +
                "\"approvalId\": 12345," +
                "\"status\": \"APPROVED\"," +
                "\"updatedBy\": \"managerUser\"," +
                "\"timestamp\": \"2026-03-26T15:30:00Z\"}";

        // Send the message to the subscribed topic
        stompSession.send("/app/approval/update", approvalUpdateJson.getBytes());

        // Wait for UI to update approval status without full page reload
        // We check that the approval item status text changes to "APPROVED"

        boolean statusUpdated = wait.until(driver -> {
            WebElement statusElement = driver.findElement(By.cssSelector(".approval-item[data-id='12345'] .status"));
            return statusElement != null && "APPROVED".equalsIgnoreCase(statusElement.getText().trim());
        });

        assertTrue(statusUpdated, "Approval status should update to APPROVED in real-time");

        // Verify no full page reload occurred by checking the URL remains the same
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/approvals"), "Page URL should remain on approvals page");

        // Verify ApprovalAuditTrailView shows updated audit trail entry
        WebElement auditTrailView = driver.findElement(By.id("ApprovalAuditTrailView"));
        assertNotNull(auditTrailView, "Audit trail view component should be present");

        // Wait for new audit trail entry to appear
        boolean auditEntryAppeared = wait.until(driver -> {
            return auditTrailView.findElements(By.cssSelector(".audit-entry")).stream()
                    .anyMatch(e -> e.getText().contains("APPROVED") && e.getText().contains("managerUser"));
        });

        assertTrue(auditEntryAppeared, "Audit trail should show the new approval entry in real-time");

        // Verify no error messages or UI glitches
        boolean errorVisible = driver.findElements(By.cssSelector(".error-message, .ui-glitch")).size() > 0;
        assertFalse(errorVisible, "No error messages or UI glitches should be visible");
    }
}