/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6220
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:39:45
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import io.github.bonigarcia.wdm.WebDriverManager;
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
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test to validate real-time UI updates reflecting currency rate changes without page reload.
 * Preconditions:
 * - Currency real-time update WebSocket service is running.
 * - UI is loaded and connected to WebSocket feed.
 *
 * The test triggers a currency override via REST API call and observes UI currency display panel updates in real-time.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyOverrideRealTimeUpdateIT {

    private static WebDriver driver;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
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
    public void setupMocks() {
        // Mock response for currency override POST endpoint
        when(currencyConvertionController.overrideCurrencyRate(
                eq("USD"), any(Double.class), any(String.class), any(String.class)))
            .thenReturn(new com.webapp.fpmapp.dto.CurrencyOverrideResponse("SUCCESS", "Override applied"));

        // Could add other mocks for currency queries if necessary
    }

    @Test
    public void testRealTimeCurrencyOverrideUIUpdate() throws InterruptedException, TimeoutException {
        // 1. Load the UI page (assuming at http://localhost:8080/currency-panel)
        driver.get("http://localhost:8080/currency-panel");

        // Wait for page load and WebSocket connection establishment
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd)
                .executeScript("return document.readyState").equals("complete"));

        // Check initial currency rate display element is present
        WebElement usdRateElement = wait.until(d -> d.findElement(By.id("currency-usd-rate")));
        WebElement usdOverrideFlagElement = wait.until(d -> d.findElement(By.id("currency-usd-override-flag")));

        // Capture initial state
        String initialRate = usdRateElement.getText();
        String initialOverrideFlag = usdOverrideFlagElement.getText();

        // 2. Trigger a currency override via POST /api/fpm/currency/override
        double overriddenRate = 1.25;
        String overrideReason = "Test override for automation";
        String adminUserId = "adminUser123";

        // Build JSON body
        String requestBody = String.format(
                "{\"currencyCode\":\"USD\",\"newRate\":%s,\"adminUserId\":\"%s\",\"overrideReason\":\"%s\"}",
                overriddenRate, adminUserId, overrideReason);

        // Perform the POST call
        webTestClient.post()
                .uri("/api/fpm/currency/override")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("SUCCESS");

        // 3. Observe the UI currency display panel for real-time data update
        // Wait max 20 seconds for WebSocket pushed update reflecting override value
        boolean updated = wait.until(wd -> {
            String updatedRateText = wd.findElement(By.id("currency-usd-rate")).getText();
            String updatedOverrideFlag = wd.findElement(By.id("currency-usd-override-flag")).getText();
            try {
                double updatedRate = Double.parseDouble(updatedRateText.trim());
                boolean overrideFlagSet = "OVERRIDDEN".equalsIgnoreCase(updatedOverrideFlag.trim());
                // Check if the overridden rate is reflected and flag is set
                return Math.abs(updatedRate - overriddenRate) < 0.001 && overrideFlagSet;
            } catch (NumberFormatException ex) {
                return false;
            }
        });

        assertTrue(updated, "Expected the UI to update currency rate and override flag in real-time.");

        // 4. Confirm the page does not reload or require manual refresh during update
        // We'll check stability of the page - no navigation event or reload by checking title remains same
        String pageTitle = driver.getTitle();
        Thread.sleep(1000); // short pause
        assertEquals(pageTitle, driver.getTitle(), "Page reload detected - it should not reload on update.");

        // 5. Verify no UI errors or stale data displayed
        // Check if any error message element visible
        boolean errorVisible = driver.findElements(By.id("currency-error-message")).stream()
                .anyMatch(el -> el.isDisplayed());

        assertFalse(errorVisible, "No UI error messages should be visible after override update.");
    }
}
