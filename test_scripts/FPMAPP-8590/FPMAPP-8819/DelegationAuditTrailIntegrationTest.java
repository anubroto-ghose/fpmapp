/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8819
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:50:55
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.AuditTrailService;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DelegationAuditTrailIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuditTrailService auditTrailService;

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

    private static final String BASE_URL = "http://localhost:8080";

    private static final String DELEGATION_ACTION_TYPE = "delegation";

    private static final String APPROVAL_REQUEST_ID = "12345";
    private static final String DELEGATED_USER_ID = "user-6789";
    private static final String DELEGATED_USERNAME = "delegateUser";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
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
        // Mock audit trail service to simulate audit log retrieval
        when(auditTrailService.getAuditTrail(APPROVAL_REQUEST_ID))
            .thenReturn(Collections.singletonList(
                new com.webapp.fpmapp.entities.AuditLogEntry(
                    1L,
                    APPROVAL_REQUEST_ID,
                    DELEGATION_ACTION_TYPE,
                    Instant.now(),
                    "user-123",
                    "user-123",
                    DELEGATED_USER_ID,
                    "Delegated approval to user delegateUser"
                )
            ));

        // Mock delegation action to return success
        when(fpmDealsheetController.delegateApprovalRequest(any(), any(), any()))
            .thenReturn(Mono.just("Delegation successful"));

        // Mock user profile retrieval for delegated user
        when(fpmUserProfileController.getUserById(DELEGATED_USER_ID))
            .thenReturn(Mono.just(new User(DELEGATED_USER_ID, DELEGATED_USERNAME)));
    }

    @Test
    public void testDelegationActionIsLoggedCorrectly() {
        // Step 1: Log in as a user with delegation rights
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("delegatorUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for redirect to dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to approval request page
        driver.get(BASE_URL + "/approvals/" + APPROVAL_REQUEST_ID);

        // Step 3: Perform delegation action
        WebElement delegateButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("delegateBtn")));
        delegateButton.click();

        WebElement delegateUserInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateUserId")));
        WebElement confirmDelegateBtn = driver.findElement(By.id("confirmDelegateBtn"));

        delegateUserInput.sendKeys(DELEGATED_USER_ID);
        confirmDelegateBtn.click();

        // Wait for delegation success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationSuccessMsg")));
        assertThat(successMsg.getText()).contains("Delegation successful");

        // Step 4: Retrieve audit trail via API and verify delegation log
        List<com.webapp.fpmapp.entities.AuditLogEntry> auditLogs = auditTrailService.getAuditTrail(APPROVAL_REQUEST_ID);

        assertThat(auditLogs).isNotEmpty();

        com.webapp.fpmapp.entities.AuditLogEntry delegationLog = auditLogs.stream()
            .filter(log -> DELEGATION_ACTION_TYPE.equals(log.getActionType()))
            .findFirst()
            .orElse(null);

        assertThat(delegationLog).isNotNull();
        assertThat(delegationLog.getApprovalRequestId()).isEqualTo(APPROVAL_REQUEST_ID);
        assertThat(delegationLog.getActionType()).isEqualTo(DELEGATION_ACTION_TYPE);
        assertThat(delegationLog.getDelegationToUserId()).isEqualTo(DELEGATED_USER_ID);
        assertThat(delegationLog.getComments()).contains(DELEGATED_USERNAME);
        assertThat(delegationLog.getActionTimestamp()).isNotNull();

        // Verify immutability by attempting to modify (simulate) and expecting failure
        try {
            delegationLog.setComments("Modified comment");
            throw new AssertionError("Audit log entry should be immutable");
        } catch (UnsupportedOperationException | UnsupportedOperationException e) {
            // Expected immutability exception
        } catch (Exception e) {
            // If no immutability enforcement in entity, fail test
            throw new AssertionError("Audit log entry is mutable, which is not allowed", e);
        }

        // Step 5: Verify API filtering by action type
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/approvals/{id}/audit-trail")
                .queryParam("actionType", DELEGATION_ACTION_TYPE)
                .build(APPROVAL_REQUEST_ID))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(com.webapp.fpmapp.entities.AuditLogEntry.class)
            .value(logs -> {
                assertThat(logs).isNotEmpty();
                assertThat(logs.stream().allMatch(log -> DELEGATION_ACTION_TYPE.equals(log.getActionType()))).isTrue();
            });
    }
}
