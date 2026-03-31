/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8937
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:52:44
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
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import org.springframework.boot.test.web.server.LocalManagementPort;

/**
 * Integration test verifying real-time approval status updates in the UI.
 * 
 * Preconditions:
 * - User logged in with appropriate role-based permissions.
 * - Approval requests exist and are pending or in progress.
 * - WebSocket connection is active.
 * 
 * This test mocks backend services and simulates a WebSocket message to verify UI updates without page refresh.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RealTimeApprovalStatusUpdateIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private final String baseUrl = "http://localhost:";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Mock user profile with role-based permissions
        when(fpmUserProfileController.getCurrentUserRole()).thenReturn("APPROVER");

        // Mock approval requests data
        when(fpmCommonController.getApprovalRequests(any())).thenReturn(MockData.getMockApprovalRequests());

        // Mock currency conversion and forecast services as needed
        when(currencyConvertionController.convertCurrency(any(), any(), any())).thenReturn(1.0);
        when(fpmForecastController.getForecastData(any())).thenReturn(MockData.getMockForecastData());
    }

    @Test
    public void testRealTimeApprovalStatusUpdates() throws Exception {
        driver.get(baseUrl + port + "/fpm/approvals");

        // Wait for the approval requests list to be visible
        WebElement approvalList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approval-requests-list")));

        // Verify initial status of a known approval request
        WebElement approvalItem = approvalList.findElement(By.cssSelector("li[data-approval-id='12345']"));
        WebElement statusIndicator = approvalItem.findElement(By.className("approval-status"));
        String initialStatus = statusIndicator.getText();
        assertThat(initialStatus).isEqualToIgnoringCase("Pending");

        // Simulate WebSocket message for status update
        // We inject JavaScript to simulate receiving a WebSocket message that updates the approval status
        String script = "var event = new CustomEvent('approvalStatusUpdate', { detail: { approvalId: '12345', newStatus: 'Approved', delegationStatus: 'Delegated', roleBasedIndicator: 'Approver' } });" +
                        "document.dispatchEvent(event);";

        ((JavascriptExecutor) driver).executeScript(script);

        // Wait for the UI to update dynamically
        wait.until(ExpectedConditions.textToBePresentInElement(statusIndicator, "Approved"));

        // Verify updated status text
        String updatedStatus = statusIndicator.getText();
        assertThat(updatedStatus).isEqualToIgnoringCase("Approved");

        // Verify delegation status indicator is shown
        WebElement delegationIndicator = approvalItem.findElement(By.className("delegation-status"));
        assertThat(delegationIndicator.getText()).isEqualToIgnoringCase("Delegated");

        // Verify role-based approval indicator
        WebElement roleIndicator = approvalItem.findElement(By.className("role-based-indicator"));
        assertThat(roleIndicator.getText()).isEqualToIgnoringCase("Approver");

        // Verify no page refresh occurred by checking URL remains the same
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).endsWith("/fpm/approvals");
    }

    /**
     * Mock data provider for approval requests and forecast data.
     */
    private static class MockData {

        static java.util.List<java.util.Map<String, Object>> getMockApprovalRequests() {
            java.util.Map<String, Object> approval1 = new java.util.HashMap<>();
            approval1.put("approvalId", "12345");
            approval1.put("status", "Pending");
            approval1.put("delegationStatus", "None");
            approval1.put("roleBasedIndicator", "Approver");

            java.util.Map<String, Object> approval2 = new java.util.HashMap<>();
            approval2.put("approvalId", "67890");
            approval2.put("status", "In Progress");
            approval2.put("delegationStatus", "Delegated");
            approval2.put("roleBasedIndicator", "Delegate");

            return java.util.Arrays.asList(approval1, approval2);
        }

        static java.util.Map<String, Object> getMockForecastData() {
            java.util.Map<String, Object> forecast = new java.util.HashMap<>();
            forecast.put("forecastId", "f123");
            forecast.put("value", 100000);
            forecast.put("currency", "USD");
            return forecast;
        }
    }
}
