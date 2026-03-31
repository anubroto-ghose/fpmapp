/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8939
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:51:01
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.URI;
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
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration Selenium test for verifying WebSocket connection interruptions are handled gracefully.
 * 
 * Preconditions:
 * - User is logged in
 * - WebSocket connection is active
 * - Approval requests or uploads are in progress
 * 
 * Test Steps:
 * 1. Simulate WebSocket disconnection
 * 2. Verify UI shows connection loss notification
 * 3. Restore WebSocket connection
 * 4. Verify real-time updates resume
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class WebSocketConnectionInterruptionTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:8080";

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
        // Mock services responses as needed
        when(currencyConvertionController.convertCurrency("USD", "EUR", 100.0)).thenReturn(85.0);
        when(fpmForecastController.getForecastData()).thenReturn("{\"forecast\":\"stable\"}");
        when(fpmCommonController.getCommonData()).thenReturn("common-data");

        // Navigate to login page and perform login
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("Test@1234");
        loginButton.click();

        // Wait for dashboard or home page to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Verify user is logged in by checking presence of logout button
        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutButton")));
        assertThat(logoutButton.isDisplayed()).isTrue();

        // Navigate to approval requests page where WebSocket updates occur
        driver.get(BASE_URL + "/approvals");

        // Wait for WebSocket connection indicator to be visible
        WebElement wsStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ws-connection-status")));
        assertThat(wsStatus.getText()).isEqualTo("Connected");

        // Assume approval requests or uploads are in progress
        WebElement approvalList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalRequestsList")));
        assertThat(approvalList.isDisplayed()).isTrue();
    }

    @Test
    public void testWebSocketConnectionInterruptionHandling() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Step 1: Simulate WebSocket disconnection
        // We assume the frontend exposes a JS function to close the WebSocket for testing
        js.executeScript("window.simulateWebSocketDisconnect && window.simulateWebSocketDisconnect();");

        // Step 2: Verify UI shows connection loss notification
        WebElement wsStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ws-connection-status")));
        wait.until(driver -> wsStatus.getText().equalsIgnoreCase("Disconnected"));
        assertThat(wsStatus.getText()).isEqualToIgnoringCase("Disconnected");

        WebElement notification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ws-connection-notification")));
        assertThat(notification.getText()).containsIgnoringCase("connection lost");

        // Verify no stale data is shown - approval requests list should not show outdated status
        WebElement approvalList = driver.findElement(By.id("approvalRequestsList"));
        // For demonstration, check that no element has class 'stale'
        boolean hasStale = approvalList.findElements(By.className("stale")).size() > 0;
        assertThat(hasStale).isFalse();

        // Step 3: Restore WebSocket connection
        js.executeScript("window.simulateWebSocketReconnect && window.simulateWebSocketReconnect();");

        // Step 4: Verify real-time updates resume
        wait.until(driver -> wsStatus.getText().equalsIgnoreCase("Connected"));
        assertThat(wsStatus.getText()).isEqualToIgnoringCase("Connected");

        // Verify notification disappears
        wait.until(ExpectedConditions.invisibilityOf(notification));

        // Verify approval requests list updates with current status
        // For demonstration, wait for an element with class 'updated' to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#approvalRequestsList .updated")));

        // Additional assertion: check that the updated element contains expected text
        WebElement updatedElement = driver.findElement(By.cssSelector("#approvalRequestsList .updated"));
        assertThat(updatedElement.getText()).isNotEmpty();
    }
}
