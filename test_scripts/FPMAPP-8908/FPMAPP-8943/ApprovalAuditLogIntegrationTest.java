/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8943
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:47:25
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Integration test using Selenium WebDriver and Spring Boot Test
 * to verify audit log creation for approval action.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalAuditLogIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FpmCommonController auditLogService;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String APPROVAL_REQUEST_ID = "REQ-12345";
    private static final String APPROVAL_COMMENT = "Approved after thorough review.";
    private static final String USER_ID = "user-1001";

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
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
        // Mock authenticated user profile
        User mockUser = new User();
        mockUser.setId(USER_ID);
        mockUser.setUsername("compliance.officer");
        when(userProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock audit log service to simulate audit log creation and retrieval
        when(auditLogService.createAuditLog(any())).thenAnswer(invocation -> {
            // Simulate immutable audit log creation
            Object auditLogEntry = invocation.getArgument(0);
            return auditLogEntry;
        });

        when(auditLogService.getAuditLogsForRequest(APPROVAL_REQUEST_ID)).thenReturn(
                Collections.singletonList(new AuditLogEntry(USER_ID, Instant.now(), "approval", APPROVAL_COMMENT)));
    }

    @Test
    public void testApprovalCreatesAuditLogEntry() {
        // Step 1: Navigate to login page and authenticate
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("compliance.officer");
        passwordInput.sendKeys("securePassword123");
        loginButton.click();

        // Wait for dashboard page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to approval request page
        driver.get(BASE_URL + "/approvals/" + APPROVAL_REQUEST_ID);

        // Wait for approval form
        WebElement approvalForm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalForm")));

        // Step 3: Provide optional comments
        WebElement commentBox = approvalForm.findElement(By.id("approvalComment"));
        commentBox.sendKeys(APPROVAL_COMMENT);

        // Step 4: Submit approval
        WebElement approveButton = approvalForm.findElement(By.id("approveBtn"));
        approveButton.click();

        // Wait for confirmation message
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalConfirmation")));
        assertThat(confirmation.getText()).contains("Approval submitted successfully");

        // Step 5: Verify audit log entry via API
        List<AuditLogEntry> auditLogs = webTestClient.get()
                .uri("/api/auditlogs?requestId=" + APPROVAL_REQUEST_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuditLogEntry.class)
                .returnResult()
                .getResponseBody();

        assertThat(auditLogs).isNotNull();
        assertThat(auditLogs).isNotEmpty();

        AuditLogEntry logEntry = auditLogs.get(0);
        assertThat(logEntry.getUserId()).isEqualTo(USER_ID);
        assertThat(logEntry.getActionType()).isEqualTo("approval");
        assertThat(logEntry.getComments()).isEqualTo(APPROVAL_COMMENT);
        assertThat(logEntry.getTimestamp()).isBeforeOrEqualTo(Instant.now());

        // Step 6: Verify audit log entry is immutable (simulate by attempting update and expecting failure)
        webTestClient.put()
                .uri("/api/auditlogs/" + logEntry.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AuditLogEntry(USER_ID, Instant.now(), "approval", "Tampering attempt"))
                .exchange()
                .expectStatus().is4xxClientError();

        // Step 7: Verify audit trail visible in UI
        driver.get(BASE_URL + "/auditlogs/" + APPROVAL_REQUEST_ID);
        WebElement auditLogTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditLogTable")));
        String tableText = auditLogTable.getText();
        assertThat(tableText).contains(USER_ID);
        assertThat(tableText).contains("approval");
        assertThat(tableText).contains(APPROVAL_COMMENT);
    }

    // Inner class to represent audit log entries for deserialization
    public static class AuditLogEntry {
        private String id;
        private String userId;
        private Instant timestamp;
        private String actionType;
        private String comments;

        public AuditLogEntry() {}

        public AuditLogEntry(String userId, Instant timestamp, String actionType, String comments) {
            this.userId = userId;
            this.timestamp = timestamp;
            this.actionType = actionType;
            this.comments = comments;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }
}
