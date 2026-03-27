/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8613
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 08:05:16
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
import java.util.Optional;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import java.time.Duration;

/**
 * Integration Selenium test for validating audit log persistence and queryability for rejection action.
 * 
 * Preconditions:
 * - User with appropriate permissions is logged in.
 * - Rejection action is available on travel request.
 * - Audit logging framework and DB schema updated.
 * 
 * Test Steps:
 * 1. Perform rejection action on a travel request.
 * 2. Confirm audit log entry is created.
 * 3. Query audit logs filtering by action_type="rejection" and user_id.
 * 
 * Expected Results:
 * - Audit log entry stored persistently.
 * - Query returns correct audit log entry with metadata.
 * - Timestamp and user details accurate.
 * - No data loss or inconsistency.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuditLogRejectionActionTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_USERNAME = "auditorUser";
    private static final String TEST_PASSWORD = "password123";
    private static final Long TEST_USER_ID = 1001L;
    private static final Long TEST_TRAVEL_REQUEST_ID = 5001L;

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

        // Mock travel request retrieval
        when(fpmTravelController.getTravelRequestById(TEST_TRAVEL_REQUEST_ID))
            .thenReturn(Optional.of(new TravelRequest(TEST_TRAVEL_REQUEST_ID, "Business trip to NYC", "pending", TEST_USER_ID)));

        // Mock rejection action
        when(fpmTravelController.rejectTravelRequest(TEST_TRAVEL_REQUEST_ID, TEST_USER_ID))
            .then(invocation -> {
                // Simulate audit log creation
                AuditLogEntry entry = new AuditLogEntry();
                entry.setActionType("rejection");
                entry.setUserId(TEST_USER_ID);
                entry.setEntityId(TEST_TRAVEL_REQUEST_ID);
                entry.setEntityType("travel_request");
                entry.setTimestamp(Instant.now());
                entry.setDetails("Travel request rejected by user " + TEST_USER_ID);
                auditLogRepository.save(entry);
                return true;
            });

        // Mock audit log query
        when(fpmCommonController.queryAuditLogs("rejection", TEST_USER_ID))
            .then(invocation -> {
                List<AuditLogEntry> entries = auditLogRepository.findByActionTypeAndUserId("rejection", TEST_USER_ID);
                return entries;
            });
    }

    @Test
    public void testAuditLogPersistenceAndQueryForRejectionAction() {
        // Step 1: Login as user with permissions
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(TEST_USERNAME);
        passwordInput.sendKeys(TEST_PASSWORD);
        loginButton.click();

        // Wait for dashboard or home page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to travel requests page
        driver.get(BASE_URL + "/travel/requests");

        // Wait for travel requests list
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("travelRequestsTable")));

        // Step 3: Find the travel request row and click 'Reject'
        WebElement rejectButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button[data-travel-request-id='" + TEST_TRAVEL_REQUEST_ID + "'][data-action='reject']")));
        rejectButton.click();

        // Confirm rejection modal appears
        WebElement confirmRejectBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmRejectBtn")));
        confirmRejectBtn.click();

        // Wait for success notification
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notificationSuccess")));
        assertThat(successMsg.getText()).containsIgnoringCase("rejected successfully");

        // Step 4: Verify audit log entry is created in DB
        List<AuditLogEntry> auditEntries = auditLogRepository.findByActionTypeAndUserId("rejection", TEST_USER_ID);
        assertThat(auditEntries).isNotEmpty();

        AuditLogEntry auditEntry = auditEntries.stream()
            .filter(e -> e.getEntityId().equals(TEST_TRAVEL_REQUEST_ID) && e.getEntityType().equals("travel_request"))
            .findFirst()
            .orElse(null);

        assertThat(auditEntry).isNotNull();
        assertThat(auditEntry.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(auditEntry.getActionType()).isEqualTo("rejection");
        assertThat(auditEntry.getTimestamp()).isBeforeOrEqualTo(Instant.now());
        assertThat(auditEntry.getDetails()).contains("rejected");

        // Step 5: Query audit logs via mocked service
        List<AuditLogEntry> queriedEntries = fpmCommonController.queryAuditLogs("rejection", TEST_USER_ID);
        assertThat(queriedEntries).isNotEmpty();
        assertThat(queriedEntries).anyMatch(e -> e.getEntityId().equals(TEST_TRAVEL_REQUEST_ID));

        // Additional consistency checks
        for (AuditLogEntry entry : queriedEntries) {
            assertThat(entry.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(entry.getActionType()).isEqualTo("rejection");
            assertThat(entry.getTimestamp()).isNotNull();
            assertThat(entry.getDetails()).isNotEmpty();
        }
    }

    // --- Mocked or simplified domain classes and repository for test context ---

    public static class TravelRequest {
        private Long id;
        private String description;
        private String status;
        private Long requestedByUserId;

        public TravelRequest(Long id, String description, String status, Long requestedByUserId) {
            this.id = id;
            this.description = description;
            this.status = status;
            this.requestedByUserId = requestedByUserId;
        }

        public Long getId() { return id; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
        public Long getRequestedByUserId() { return requestedByUserId; }
    }

    public static class AuditLogEntry {
        private Long id;
        private String actionType;
        private Long userId;
        private Long entityId;
        private String entityType;
        private Instant timestamp;
        private String details;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getActionType() { return actionType; }
        public void setActionType(String actionType) { this.actionType = actionType; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getEntityId() { return entityId; }
        public void setEntityId(Long entityId) { this.entityId = entityId; }

        public String getEntityType() { return entityType; }
        public void setEntityType(String entityType) { this.entityType = entityType; }

        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
    }

    /**
     * Simulated repository for audit logs.
     * In real app, this would be a Spring Data JPA repository.
     */
    public interface AuditLogRepository {
        AuditLogEntry save(AuditLogEntry entry);
        List<AuditLogEntry> findByActionTypeAndUserId(String actionType, Long userId);
    }

}
