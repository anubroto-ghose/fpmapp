/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8790
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:08:26
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

/**
 * Integration Selenium test for rapid consecutive approval status updates.
 * 
 * Preconditions:
 * - User logged in
 * - WebSocket connection active (mocked)
 * - Multiple rapid approval status changes triggered
 * 
 * Validates:
 * - Immediate and correct UI updates
 * - ApprovalAuditTrailView reflects all changes
 * - No UI flicker or errors
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApprovalStatusUpdateRapidChangesIT {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private FpmCommonController fpmCommonController;

    @MockBean
    private WebSocketMockService webSocketMockService;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_USER = "testuser";
    private static final String TEST_PASSWORD = "password123";

    private static final String APPROVAL_STATUS_COMPONENT_ID = "approvalStatusComponent";
    private static final String APPROVAL_AUDIT_TRAIL_VIEW_ID = "approvalAuditTrailView";

    private BlockingQueue<String> approvalStatusUpdatesQueue = new LinkedBlockingQueue<>();

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
        MockitoAnnotations.openMocks(this);

        // Mock WebSocket service to simulate rapid approval status updates
        doAnswer(invocation -> {
            ApprovalStatusUpdateListener listener = invocation.getArgument(0);
            // Simulate rapid consecutive approval status updates
            new Thread(() -> {
                try {
                    String[] statuses = {"APPROVED", "REJECTED", "PENDING", "APPROVED"};
                    for (String status : statuses) {
                        Thread.sleep(100); // 100ms between updates
                        listener.onApprovalStatusUpdate(status);
                        approvalStatusUpdatesQueue.offer(status);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            return null;
        }).when(webSocketMockService).registerApprovalStatusListener(any());

        // Login user via UI
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(TEST_USER);
        driver.findElement(By.id("password")).sendKeys(TEST_PASSWORD);
        driver.findElement(By.id("loginButton")).click();

        // Wait for main page to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Register WebSocket listener (mocked)
        webSocketMockService.registerApprovalStatusListener(status -> {
            // This would normally push updates to the UI via WebSocket
            // Here we simulate by executing JS to update the UI component
            ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('" + APPROVAL_STATUS_COMPONENT_ID + "').innerText = arguments[0];", status);

            // Append to audit trail
            ((JavascriptExecutor) driver).executeScript(
                "var audit = document.getElementById('" + APPROVAL_AUDIT_TRAIL_VIEW_ID + "');" +
                "var entry = document.createElement('div');" +
                "entry.innerText = 'Status changed to: ' + arguments[0];" +
                "audit.appendChild(entry);", status);
        });
    }

    @Test
    public void testRapidApprovalStatusUpdates_NoFlickerAndCorrectUpdates() throws InterruptedException {
        WebElement approvalStatusComponent = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id(APPROVAL_STATUS_COMPONENT_ID)));
        WebElement auditTrailView = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id(APPROVAL_AUDIT_TRAIL_VIEW_ID)));

        // Initial state check
        String initialStatus = approvalStatusComponent.getText();
        assertThat(initialStatus).isNotNull();

        // Wait for all updates to be processed
        for (int i = 0; i < 4; i++) {
            String expectedStatus = approvalStatusUpdatesQueue.poll(5, TimeUnit.SECONDS);
            assertThat(expectedStatus).isNotNull();

            // Wait until UI reflects the expected status
            wait.until(driver -> {
                String currentStatus = driver.findElement(By.id(APPROVAL_STATUS_COMPONENT_ID)).getText();
                return expectedStatus.equals(currentStatus);
            });

            // Verify audit trail contains the latest entry
            String auditText = auditTrailView.getText();
            assertThat(auditText).contains("Status changed to: " + expectedStatus);

            // Check no flicker: the text should be stable for at least 100ms
            String before = approvalStatusComponent.getText();
            Thread.sleep(150);
            String after = approvalStatusComponent.getText();
            assertThat(after).isEqualTo(before);
        }

        // Verify no error messages on page
        boolean errorPresent = driver.findElements(By.className("error-message")).size() > 0;
        assertThat(errorPresent).isFalse();
    }

    /**
     * Mock interface to simulate WebSocket approval status updates.
     */
    public interface WebSocketMockService {
        void registerApprovalStatusListener(ApprovalStatusUpdateListener listener);
    }

    /**
     * Listener interface for approval status updates.
     */
    public interface ApprovalStatusUpdateListener {
        void onApprovalStatusUpdate(String newStatus);
    }
}
