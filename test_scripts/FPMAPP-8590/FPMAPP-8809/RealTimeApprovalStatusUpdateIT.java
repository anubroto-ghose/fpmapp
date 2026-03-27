/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8809
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:55:53
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

import java.util.Collections;

/**
 * Integration test for real-time approval status update reflected in UI immediately.
 * 
 * Preconditions:
 * - User is logged in with approval permissions.
 * - WebSocket connection is active.
 * - Approval action triggered by another user session.
 * 
 * This test uses Selenium WebDriver with ChromeDriver and mocks backend services.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
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

    private static final String BASE_URL = "http://localhost:";

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
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Mock user profile with approval permissions
        User mockUser = new User();
        mockUser.setId(1001L);
        mockUser.setUsername("approverUser");
        mockUser.setRole("APPROVER");

        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock dealsheet controller to return a pending approval request
        when(fpmDealsheetController.getPendingApprovals(any())).thenReturn(
                Collections.singletonList(
                        new com.webapp.fpmapp.dto.ApprovalRequestDTO(2001L, "Pending", "Request for Project X")));

        // Mock currency conversion and forecast services as needed
        when(currencyConvertionController.convert(any(), any(), any())).thenReturn(1.0);
        when(fpmForecastController.getForecast(any())).thenReturn(new com.webapp.fpmapp.dto.ForecastDTO());

        // Mock common controller for approval status update
        when(fpmCommonController.getApprovalStatus(any())).thenReturn("Pending");
    }

    /**
     * Test real-time approval status update reflected in UI immediately without page refresh.
     * 
     * Steps:
     * 1. Login as approver user.
     * 2. Open pending approval request.
     * 3. Simulate approval action from another user session via WebSocket message.
     * 4. Verify UI updates approval status immediately.
     */
    @Test
    public void testRealTimeApprovalStatusUpdate() throws Exception {
        // Step 1: Navigate to login page and login
        driver.get(BASE_URL + port + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("approverUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard page to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to approvals page
        driver.get(BASE_URL + port + "/approvals");

        // Wait for pending approval request to be visible
        WebElement approvalRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("tr[data-approval-id='2001']")));

        WebElement statusCell = approvalRow.findElement(By.cssSelector("td.status"));
        assertThat(statusCell.getText()).isEqualTo("Pending");

        // Step 3: Simulate approval action from another user session
        // This simulates a WebSocket message that updates the approval status to 'Approved'
        simulateWebSocketApprovalStatusUpdate(2001L, "Approved");

        // Step 4: Verify UI updates approval status immediately without page refresh
        boolean statusUpdated = wait.until(driver -> {
            WebElement updatedStatusCell = driver.findElement(By.cssSelector("tr[data-approval-id='2001'] td.status"));
            return "Approved".equals(updatedStatusCell.getText());
        });

        assertThat(statusUpdated).isTrue();

        // Verify UI remains responsive by clicking a button (e.g. refresh button) and no errors
        WebElement refreshButton = driver.findElement(By.id("refreshApprovals"));
        refreshButton.click();

        // Wait briefly and verify still on approvals page
        wait.until(ExpectedConditions.urlContains("/approvals"));

        // Final assertion: status remains 'Approved'
        WebElement finalStatusCell = driver.findElement(By.cssSelector("tr[data-approval-id='2001'] td.status"));
        assertThat(finalStatusCell.getText()).isEqualTo("Approved");
    }

    /**
     * Helper method to simulate a WebSocket message that updates approval status.
     * 
     * In a real test environment, this could be done by sending a message to the WebSocket endpoint
     * or by mocking the WebSocket client in the frontend.
     * 
     * Here, we simulate by executing JavaScript that triggers the frontend event handler.
     * 
     * @param approvalId the approval request ID
     * @param newStatus the new status to update
     */
    private void simulateWebSocketApprovalStatusUpdate(Long approvalId, String newStatus) {
        String script = "var event = new CustomEvent('approvalStatusUpdate', { detail: { approvalId: '" + approvalId + "', status: '" + newStatus + "' } });"
                + "document.dispatchEvent(event);";
        ((JavascriptExecutor) driver).executeScript(script);
    }
}
