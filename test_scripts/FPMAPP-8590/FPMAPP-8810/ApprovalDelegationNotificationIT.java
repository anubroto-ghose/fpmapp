/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8810
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:44:37
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;

import jakarta.annotation.PostConstruct;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalDelegationNotificationIT {

    private static WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static BlockingQueue<String> notificationQueue = new LinkedBlockingQueue<>();

    private static WebSocketSession testUserWebSocketSession;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
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
    public void setup() throws Exception {
        // Mock user profile to have delegation permissions
        when(fpmUserProfileController.hasDelegationPermission(any(String.class))).thenReturn(true);

        // Mock current logged-in user
        when(fpmUserProfileController.getCurrentUsername()).thenReturn("delegatedUser");

        // Setup WebSocket client to simulate active connection for logged-in user
        StandardWebSocketClient client = new StandardWebSocketClient();
        testUserWebSocketSession = client.doHandshake(new TextWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                notificationQueue.offer(message.getPayload());
            }
        }, new URI("ws://localhost:8080/ws/notifications/delegatedUser")).get();
    }

    @Test
    public void testInAppNotificationReceivedOnApprovalDelegation() throws Exception {
        // Step 1: Simulate delegation from another user
        String delegatedApprovalId = "approval-12345";
        String delegatorUser = "managerUser";
        String delegateeUser = "delegatedUser";

        // Simulate delegation event - normally this would be triggered by service logic
        // Here we simulate sending a WebSocket notification to the delegatee user
        String notificationPayload = "{\"type\":\"delegation\",\"approvalId\":\"" + delegatedApprovalId + "\",\"fromUser\":\"" + delegatorUser + "\",\"message\":\"You have been delegated an approval request.\"}";

        messagingTemplate.convertAndSendToUser(delegateeUser, "/queue/notifications", notificationPayload);

        // Step 2: Open the UI as the delegated user
        driver.get(BASE_URL + "/login");

        // Simulate login as delegatedUser
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys(delegateeUser);
        passwordInput.sendKeys("password"); // assuming test password
        loginButton.click();

        // Wait for main page to load
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 3: Wait for in-app notification to appear
        By notificationSelector = By.cssSelector(".in-app-notification[data-approval-id='" + delegatedApprovalId + "']");
        WebElement notificationElement = wait.until(ExpectedConditions.visibilityOfElementLocated(notificationSelector));

        // Step 4: Verify notification content
        String notificationText = notificationElement.getText();
        assertThat(notificationText).contains("delegated an approval request");
        assertThat(notificationText).contains(delegatorUser);

        // Step 5: Verify notification is actionable (e.g., has a button/link to view approval)
        WebElement actionButton = notificationElement.findElement(By.cssSelector("button.view-approval"));
        assertThat(actionButton).isNotNull();

        // Step 6: Click the action button and verify navigation to approval detail page
        actionButton.click();
        wait.until(ExpectedConditions.urlContains("/approvals/" + delegatedApprovalId));

        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).endsWith(delegatedApprovalId);
    }

    @AfterAll
    public static void cleanupWebSocket() throws Exception {
        if (testUserWebSocketSession != null && testUserWebSocketSession.isOpen()) {
            testUserWebSocketSession.close();
        }
    }

    @Configuration
    static class TestConfig {
        @Bean
        public SimpMessagingTemplate simpMessagingTemplate() {
            // Mock or real bean depending on test setup
            return new SimpMessagingTemplate(message -> {
                // no-op for test
            });
        }
    }
}
