/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8607
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 08:09:17
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

import com.webapp.fpmapp.services.CurrencyConvertionController;

import java.util.Collections;
import java.util.List;

/**
 * Integration test for real-time currency rate update notifications in the FPM_UI.
 * 
 * Preconditions:
 * - User is logged in
 * - WebSocket connection is active
 * 
 * This test simulates a backend currency rate update event pushed via WebSocket
 * and verifies the UI updates instantly without page refresh.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyRateUpdateNotificationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:";

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static WebSocketStompClient stompClient;
    private StompSession stompSession;

    private final CountDownLatch latch = new CountDownLatch(1);

    private volatile String receivedCurrencyUpdatePayload = null;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Setup WebSocketStompClient for simulating backend push
        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        stompClient = new WebSocketStompClient(new SockJsClient(transports));
        stompClient.setInboundMessageSizeLimit(64 * 1024);
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
    public void setup() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock the currency conversion controller to return a fixed rate
        when(currencyConvertionController.getCurrentRates()).thenReturn(
                // Simulated initial currency rates
                java.util.Map.of("USD", 1.0, "EUR", 0.85, "GBP", 0.75));

        // Navigate to the login page and perform login
        driver.get(BASE_URL + port + "/login");

        // Simulate login (assuming username and password fields and login button exist)
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("Test@1234");
        loginButton.click();

        // Wait for redirect to dashboard or main page
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void testCurrencyRateUpdateNotification() throws Exception {
        // Navigate to the currency rates page/component
        driver.get(BASE_URL + port + "/currency-rates");

        // Wait for the currency rates component to be visible
        WebElement currencyRatesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyRatesTable")));

        // Verify initial rate for EUR is displayed correctly
        WebElement eurRateCell = currencyRatesTable.findElement(By.xpath(".//td[@data-currency='EUR']"));
        String initialEurRateText = eurRateCell.getText();
        assertThat(initialEurRateText).isEqualTo("0.85");

        // Setup WebSocket STOMP session to simulate backend push
        String wsUrl = "ws://localhost:" + port + "/ws-endpoint/websocket";

        stompSession = stompClient.connect(new URI(wsUrl), new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompSession.ConnectedHeaders connectedHeaders) {
                // Subscribe to currency rate update topic
                session.subscribe("/topic/currencyRateUpdates", frame -> {
                    receivedCurrencyUpdatePayload = new String(frame.getPayload());
                    latch.countDown();
                });
            }
        }).get(5, TimeUnit.SECONDS);

        // Simulate backend sending a currency rate update event
        String updatedCurrencyRateJson = "{\"currency\":\"EUR\",\"rate\":0.90}";

        // Simulate sending message to /topic/currencyRateUpdates
        stompSession.send("/app/sendCurrencyRateUpdate", updatedCurrencyRateJson.getBytes());

        // Wait for the UI to receive and process the update
        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);
        assertThat(messageReceived).isTrue();

        // Wait for the UI element to update the EUR rate
        wait.until(ExpectedConditions.textToBePresentInElement(eurRateCell, "0.90"));

        // Verify the UI updated the rate instantly without page refresh
        String updatedEurRateText = eurRateCell.getText();
        assertThat(updatedEurRateText).isEqualTo("0.90");

        // Verify notification appears
        WebElement notification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyUpdateNotification")));
        String notificationText = notification.getText();
        assertThat(notificationText).contains("EUR rate updated to 0.90");

        // Verify no UI glitches: check that the table is still displayed and enabled
        assertThat(currencyRatesTable.isDisplayed()).isTrue();
        assertThat(currencyRatesTable.isEnabled()).isTrue();
    }
}
