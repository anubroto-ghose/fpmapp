/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6235
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:29:45
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import static org.mockito.Mockito.*;

/**
 * Integration test for verifying the audit log query reflects status changes and actions correctly
 * on a travel request, fulfilling ticket FPMAPP-6168-TC03.
 * 
 * This test uses Selenium WebDriver to simulate UI API calls and verify displayed audit logs.
 * Services are mocked to simulate backend responses.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class AuditLogTravelRequestTest {

    private WebDriver driver;

    @Autowired
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private final String baseUrl = "http://localhost:8080";

    private final String travelRequestId = "TRV-20250915-0001";

    /**
     * Mock audit log entries matching multiple approval actions on the travel request.
     */
    private final List<AuditLogRecord> mockAuditLogs = List.of(
        new AuditLogRecord("1", travelRequestId, "user-approver1", "APPROVE", Instant.parse("2025-09-14T10:15:30Z"), "PENDING", "APPROVED", "Approved by level 1 approver"),
        new AuditLogRecord("2", travelRequestId, "user-delegator", "DELEGATE", Instant.parse("2025-09-14T12:00:00Z"), "APPROVED", "DELEGATED", "Delegated approval to user-approver2"),
        new AuditLogRecord("3", travelRequestId, "user-approver2", "REJECT", Instant.parse("2025-09-14T13:45:00Z"), "DELEGATED", "REJECTED", "Rejected due to missing documents")
    );

    /**
     * POJO representing audit log details
     */
    private static class AuditLogRecord {
        public final String auditId;
        public final String approvalId;
        public final String userId;
        public final String actionType;
        public final Instant actionTimestamp;
        public final String previousStatus;
        public final String newStatus;
        public final String remarks;

        public AuditLogRecord(String auditId, String approvalId, String userId, String actionType,
                              Instant actionTimestamp, String previousStatus, String newStatus, String remarks) {
            this.auditId = auditId;
            this.approvalId = approvalId;
            this.userId = userId;
            this.actionType = actionType;
            this.actionTimestamp = actionTimestamp;
            this.previousStatus = previousStatus;
            this.newStatus = newStatus;
            this.remarks = remarks;
        }
    }

    @BeforeAll
    public void setup() {
        // Setup ChromeDriver with headless option for CI environment compatibility
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver"); // Adjust path as per environment
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);

        // Mock the FpmCommonController audit log query method to return mock data
        when(fpmCommonController.getAuditLogByApprovalId(eq(travelRequestId)))
            .thenReturn(mockAuditLogs);
    }

    @AfterAll
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Audit log query returns complete and accurate travel request approval audit records")
    public void testAuditLogTravelRequests() {
        try {
            // Navigate to travel request audit log UI endpoint
            String auditLogsUrl = baseUrl + "/travel/audit/" + travelRequestId;
            driver.get(auditLogsUrl);

            // Wait for audit log panel to load
            WebElement auditLogTable = waitForElement(By.id("audit-log-table"), Duration.ofSeconds(10));
            assertThat(auditLogTable).isNotNull();

            // Retrieve all rows from the audit log table body
            List<WebElement> rows = auditLogTable.findElements(By.cssSelector("tbody tr"));

            // Validate number of records equals number of mocked audit logs
            assertThat(rows).hasSize(mockAuditLogs.size());

            for (int i = 0; i < mockAuditLogs.size(); i++) {
                AuditLogRecord expected = mockAuditLogs.get(i);
                WebElement row = rows.get(i);

                // Extract columns
                List<WebElement> cols = row.findElements(By.tagName("td"));
                assertThat(cols).hasSize(7);

                String userId = cols.get(0).getText().trim();
                String actionType = cols.get(1).getText().trim();
                String actionTimestamp = cols.get(2).getText().trim();
                String prevStatus = cols.get(3).getText().trim();
                String newStatus = cols.get(4).getText().trim();
                String remarks = cols.get(5).getText().trim();
                String auditId = cols.get(6).getText().trim();

                // Assert all fields are non-empty
                assertThat(auditId).isNotEmpty();
                assertThat(userId).isEqualTo(expected.userId);
                assertThat(actionType).isEqualToIgnoringCase(expected.actionType);
                // Parse displayed timestamp and compare (allowing seconds tolerance)
                Instant uiTimestamp = Instant.parse(actionTimestamp);
                long diffSeconds = Math.abs(uiTimestamp.getEpochSecond() - expected.actionTimestamp.getEpochSecond());
                assertThat(diffSeconds).isLessThanOrEqualTo(2);
                assertThat(prevStatus).isEqualToIgnoringCase(expected.previousStatus);
                assertThat(newStatus).isEqualToIgnoringCase(expected.newStatus);
                assertThat(remarks).isEqualTo(expected.remarks);
            }

            // Additional performance related check can be done by measuring response time in real environment
            // Here, just confirm presence and correctness

        } catch (NoSuchElementException nse) {
            throw new AssertionError("Expected audit log element not found", nse);
        } catch (Exception e) {
            throw new AssertionError("Unexpected error during audit log travel request test", e);
        }
    }

    /**
     * Utility to wait for presence of an element by locator
     * @param locator The element locator
     * @param timeout Max duration to wait
     * @return WebElement if found
     * @throws RuntimeException if element not found in time
     */
    private WebElement waitForElement(By locator, Duration timeout) {
        long endTime = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < endTime) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (NoSuchElementException ignored) {
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {}
        }
        throw new RuntimeException("Timeout waiting for element: " + locator);
    }
}
