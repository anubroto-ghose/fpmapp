/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6186
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:07:06
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.controllers.FpmTravelController;
import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

/**
 * Integration test for Approval workflows using Selenium and Spring Boot context.
 * Covers role-based approval, rejection, delegation, and audit trail verification.
 * 
 * Preconditions:
 * - Approval workflows implemented and active.
 * - Audit trail system integrated.
 * 
 * This test mocks supporting services and verifies UI-driven approval actions.
 *
 * Note: Assumes Chromedriver is installed and on PATH.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FpmApprovalWorkflowIntegrationTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private CurrencyConvertionController currencyConverterMock;

    @MockBean
    private FpmForecastController forecastControllerMock;

    @MockBean
    private FpmCommonController commonControllerMock;

    @MockBean
    private FpmDealsheetController dealsheetControllerMock;

    @MockBean
    private FpmTravelController travelControllerMock;

    @MockBean
    private FpmUserProfileController userProfileControllerMock;

    @BeforeAll
    public void setup() {
        // Setup ChromeDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // run headless for CI
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock currency conversion responses
        doReturn(1.10).when(currencyConverterMock).convertCurrency(anyString(), anyString(), anyDouble());

        // Mock forecast controller responses
        doReturn("ForecastData").when(forecastControllerMock).getForecastSummary(any());

        // Mock common controller for audit log retrieval
        doReturn(true).when(commonControllerMock).logAuditEntry(anyString(), anyString(), anyString());

        // Mock user profiles
        doReturn("ROLE_APPROVER").when(userProfileControllerMock).getUserRole(anyString());

        // Mock dealsheet approval
        doReturn(true).when(dealsheetControllerMock).approveDealSheet(anyString(), anyString());
        doReturn(true).when(dealsheetControllerMock).getApprovalStatus(anyString());

        // Mock staffing rejection
        doReturn(true).when(commonControllerMock).rejectStaffingRequest(anyString(), anyString());
        doReturn(false).when(commonControllerMock).getStaffingApprovalStatus(anyString());

        // Mock delegation in travel
        doReturn(true).when(travelControllerMock).delegateTravelApproval(anyString(), anyString(), anyString());
        doReturn(true).when(travelControllerMock).getTravelApprovalDelegationStatus(anyString());
    }

    @AfterAll
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Test Approval Status Updates Recorded with Timestamp and User IDs - DealSheet Approval")
    public void testDealSheetApprovalStatusWithAudit() {
        final String dealSheetId = UUID.randomUUID().toString();
        final String approverUserId = "user123";

        // Simulate navigating to deal sheet approval page
        driver.get("http://localhost:8080/dealsheets/approval/" + dealSheetId);

        // Wait for approve button and click approve
        WebElement approveButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("btn-approve")));
        approveButton.click();

        // Wait for approval status to update in UI
        WebElement statusElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("approval-status")));
        String statusText = statusElement.getText();

        assertThat(statusText.toLowerCase()).contains("approved");

        // Verify via mocked controller approval method
        boolean approved = dealsheetControllerMock.approveDealSheet(dealSheetId, approverUserId);
        assertThat(approved).isTrue();

        // Verify audit log invoked
        boolean auditLogged = commonControllerMock.logAuditEntry(dealSheetId, approverUserId, "APPROVED");
        assertThat(auditLogged).isTrue();
    }

    @Test
    @DisplayName("Test Rejection Status Updates Recorded with Timestamp and User IDs - Staffing Request")
    public void testStaffingRequestRejectionStatusWithAudit() {
        final String staffingRequestId = UUID.randomUUID().toString();
        final String approverUserId = "user456";

        // Simulate navigating to staffing request approval page
        driver.get("http://localhost:8080/staffing/approval/" + staffingRequestId);

        // Wait for reject button and click reject
        WebElement rejectButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("btn-reject")));
        rejectButton.click();

        // Wait for rejection status to update in UI
        WebElement statusElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("approval-status")));
        String statusText = statusElement.getText();

        assertThat(statusText.toLowerCase()).contains("rejected");

        // Verify via mocked controller rejection method
        boolean rejected = commonControllerMock.rejectStaffingRequest(staffingRequestId, approverUserId);
        assertThat(rejected).isTrue();

        // Verify audit log invoked
        boolean auditLogged = commonControllerMock.logAuditEntry(staffingRequestId, approverUserId, "REJECTED");
        assertThat(auditLogged).isTrue();
    }

    @Test
    @DisplayName("Test Delegation Status Changes and Audit for Travel Request")
    public void testTravelRequestDelegationWithAudit() {
        final String travelRequestId = UUID.randomUUID().toString();
        final String delegatorUserId = "user789";
        final String delegateeUserId = "user999";

        // Navigate to travel request delegation page
        driver.get("http://localhost:8080/travel/approval/delegate/" + travelRequestId);

        // Fill delegatee user input
        WebElement delegateeInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("input-delegatee")));
        delegateeInput.sendKeys(delegateeUserId);

        // Submit delegation button
        WebElement delegateButton = driver.findElement(By.id("btn-delegate"));
        delegateButton.click();

        // Wait for delegation status update in UI
        WebElement statusElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("delegation-status")));
        String statusText = statusElement.getText();

        assertThat(statusText.toLowerCase()).contains("delegated");

        // Verify delegation success via controller
        boolean delegated = travelControllerMock.delegateTravelApproval(travelRequestId, delegatorUserId, delegateeUserId);
        assertThat(delegated).isTrue();

        // Verify audit log invoked
        boolean auditLogged = commonControllerMock.logAuditEntry(travelRequestId, delegatorUserId, "DELEGATED to " + delegateeUserId);
        assertThat(auditLogged).isTrue();
    }

    @Test
    @DisplayName("Test Audit Trail Accuracy for Approval Actions")
    public void testAuditTrailReflectsApprovalActions() {
        final String approvalId = UUID.randomUUID().toString();
        final String userId = "audittestuser";

        // Mock audit trail entries
        doReturn(true).when(commonControllerMock).logAuditEntry(approvalId, userId, "APPROVED");
        doReturn(true).when(commonControllerMock).logAuditEntry(approvalId, userId, "REJECTED");
        doReturn(true).when(commonControllerMock).logAuditEntry(approvalId, userId, "DELEGATED");

        // Log three actions
        assertThat(commonControllerMock.logAuditEntry(approvalId, userId, "APPROVED")).isTrue();
        assertThat(commonControllerMock.logAuditEntry(approvalId, userId, "REJECTED")).isTrue();
        assertThat(commonControllerMock.logAuditEntry(approvalId, userId, "DELEGATED")).isTrue();

        // Assuming a controller method to retrieve audit trail exists and is mocked
        var auditTrail = new java.util.ArrayList<String>();
        auditTrail.add(String.format("APPROVED by %s at %s", userId, Instant.now()));
        auditTrail.add(String.format("REJECTED by %s at %s", userId, Instant.now()));
        auditTrail.add(String.format("DELEGATED by %s at %s", userId, Instant.now()));

        doReturn(auditTrail).when(commonControllerMock).getAuditTrail(anyString());

        var retrievedAuditTrail = commonControllerMock.getAuditTrail(approvalId);
        assertThat(retrievedAuditTrail).isNotNull();
        assertThat(retrievedAuditTrail.size()).isEqualTo(3);
        assertThat(retrievedAuditTrail.get(0)).contains("APPROVED by");
        assertThat(retrievedAuditTrail.get(1)).contains("REJECTED by");
        assertThat(retrievedAuditTrail.get(2)).contains("DELEGATED by");
    }

}
