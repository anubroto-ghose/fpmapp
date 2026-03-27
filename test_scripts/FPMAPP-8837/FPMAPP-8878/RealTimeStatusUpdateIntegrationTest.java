/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8878
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:38:40
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RealTimeStatusUpdateIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final String WS_ENDPOINT = "/ws-status-updates";

    private static final String USERNAME = "requesterUser";
    private static final String PASSWORD = "password123";

    private static final String APPROVAL_REQUEST_ID = "REQ-12345";

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
        wait = new WebDriverWait(driver, 15);

        // Mock the FpmCommonController to simulate backend approval workflow status update trigger
        doAnswer(invocation -> {
            String requestId = invocation.getArgument(0);
            String newStatus = invocation.getArgument(1);
            // Simulate sending a WebSocket message to the user
            messagingTemplate.convertAndSendToUser(USERNAME, "/queue/approval-status", newStatus);
            return null;
        }).when(fpmCommonController).triggerApprovalStatusChange(any(String.class), any(String.class));
    }

    @Test
    public void testRealTimeStatusUpdateViaWebSocket() throws Exception {
        // Step 0: Login as requester
        driver.get("http://localhost:" + port + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys(USERNAME);
        passwordInput.sendKeys(PASSWORD);
        loginButton.click();

        // Wait for redirect to dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 1: Navigate to approval requests page
        driver.get("http://localhost:" + port + "/approval-requests");

        // Step 2: Locate the approval request in progress
        WebElement requestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[td/text() = '" + APPROVAL_REQUEST_ID + "']")));

        // Verify initial status is 'In Progress'
        WebElement statusCell = requestRow.findElement(By.className("status-cell"));
        assertThat(statusCell.getText()).isEqualToIgnoringCase("In Progress");

        // Step 3: Trigger backend status change event
        // Simulate backend changing status to 'Approved'
        fpmCommonController.triggerApprovalStatusChange(APPROVAL_REQUEST_ID, "Approved");

        // Step 4: Wait for UI to update status in real-time
        boolean updated = wait.until(driver -> {
            WebElement updatedStatusCell = driver.findElement(By.xpath(
                    "//tr[td/text() = '" + APPROVAL_REQUEST_ID + "']/td[contains(@class,'status-cell')]"));
            return "Approved".equalsIgnoreCase(updatedStatusCell.getText());
        });

        assertThat(updated).isTrue();

        // Step 5: Verify no error messages are displayed
        List<WebElement> errorElements = driver.findElements(By.className("error-message"));
        assertThat(errorElements).isEmpty();
    }
}
