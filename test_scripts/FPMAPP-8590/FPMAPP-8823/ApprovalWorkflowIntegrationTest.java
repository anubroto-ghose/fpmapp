/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8823
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:47:18
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalWorkflowIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user profile with approver role
        when(fpmUserProfileController.getCurrentUserRole()).thenReturn("Approver");

        // Mock currency conversion to return 1:1 for simplicity
        when(currencyConvertionController.convertCurrency(any(), any(), any())).thenReturn(1.0);

        // Mock forecast controller responses if needed
        when(fpmForecastController.getForecastData(any())).thenReturn(new HashMap<>());

        // Mock common controller approval workflow status tracking
        when(fpmCommonController.isApprovalWorkflowActive()).thenReturn(true);
    }

    @Test
    public void testApprovalWorkflowStatusTransitionsAndLogging() {
        driver.get(BASE_URL + "/approval-requests");

        // Wait for the approval requests list to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalRequestsTable")));

        // Select the first pending approval request
        WebElement firstRequest = driver.findElement(By.cssSelector("#approvalRequestsTable tbody tr[data-status='pending']"));
        assertThat(firstRequest).isNotNull();

        String requestId = firstRequest.getAttribute("data-request-id");

        // Approve the request
        WebElement approveButton = firstRequest.findElement(By.cssSelector("button.approve-btn"));
        approveButton.click();

        // Wait for status update
        wait.until(ExpectedConditions.textToBePresentInElement(firstRequest.findElement(By.cssSelector("td.status")), "Approved"));

        String approvedStatus = firstRequest.findElement(By.cssSelector("td.status")).getText();
        assertThat(approvedStatus).isEqualToIgnoringCase("Approved");

        // Verify that approver role and timestamp are logged
        WebElement approvedLog = firstRequest.findElement(By.cssSelector("td.log"));
        String approvedLogText = approvedLog.getText();
        assertThat(approvedLogText).contains("Approver");
        assertThat(isValidTimestampInLog(approvedLogText)).isTrue();

        // Reset for next test - reload page
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalRequestsTable")));

        // Select the same request again (simulate a new pending request for reject test)
        WebElement rejectRequest = driver.findElement(By.cssSelector("#approvalRequestsTable tbody tr[data-request-id='" + requestId + "']"));

        // Reject the request
        WebElement rejectButton = rejectRequest.findElement(By.cssSelector("button.reject-btn"));
        rejectButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(rejectRequest.findElement(By.cssSelector("td.status")), "Rejected"));

        String rejectedStatus = rejectRequest.findElement(By.cssSelector("td.status")).getText();
        assertThat(rejectedStatus).isEqualToIgnoringCase("Rejected");

        WebElement rejectedLog = rejectRequest.findElement(By.cssSelector("td.log"));
        String rejectedLogText = rejectedLog.getText();
        assertThat(rejectedLogText).contains("Approver");
        assertThat(isValidTimestampInLog(rejectedLogText)).isTrue();

        // Reset for delegation test
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalRequestsTable")));

        // Select a request with delegation permission
        WebElement delegateRequest = driver.findElement(By.cssSelector("#approvalRequestsTable tbody tr[data-delegation='true']"));
        assertThat(delegateRequest).isNotNull();

        WebElement delegateButton = delegateRequest.findElement(By.cssSelector("button.delegate-btn"));
        delegateButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(delegateRequest.findElement(By.cssSelector("td.status")), "Delegated"));

        String delegatedStatus = delegateRequest.findElement(By.cssSelector("td.status")).getText();
        assertThat(delegatedStatus).isEqualToIgnoringCase("Delegated");

        WebElement delegatedLog = delegateRequest.findElement(By.cssSelector("td.log"));
        String delegatedLogText = delegatedLog.getText();
        assertThat(delegatedLogText).contains("Approver");
        assertThat(isValidTimestampInLog(delegatedLogText)).isTrue();

        // Verify user perspective view of current approval status
        driver.get(BASE_URL + "/user/approval-status?requestId=" + delegateRequest.getAttribute("data-request-id"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currentApprovalStatus")));

        WebElement currentStatus = driver.findElement(By.id("currentApprovalStatus"));
        String currentStatusText = currentStatus.getText();
        assertThat(currentStatusText).isIn("Approved", "Rejected", "Delegated", "Pending");
    }

    private boolean isValidTimestampInLog(String logText) {
        // Example log format: "Status changed by Approver at 2024-06-01T14:30:00"
        try {
            String[] parts = logText.split(" at ");
            if (parts.length < 2) {
                return false;
            }
            String timestampStr = parts[1].trim();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime.parse(timestampStr, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
