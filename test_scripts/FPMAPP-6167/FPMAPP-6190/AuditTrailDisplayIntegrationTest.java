/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6190
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:03:49
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // Run on a defined port for WebDriver
@ActiveProfiles("test")
public class AuditTrailDisplayIntegrationTest {

    private static WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private FpmUserProfileController userProfileController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes ChromeDriver executable in system PATH or configured)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @BeforeEach
    public void setup() {
        // Mock backend API response for /api/fpm/approvals/audit for approvalId=123
        List<MockAuditEntry> mockAuditEntries = Arrays.asList(
                new MockAuditEntry("2025-09-10T14:30:00Z", "APPROVAL", "approverUser1", "Approved the request", null),
                new MockAuditEntry("2025-09-11T09:15:00Z", "DELEGATION", "managerUser2", "Delegated to delegateUser3", "delegateUser3"),
                new MockAuditEntry("2025-09-12T16:45:00Z", "REJECTION", "delegateUser3", "Rejected due to missing docs", null),
                new MockAuditEntry("2025-09-13T08:00:00Z", "OVERRIDE", "adminUser4", "Currency override applied", null)
        );

        // Here we simulate stubbing/mock, but since this is Selenium + SpringBoot integration, 
        // often the API returns real mock data or a test database is used.
        // For demonstration, let's assume the controller method returning audit entries is mocked:
        Mockito.when(fpmCommonController.getAuditTrailForApproval(123L))
                .thenReturn(mockAuditEntries);

        // Normally, more infrastructure is needed to intercept REST calls or use WireMock etc. 
        // For simplicity, assume the backend is prepared to return static mocked data.
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAuditTrailDisplaysAllRecordsWithDetails() {
        // 1. Navigate to the approval requests page
        driver.get(BASE_URL + "/approvals");

        // Check page title or header to verify loaded
        assertTrue(driver.getTitle().contains("Approvals"), "Approval page title check");

        // 2. Select an approval request with known audit trail (id=123 assumed)
        // Let's assume each approval has a clickable element with data-approval-id attribute
        WebElement approvalItem = driver.findElement(By.cssSelector("div.approval-item[data-approval-id='123']"));
        assertNotNull(approvalItem, "Approval item 123 should be present.");
        approvalItem.click();

        // Wait until audit trail panel/button is visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement auditTrailButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-view-audit-trail")));

        // 3. Open the audit trail panel
        auditTrailButton.click();

        // Wait for audit trail panel/modal to appear
        WebElement auditPanel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("audit-trail-panel")));
        assertTrue(auditPanel.isDisplayed(), "Audit trail panel should be visible.");

        // 4. Inspect the audit trail entries in the panel
        List<WebElement> auditEntries = auditPanel.findElements(By.cssSelector(".audit-entry"));
        assertFalse(auditEntries.isEmpty(), "Audit entries should be displayed.");

        // Assert each entry details for timestamp, action type, user identity, and delegation details
        boolean foundApproval = false;
        boolean foundDelegation = false;
        boolean foundRejection = false;
        boolean foundOverride = false;

        for (WebElement entry : auditEntries) {
            String timestamp = entry.findElement(By.cssSelector(".audit-timestamp")).getText();
            String actionType = entry.findElement(By.cssSelector(".audit-action-type")).getText();
            String user = entry.findElement(By.cssSelector(".audit-user")).getText();

            assertNotNull(timestamp, "Timestamp should be present.");
            assertNotNull(actionType, "Action type should be present.");
            assertNotNull(user, "User identity should be present.");

            if (actionType.equalsIgnoreCase("APPROVAL")) {
                assertEquals("approverUser1", user, "Approval user mismatch.");
                assertTrue(timestamp.contains("2025-09-10"), "Approval timestamp matches expected date.");
                foundApproval = true;
            }

            if (actionType.equalsIgnoreCase("DELEGATION")) {
                String delegationDetail = entry.findElement(By.cssSelector(".audit-delegation-details")).getText();
                assertTrue(delegationDetail.contains("delegateUser3"), "Delegation details show delegatee.");
                assertEquals("managerUser2", user, "Delegation actor user mismatch.");
                foundDelegation = true;
            }

            if (actionType.equalsIgnoreCase("REJECTION")) {
                assertEquals("delegateUser3", user, "Rejection user mismatch.");
                assertTrue(timestamp.contains("2025-09-12"), "Rejection timestamp matches expected date.");
                foundRejection = true;
            }

            if (actionType.equalsIgnoreCase("OVERRIDE")) {
                assertEquals("adminUser4", user, "Override user mismatch.");
                assertTrue(timestamp.contains("2025-09-13"), "Override timestamp matches expected date.");
                foundOverride = true;
            }
        }

        assertTrue(foundApproval, "Audit trail includes approval entry.");
        assertTrue(foundDelegation, "Audit trail includes delegation entry.");
        assertTrue(foundRejection, "Audit trail includes rejection entry.");
        assertTrue(foundOverride, "Audit trail includes override entry.");
    }

    /**
     * Helper class to simulate audit entries returned from mocked service
     */
    public static class MockAuditEntry {
        private String actionTimestamp;
        private String actionType;
        private String userId;
        private String remarks;
        private String delegationDetails; // nullable

        public MockAuditEntry(String actionTimestamp, String actionType, String userId, String remarks, String delegationDetails) {
            this.actionTimestamp = actionTimestamp;
            this.actionType = actionType;
            this.userId = userId;
            this.remarks = remarks;
            this.delegationDetails = delegationDetails;
        }

        public String getActionTimestamp() {
            return actionTimestamp;
        }

        public String getActionType() {
            return actionType;
        }

        public String getUserId() {
            return userId;
        }

        public String getRemarks() {
            return remarks;
        }

        public String getDelegationDetails() {
            return delegationDetails;
        }
    }
}
