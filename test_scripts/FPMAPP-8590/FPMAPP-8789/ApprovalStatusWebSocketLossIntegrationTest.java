/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8789
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:00:34
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.Duration;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test for real-time approval status update UI behavior when WebSocket connection is lost.
 * 
 * Preconditions:
 * - User logged in
 * - WebSocket connection lost
 * - Approval status changed by another user/session
 * 
 * Verifies that UI does not update approval status in real-time,
 * no full page reload occurs, and connection error indicator is shown.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApprovalStatusWebSocketLossIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:8080/fpmapp";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        // Mock currency conversion service to avoid external calls
        Mockito.when(currencyConvertionController.getExchangeRate(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(1.0);

        // Mock forecast and common controllers as needed
        Mockito.when(fpmForecastController.getForecastData(Mockito.anyString()))
                .thenReturn("{}" /* empty JSON as string */);
        Mockito.when(fpmCommonController.getCommonData())
                .thenReturn("{}" /* empty JSON as string */);

        // Navigate to login page and perform login
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("TestPassword123");
        loginButton.click();

        // Wait for main page to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void testApprovalStatusDoesNotUpdateWhenWebSocketLost() throws InterruptedException {
        // Navigate to approvals page
        driver.get(BASE_URL + "/approvals");

        // Wait for approval list to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".approval-item")));

        // Locate an approval item and get its initial status text
        WebElement approvalItem = driver.findElement(By.cssSelector(".approval-item[data-request-id='12345']"));
        WebElement statusElement = approvalItem.findElement(By.cssSelector(".approval-status"));
        String initialStatus = statusElement.getText();

        // Simulate WebSocket connection loss by overriding the WebSocket object in browser
        ((JavascriptExecutor) driver).executeScript(
                "if(window.AppWebSocket) { window.AppWebSocket.close(); window.AppWebSocket = null; }"
        );

        // Verify UI shows connection lost indicator
        WebElement connectionIndicator = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ws-connection-status")));
        assertThat(connectionIndicator.getText().toLowerCase()).contains("disconnected");

        // Simulate backend approval status change triggered by another user/session
        // This would normally be pushed via WebSocket, but since WS is lost, UI should not update
        // We simulate this by directly changing the DOM via JS to mimic a backend update attempt
        ((JavascriptExecutor) driver).executeScript(
                "var statusElem = document.querySelector('.approval-item[data-request-id=\'12345\'] .approval-status');" +
                "if(statusElem) { statusElem.textContent = 'Approved'; }"
        );

        // Wait briefly to simulate time passing
        TimeUnit.SECONDS.sleep(3);

        // Verify that the approval status text remains unchanged (no real-time update)
        String currentStatus = statusElement.getText();
        assertThat(currentStatus).isEqualTo(initialStatus);

        // Verify no full page reload occurred by checking URL remains the same
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("/approvals");

        // Verify no error messages related to approval update failures are shown
        boolean errorMessagePresent = driver.findElements(By.cssSelector(".error-message")).stream()
                .anyMatch(e -> e.getText().toLowerCase().contains("approval update failed"));
        assertThat(errorMessagePresent).isFalse();
    }
}
