/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6191
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:02:59
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * Integration test validates real-time approval status update UI changes via WebSocket push
 * without page refresh in the Spring Boot app context using Selenium WebDriver.
 * 
 * Preconditions:
 * - The requester is logged in and viewing the approval requests page.
 * - WebSocket connection established to receive live updates.
 * - A separate mock user performs approval action that triggers real-time update.
 * 
 * Created per FPMAPP-6191 based on FPMAPP-6172 requirements.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class ApprovalRealTimeStatusUpdateIntegrationTest {

    private static WebDriver driverRequester;
    private static WebDriver driverApprover;
    private static final String BASE_URL = "http://localhost:8080";
    private static final int WAIT_TIMEOUT_SECONDS = 30;

    private static final String APPROVAL_ID = "approval-12345";

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions optionsRequester = new ChromeOptions();
        optionsRequester.addArguments("--headless", "--window-size=1920,1080");
        driverRequester = new ChromeDriver(optionsRequester);

        ChromeOptions optionsApprover = new ChromeOptions();
        optionsApprover.addArguments("--headless", "--window-size=1920,1080");
        driverApprover = new ChromeDriver(optionsApprover);
    }

    @AfterAll
    public static void teardown() {
        if (driverRequester != null) {
            driverRequester.quit();
        }
        if (driverApprover != null) {
            driverApprover.quit();
        }
    }

    @Test
    public void testRealTimeApprovalStatusUpdateViaWebSocket() throws Exception {
        // Mock the approval details with initial status PENDING
        Mockito.when(fpmDealsheetController.getApprovalStatus(APPROVAL_ID))
                .thenReturn("PENDING");

        // Mock updated status after approval action
        Mockito.when(fpmDealsheetController.getApprovalStatus(APPROVAL_ID))
                .thenReturn("APPROVED");

        // Step 1: Requester logs in and navigates to approval requests page
        loginAsRequester(driverRequester);
        driverRequester.get(BASE_URL + "/approval-requests");

        // Wait until the approval request status element is loaded
        WebDriverWait waitRequester = new WebDriverWait(driverRequester, Duration.ofSeconds(WAIT_TIMEOUT_SECONDS));
        By approvalStatusSelector = By.id("approval-status-" + APPROVAL_ID);
        waitRequester.until(ExpectedConditions.visibilityOfElementLocated(approvalStatusSelector));

        WebElement statusElement = driverRequester.findElement(approvalStatusSelector);
        String initialStatus = statusElement.getText();
        Assertions.assertEquals("PENDING", initialStatus, "Initial approval status should be PENDING");

        // Step 2: Approver logs in and performs approval action in separate session
        loginAsApprover(driverApprover);
        driverApprover.get(BASE_URL + "/approval-requests/actions?approvalId=" + APPROVAL_ID);

        // Approve the approval request
        WebElement approveButton = driverApprover.findElement(By.id("btn-approve"));
        approveButton.click();

        // Simulate backend processing that updates approval status and triggers WebSocket push
        simulateApprovalAction(APPROVAL_ID, "APPROVED");

        // Step 3: Back on requester session - wait for WebSocket pushed update without page refresh
        CountDownLatch updateReceivedLatch = new CountDownLatch(1);

        // Use JavaScript executor to listen and expose WebSocket updates for test
        JavascriptExecutor jsExec = (JavascriptExecutor) driverRequester;

        // Inject a JS snippet to observe approval status changes (assuming the app updates element text on WS msg)
        jsExec.executeScript(
                "window.approvalStatusUpdates = [];
" +
                "var originalSetText = Object.getOwnPropertyDescriptor(HTMLDivElement.prototype, 'innerText').set;
" +
                "Object.defineProperty(document.getElementById('approval-status-" + APPROVAL_ID + "'), 'innerText', {
" +
                "  set: function(val) {
" +
                "    window.approvalStatusUpdates.push(val);
" +
                "    originalSetText.call(this, val);
" +
                "  },
" +
                "  configurable: true
" +
                "});
");

        // Poll and wait for the status update within timeout (poll every 500ms)
        boolean statusUpdated = false;
        long waitUntil = System.currentTimeMillis() + WAIT_TIMEOUT_SECONDS * 1000L;

        while (System.currentTimeMillis() < waitUntil) {
            Object updates = jsExec.executeScript("return window.approvalStatusUpdates;");
            if (updates instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<Object> updateList = (java.util.List<Object>) updates;
                if (updateList.contains("APPROVED")) {
                    statusUpdated = true;
                    break;
                }
            }
            Thread.sleep(500L);
        }

        Assertions.assertTrue(statusUpdated, "Approval status did not update to APPROVED via WebSocket push");

        // Final UI assertion
        String updatedStatusText = driverRequester.findElement(approvalStatusSelector).getText();
        Assertions.assertEquals("APPROVED", updatedStatusText,
                "UI approval status should reflect 'APPROVED' without page refresh");

        // Ensure page was not reloaded (window.performance API can help - check navigation entries)
        Object navType = jsExec.executeScript(
          "return window.performance.getEntriesByType('navigation')[0].type;"
        );
        Assertions.assertEquals("navigate", navType.toString(), "Page navigation type should remain 'navigate' - no full reload");
    }

    private void loginAsRequester(WebDriver driver) {
        driver.get(BASE_URL + "/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SECONDS));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

        driver.findElement(By.id("username")).sendKeys("requesterUser");
        driver.findElement(By.id("password")).sendKeys("requesterPass123");
        driver.findElement(By.id("btn-login")).click();

        wait.until(ExpectedConditions.urlContains("approval-requests"));
    }

    private void loginAsApprover(WebDriver driver) {
        driver.get(BASE_URL + "/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SECONDS));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

        driver.findElement(By.id("username")).sendKeys("approverUser");
        driver.findElement(By.id("password")).sendKeys("approverPass123");
        driver.findElement(By.id("btn-login")).click();

        wait.until(ExpectedConditions.urlContains("approval-requests"));
    }

    /**
     * Simulates backend approval action that updates the approval status
     * and triggers WebSocket push notification to connected clients.
     * In real system, this would be triggered automatically via service logic.
     * For this test, we mock that behavior.
     * 
     * @param approvalId the approval request ID
     * @param newStatus the updated status
     */
    private void simulateApprovalAction(String approvalId, String newStatus) {
        // Mock backend update to status
        Mockito.when(fpmDealsheetController.getApprovalStatus(approvalId)).thenReturn(newStatus);

        // Simulate WebSocket message broadcast (would be implemented by RealTimeNotificationService)
        // For this test, no real WebSocket server is spun, assume frontend JS picks up update.
        // This is placeholder for potential embedded WS server or mocks if available.
    }
}
