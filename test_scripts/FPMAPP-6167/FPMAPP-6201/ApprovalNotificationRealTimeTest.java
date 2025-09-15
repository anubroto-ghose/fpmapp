/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6201
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:55:27
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.controllers.FpmTravelController;
import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmForecastController;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApprovalNotificationRealTimeTest {

    private static WebDriver driver;
    private static WebSocketStompClient stompClient;
    private static StompSession stompSession;

    private static final String WS_URI = "ws://localhost:8080/ws/approvals";
    private static final String BASE_URL = "http://localhost:8080";

    private BlockingQueue<String> blockingQueue;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (Assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        WebSocketClient client = new SockJsClient(
                List.of(new WebSocketTransport(new org.springframework.web.socket.client.standard.StandardWebSocketClient())));
        stompClient = new WebSocketStompClient(client);
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
        blockingQueue = new LinkedBlockingDeque<>();

        // Mock responses for external services
        when(currencyConvertionController.getCurrentRate(any(String.class))).thenReturn(1.1);

        // Mock notification API POST
        // We assume the backend endpoint /notifications/in-app is called
        // We'll use spy on dealsheet controller to trigger notification behavior
        doAnswer(invocation -> {
            // Simulate successful response
            return "{\"status\":\"success\"}";
        }).when(fpmDealsheetController).postInAppNotification(any());

        // Connect WebSocket client
        stompSession = stompClient.connect(new URI(WS_URI), new StompSessionHandlerAdapter() {
        }).get(3, TimeUnit.SECONDS);
        stompSession.subscribe("/topic/approvals/updates", new org.springframework.messaging.simp.stomp.StompFrameHandler() {

            @Override
            public java.lang.reflect.Type getPayloadType(org.springframework.messaging.simp.stomp.StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(org.springframework.messaging.simp.stomp.StompHeaders headers, Object payload) {
                blockingQueue.offer((String) payload);
            }
        });
    }

    @Test
    public void testInAppNotificationDisplayedRealTimeWithoutPageRefresh() throws Exception {
        // 1. Login simulation (fake, loading a test page mocking user logged in)
        driver.get(BASE_URL + "/fpm_ui/login?testUser=approverUser");

        WebDriverWait wait = new WebDriverWait(driver, 10);

        // 2. Navigate to approval page with notification area
        driver.get(BASE_URL + "/fpm_ui/approvals");

        // Wait for WebSocket client to be ready and page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inAppNotificationArea")));

        // 3. Trigger an approval workflow state change via simulated backend call
        // We'll simulate through mock call on controller or triggering an event

        // We simulate an approval event that triggers notification:
        String approvalEventJson = "{" +
                "\"approvalId\":12345," +
                "\"eventType\":\"APPROVAL_GRANTED\"," +
                "\"message\":\"Your approval for deal #12345 has been granted by user approverUser.\"" +
                "}";

        // Simulate sending the notification via WebSocket broadcast
        stompSession.send("/app/approvals/event", approvalEventJson.getBytes());

        // 4. Wait and check the UI notification area updates dynamically
        // We'll wait for the notification text to appear

        boolean notificationAppeared = wait.until(d -> {
            WebElement notificationArea = d.findElement(By.id("inAppNotificationArea"));
            String text = notificationArea.getText();
            return text != null && text.contains("Your approval for deal #12345 has been granted");
        });

        assertTrue(notificationAppeared, "Expected in-app notification was not displayed in real-time");

        // 5. Verify the POST /notifications/in-app API was called (mock verification)
        verify(fpmDealsheetController, atLeastOnce()).postInAppNotification(any());

        // 6. Verify notification content in backend
        String receivedNotification = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertNotNull(receivedNotification, "Expected notification message was not received via websocket");
        assertTrue(receivedNotification.contains("APPROVAL_GRANTED"), "Notification eventType mismatch");
        assertTrue(receivedNotification.contains("12345"), "Notification approvalId mismatch");
    }
}
