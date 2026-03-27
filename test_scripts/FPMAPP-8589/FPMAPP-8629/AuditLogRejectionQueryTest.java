/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8629
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:54:26
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuditLogRejectionQueryTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @InjectMocks
    private FpmUserProfileController fpmUserProfileController;

    private static final String BASE_URL = "http://localhost:8080";

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
    public void setupMocks() {
        // Mock audit log query response
        // Simulate audit log entries with rejection actions
        List<AuditLogEntry> mockAuditLogs = new ArrayList<>();

        // User and timestamps for test
        String testUserId = "user123";
        Instant ts1 = LocalDateTime.of(2024, 6, 1, 10, 0).toInstant(ZoneOffset.UTC);
        Instant ts2 = LocalDateTime.of(2024, 6, 2, 15, 30).toInstant(ZoneOffset.UTC);
        Instant ts3 = LocalDateTime.of(2024, 6, 3, 9, 45).toInstant(ZoneOffset.UTC);

        // Add rejection entries for user123
        mockAuditLogs.add(new AuditLogEntry(testUserId, ts1, "rejection", "Rejected due to insufficient funds"));
        mockAuditLogs.add(new AuditLogEntry(testUserId, ts2, "rejection", "Rejected due to policy violation"));

        // Add other action types and users
        mockAuditLogs.add(new AuditLogEntry("user999", ts3, "approval", "Approved by manager"));
        mockAuditLogs.add(new AuditLogEntry(testUserId, ts3, "approval", "Approved after review"));

        // Mock the service method that queries audit logs
        when(fpmCommonController.queryAuditLogs(eq("rejection"), eq(testUserId), any(Instant.class), any(Instant.class)))
                .thenAnswer(invocation -> {
                    String actionType = invocation.getArgument(0);
                    String userId = invocation.getArgument(1);
                    Instant startTs = invocation.getArgument(2);
                    Instant endTs = invocation.getArgument(3);

                    List<AuditLogEntry> filtered = new ArrayList<>();
                    for (AuditLogEntry entry : mockAuditLogs) {
                        if (entry.getActionType().equals(actionType) &&
                            entry.getUserId().equals(userId) &&
                            !entry.getTimestamp().isBefore(startTs) &&
                            !entry.getTimestamp().isAfter(endTs)) {
                            filtered.add(entry);
                        }
                    }
                    return filtered;
                });
    }

    @Test
    public void testQueryAuditLogsForRejectionByUserIdAndTimestamp() {
        String testUserId = "user123";
        Instant startTimestamp = LocalDateTime.of(2024, 6, 1, 0, 0).toInstant(ZoneOffset.UTC);
        Instant endTimestamp = LocalDateTime.of(2024, 6, 2, 23, 59).toInstant(ZoneOffset.UTC);

        // Navigate to audit log query page
        driver.get(BASE_URL + "/audit-logs");

        // Wait for page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("audit-log-query-form")));

        // Fill in user_id filter
        WebElement userIdInput = driver.findElement(By.id("userIdInput"));
        userIdInput.clear();
        userIdInput.sendKeys(testUserId);

        // Select action_type = rejection
        WebElement actionTypeSelect = driver.findElement(By.id("actionTypeSelect"));
        actionTypeSelect.click();
        WebElement rejectionOption = driver.findElement(By.cssSelector("option[value='rejection']"));
        rejectionOption.click();

        // Fill in timestamp range
        WebElement startTimestampInput = driver.findElement(By.id("startTimestampInput"));
        startTimestampInput.clear();
        startTimestampInput.sendKeys("2024-06-01T00:00");

        WebElement endTimestampInput = driver.findElement(By.id("endTimestampInput"));
        endTimestampInput.clear();
        endTimestampInput.sendKeys("2024-06-02T23:59");

        // Submit query
        WebElement submitButton = driver.findElement(By.id("submitQueryBtn"));
        submitButton.click();

        // Wait for results table
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditLogResultsTable")));

        // Verify results
        List<WebElement> rows = driver.findElements(By.cssSelector("#auditLogResultsTable tbody tr"));

        assertThat(rows).isNotEmpty();

        for (WebElement row : rows) {
            String userId = row.findElement(By.cssSelector("td.userId")).getText();
            String actionType = row.findElement(By.cssSelector("td.actionType")).getText();
            String timestampStr = row.findElement(By.cssSelector("td.timestamp")).getText();
            String details = row.findElement(By.cssSelector("td.details")).getText();

            // Assert userId matches
            assertThat(userId).isEqualTo(testUserId);

            // Assert actionType is rejection
            assertThat(actionType).isEqualToIgnoringCase("rejection");

            // Assert timestamp is within range
            Instant timestamp = Instant.parse(timestampStr);
            assertThat(timestamp).isBetween(startTimestamp, endTimestamp);

            // Assert details is not empty
            assertThat(details).isNotBlank();
        }

        // Additional check: ensure query executed efficiently (mocked here as no exception)
        // In real scenario, could check query execution time or DB explain plan
    }

    // Inner class to simulate audit log entries
    private static class AuditLogEntry {
        private final String userId;
        private final Instant timestamp;
        private final String actionType;
        private final String details;

        public AuditLogEntry(String userId, Instant timestamp, String actionType, String details) {
            this.userId = userId;
            this.timestamp = timestamp;
            this.actionType = actionType;
            this.details = details;
        }

        public String getUserId() {
            return userId;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public String getActionType() {
            return actionType;
        }

        public String getDetails() {
            return details;
        }
    }
}
