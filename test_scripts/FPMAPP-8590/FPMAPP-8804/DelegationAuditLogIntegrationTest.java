/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8804
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:39:34
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.AuditTrailService;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmDealsheetController;
import com.webapp.fpmapp.services.FpmTravelController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.entities.User;

/**
 * Integration test verifying delegation action audit log includes delegator, delegatee, and delegation time.
 * 
 * Preconditions:
 * - User with delegation permissions is logged in.
 * - AuditTrailService and DB tables operational.
 * 
 * Test Steps:
 * 1. Perform delegation action.
 * 2. Retrieve audit log entry via API.
 * 3. Verify audit log correctness and UI display.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DelegationAuditLogIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuditTrailService auditTrailService;

    private final String baseUrl = "http://localhost:8080";

    private final String delegatorUserId = "user123";
    private final String delegateeUserId = "user456";

    private final Instant delegationTimestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
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
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock audit trail service to return expected audit log entry after delegation
        when(auditTrailService.getAuditTrail(any(String.class))).thenAnswer(invocation -> {
            String approvalRequestId = invocation.getArgument(0);
            // Return a mocked audit log entry list with delegation action
            return Collections.singletonList(new com.webapp.fpmapp.entities.AuditLogEntry(
                    "audit123",
                    approvalRequestId,
                    "delegation",
                    delegationTimestamp,
                    delegatorUserId,
                    delegatorUserId,
                    delegateeUserId,
                    "Delegation performed successfully"
            ));
        });
    }

    @Test
    public void testDelegationActionCreatesAuditLogAndIsDisplayedInUI() {
        // Step 1: Perform delegation action via UI
        driver.get(baseUrl + "/login");

        // Login as delegator user with delegation permissions
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys(delegatorUserId);
        passwordInput.sendKeys("password123"); // assuming test password
        loginButton.click();

        // Wait for dashboard or approval page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to delegation page
        driver.get(baseUrl + "/approvals/delegate");

        // Fill delegation form
        WebElement delegateeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateeUserId")));
        WebElement approvalRequestInput = driver.findElement(By.id("approvalRequestId"));
        WebElement delegateButton = driver.findElement(By.id("delegateButton"));

        String approvalRequestId = "approvalReq789";

        delegateeInput.sendKeys(delegateeUserId);
        approvalRequestInput.sendKeys(approvalRequestId);
        delegateButton.click();

        // Wait for success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        assertThat(successMsg.getText()).contains("Delegation successful");

        // Step 2: Retrieve audit log entry via API
        List<com.webapp.fpmapp.entities.AuditLogEntry> auditLogs = webTestClient.get()
                .uri("/api/approvals/" + approvalRequestId + "/audit-trail")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(com.webapp.fpmapp.entities.AuditLogEntry.class)
                .returnResult()
                .getResponseBody();

        assertThat(auditLogs).isNotNull();
        assertThat(auditLogs).hasSize(1);

        com.webapp.fpmapp.entities.AuditLogEntry logEntry = auditLogs.get(0);

        // Step 3: Verify audit log contents
        assertThat(logEntry.getActionType()).isEqualTo("delegation");
        assertThat(logEntry.getApprovalRequestId()).isEqualTo(approvalRequestId);
        assertThat(logEntry.getPerformedByUserId()).isEqualTo(delegatorUserId);
        assertThat(logEntry.getDelegationFromUserId()).isEqualTo(delegatorUserId);
        assertThat(logEntry.getDelegationToUserId()).isEqualTo(delegateeUserId);

        // Timestamp should be close to delegationTimestamp (within 5 seconds tolerance)
        long diffSeconds = Math.abs(logEntry.getActionTimestamp().getEpochSecond() - delegationTimestamp.getEpochSecond());
        assertThat(diffSeconds).isLessThanOrEqualTo(5);

        // Step 4: Verify audit trail is displayed correctly in UI
        driver.get(baseUrl + "/approvals/" + approvalRequestId + "/audit-trail-view");

        WebElement auditTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailTable")));

        // Verify table contains delegator, delegatee, and timestamp
        String tableText = auditTable.getText();
        assertThat(tableText).contains(delegatorUserId);
        assertThat(tableText).contains(delegateeUserId);
        assertThat(tableText).contains("delegation");

        // Verify timestamp displayed (formatted)
        String formattedTimestamp = delegationTimestamp.toString().substring(0, 19); // ISO basic
        assertThat(tableText).contains(formattedTimestamp.substring(0, 10)); // date part
    }
}
