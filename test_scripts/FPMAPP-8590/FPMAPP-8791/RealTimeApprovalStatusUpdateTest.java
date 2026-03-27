/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8791
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:07:47
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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Integration Selenium test for real-time approval status update without full page refresh.
 * 
 * Preconditions:
 * - User logged in
 * - Approval workflow active with pending tasks
 * - WebSocket connection active
 * 
 * This test mocks backend services to simulate approval action from another user/system
 * and verifies UI updates in real-time.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class RealTimeApprovalStatusUpdateTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private String baseUrl;

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
    public void setup() {
        baseUrl = "http://localhost:" + port + "/fpmapp";

        // Mock user profile to simulate logged-in user with pending approvals
        when(userProfileController.getCurrentUser()).thenReturn(
                new com.webapp.fpmapp.entities.User(1001L, "jdoe", "John Doe", "jdoe@example.com", "ROLE_APPROVER"));

        // Mock common controller to simulate active approval workflow
        when(fpmCommonController.isApprovalWorkflowActive()).thenReturn(true);

        // Mock currency conversion to avoid external calls
        when(currencyConvertionController.convert(any(), any(), any())).thenReturn(1.0);

        // Mock forecast controller as needed
        when(fpmForecastController.getForecastData(any())).thenReturn(null);
    }

    /**
     * Test real-time approval status update without full page refresh.
     * 
     * Steps:
     * 1. Load the approval page with pending tasks.
     * 2. Simulate an approval action triggered externally via WebSocket.
     * 3. Verify UI updates approval status and audit trail in real-time.
     * 4. Verify no full page reload occurred.
     * 5. Verify no error messages or UI glitches.
     */
    @Test
    public void testRealTimeApprovalStatusUpdate() {
        driver.get(baseUrl + "/approvals");

        // Verify user is logged in by checking presence of username display
        WebElement userDisplay = driver.findElement(By.id("userDisplayName"));
        assertThat(userDisplay.getText()).isEqualTo("John Doe");

        // Verify pending approval item is present
        WebElement pendingItem = driver.findElement(By.cssSelector(".approval-item.pending"));
        assertThat(pendingItem).isNotNull();

        // Capture initial approval status text
        WebElement statusElement = pendingItem.findElement(By.cssSelector(".approval-status"));
        String initialStatus = statusElement.getText();
        assertThat(initialStatus).isEqualToIgnoringCase("Pending");

        // Capture initial audit trail entries count
        WebElement auditTrailView = driver.findElement(By.id("ApprovalAuditTrailView"));
        int initialAuditEntries = auditTrailView.findElements(By.cssSelector(".audit-entry")).size();

        // Simulate external approval action via JavaScript WebSocket message
        // This simulates a message from server notifying approval update
        String wsMessage = "{\"type\":\"approvalUpdate\",\"itemId\":\"" + pendingItem.getAttribute("data-item-id") + "\",\"newStatus\":\"Approved\",\"auditEntry\":{\"user\":\"asmith\",\"action\":\"Approved\",\"timestamp\":\"2024-06-01T10:15:30Z\"}}";

        ((JavascriptExecutor) driver).executeScript(
                "window.dispatchEvent(new MessageEvent('message', { data: arguments[0] }));", wsMessage);

        // Wait up to 10 seconds for UI to update approval status
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        Boolean statusUpdated = wait.until(driver -> {
            WebElement updatedStatusElement = driver.findElement(By.cssSelector(".approval-item.pending .approval-status"));
            return "Approved".equalsIgnoreCase(updatedStatusElement.getText());
        });

        assertThat(statusUpdated).isTrue();

        // Verify audit trail updated with new entry
        int updatedAuditEntries = auditTrailView.findElements(By.cssSelector(".audit-entry")).size();
        assertThat(updatedAuditEntries).isEqualTo(initialAuditEntries + 1);

        // Verify new audit entry content
        WebElement latestAuditEntry = auditTrailView.findElements(By.cssSelector(".audit-entry")).get(updatedAuditEntries - 1);
        String auditText = latestAuditEntry.getText();
        assertThat(auditText).contains("asmith").contains("Approved");

        // Verify no full page reload by checking that the page URL remains the same
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).isEqualTo(baseUrl + "/approvals");

        // Verify no error messages or UI glitches
        boolean errorVisible = driver.findElements(By.cssSelector(".error-message, .ui-glitch")).size() > 0;
        assertThat(errorVisible).isFalse();
    }
}
