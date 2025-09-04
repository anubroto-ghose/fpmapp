/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5268
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:16:32
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

// Using SpringBootTest with random port to run integration test with WebDriver
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class FpmDealSheetApprovalIntegrationTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private final String APPROVAL_REQUEST_ID = "request-1234";
    private final String APPROVER_USER_ID = "user-approver-001";
    private final String REQUESTER_USER_ID = "user-requester-001";

    @BeforeEach
    public void setUp() {
        // Set ChromeDriver location according to your environment
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock authenticated user with approval rights
        when(fpmUserProfileController.getCurrentUserRole()).thenReturn("FinancialManager");
        when(fpmUserProfileController.getCurrentUserId()).thenReturn(APPROVER_USER_ID);

        // Mock fetching pending approval requests
        when(fpmDealsheetController.getPendingApprovalRequests(any()))
            .thenReturn(java.util.List.of(
                new com.webapp.fpmapp.dto.FpmDealsheetController.ApprovalRequest(
                    APPROVAL_REQUEST_ID, "Deal Sheet #123", REQUESTER_USER_ID, "Pending", "Manager", null)));

        // Mock approval processing
        when(fpmDealsheetController.approveDealSheet(APPROVAL_REQUEST_ID, APPROVER_USER_ID, "FinancialManager"))
            .thenReturn(true);

        // Mock notification send
        when(fpmCommonController.sendNotification(REQUESTER_USER_ID, "Your deal sheet request #123 has been approved."))
            .thenReturn(true);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testRoleBasedApprovalWorkflow_success() {
        try {
            String baseUrl = "http://localhost:" + port + "/approvals";

            driver.get(baseUrl);

            // Wait for approvals list to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalRequestsTable")));

            // Select first deal sheet approval request
            WebElement firstRow = driver.findElement(By.cssSelector("#approvalRequestsTable tbody tr"));
            assertTrue(firstRow.isDisplayed(), "First approval request row should be visible");

            // Click on the row to view details
            firstRow.click();

            // Wait for deal sheet detail panel/section
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dealSheetDetail")));

            // Check approval status is 'Pending'
            WebElement statusLabel = driver.findElement(By.id("approvalStatus"));
            assertTrue(statusLabel.getText().equalsIgnoreCase("Pending"), "Approval status initially should be Pending");

            // Click 'Approve' button
            WebElement approveButton = driver.findElement(By.id("approveButton"));
            approveButton.click();

            // Wait for confirmation message
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalSuccessMessage")));
            WebElement successMsg = driver.findElement(By.id("approvalSuccessMessage"));
            assertTrue(successMsg.getText().contains("successfully approved"), "Success message should be displayed after approval");

            // Verify that the status label updates to Approved
            wait.until(ExpectedConditions.textToBe(By.id("approvalStatus"), "Approved"));
            WebElement updatedStatusLabel = driver.findElement(By.id("approvalStatus"));
            assertTrue(updatedStatusLabel.getText().equalsIgnoreCase("Approved"), "Approval status should update to Approved");

            // Verify notification sent (mock verified implicitly by mocking)
            boolean notificationSent = fpmCommonController.sendNotification(REQUESTER_USER_ID, "Your deal sheet request #123 has been approved.");
            assertTrue(notificationSent, "Notification should be sent to requester after approval");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Test failed due to exception: " + e.getMessage());
        }
    }
}
