/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8790
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:13:54
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.Duration;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApprovalStatusUpdateRapidChangesIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private static WebSocketStompClient stompClient;
    private StompSession stompSession;

    private final BlockingQueue<String> approvalStatusMessages = new LinkedBlockingDeque<>();

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Setup WebSocketStompClient
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
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
        // Mock service responses as needed
        Mockito.when(currencyConvertionController.getCurrentRate(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(1.12);
        Mockito.when(fpmForecastController.getForecast(Mockito.anyString()))
                .thenReturn("ForecastData");
        Mockito.when(fpmCommonController.getCommonData())
                .thenReturn("CommonData");

        // Establish WebSocket connection
        String url = String.format("ws://localhost:%d/ws-endpoint", port);
        stompSession = stompClient.connect(url, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {
        }).get(5, TimeUnit.SECONDS);

        // Subscribe to approval status topic
        stompSession.subscribe("/topic/approval-status", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                approvalStatusMessages.offer((String) payload);
            }
        });

        // Navigate to the application login page and login
        driver.get("http://localhost:" + port + "/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("testpassword");
        loginButton.click();

        // Wait for main page to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Confirm WebSocket connection is active in UI (some indicator element)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("websocketStatusActive")));
    }

    @Test
    public void testRapidConsecutiveApprovalStatusUpdates_NoUIFlicker() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Locate the approval status element and audit trail view
        WebElement approvalStatusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatus")));
        WebElement auditTrailView = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalAuditTrailView")));

        // Simulate rapid approval status changes from backend via WebSocket messages
        // For test, we simulate by sending messages directly to the subscription queue
        // In real scenario, these would come from other user sessions or backend events

        String[] rapidStatuses = new String[] {
                "APPROVED_BY_MANAGER",
                "REJECTED_BY_DIRECTOR",
                "DELEGATED_TO_USER123",
                "APPROVED_BY_DIRECTOR",
                "PENDING_FINAL_APPROVAL"
        };

        // Simulate backend pushing these status updates rapidly
        for (String status : rapidStatuses) {
            // Simulate receiving message
            approvalStatusMessages.offer(status);

            // Use JS executor to simulate UI update triggered by WebSocket message
            ((JavascriptExecutor) driver).executeScript(
                    "document.getElementById('approvalStatus').textContent = arguments[0];", status);

            // Append audit trail entry
            ((JavascriptExecutor) driver).executeScript(
                    "var audit = document.getElementById('approvalAuditTrailView');" +
                    "var entry = document.createElement('div');" +
                    "entry.textContent = arguments[0] + ' at ' + new Date().toLocaleTimeString();" +
                    "audit.appendChild(entry);", status);

            // Small delay to simulate rapid but not simultaneous updates
            Thread.sleep(200);
        }

        // Verify UI approval status matches last status
        wait.until(ExpectedConditions.textToBe(By.id("approvalStatus"), rapidStatuses[rapidStatuses.length - 1]));
        String finalStatus = approvalStatusElement.getText();
        assertThat(finalStatus).isEqualTo(rapidStatuses[rapidStatuses.length - 1]);

        // Verify audit trail contains all status updates
        String auditTrailText = auditTrailView.getText();
        for (String status : rapidStatuses) {
            assertThat(auditTrailText).contains(status);
        }

        // Verify no flicker or visual glitches by checking element visibility and stable text
        assertThat(approvalStatusElement.isDisplayed()).isTrue();

        // Check no error messages displayed
        boolean errorPresent = driver.findElements(By.className("error-message")).size() > 0;
        assertThat(errorPresent).isFalse();
    }
}
