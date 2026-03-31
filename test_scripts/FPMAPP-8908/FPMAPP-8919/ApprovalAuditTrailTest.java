/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8919
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 15:10:06
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test for verifying audit trail logging of approval decisions.
 * 
 * Preconditions:
 * - User with approval permissions is logged in.
 * - A request is submitted and pending approval.
 * 
 * Test Steps:
 * 1. Approve the pending request.
 * 2. Access the audit log for the approved request.
 * 
 * Expected Results:
 * - Audit log entry contains approver's user ID.
 * - Timestamp recorded accurately.
 * - Decision details indicate approval.
 * - Audit log entry is immutable.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalAuditTrailTest {

    private static WebDriver driver;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_USER_ID = "approver123";
    private static final String TEST_REQUEST_ID = "REQ-20240601-001";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock logged-in user with approval permissions
        User mockUser = new User();
        mockUser.setUserId(TEST_USER_ID);
        mockUser.setUsername("approver.user");
        mockUser.setRoles(Collections.singletonList("ROLE_APPROVER"));
        when(userProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock audit log retrieval with a sample immutable audit entry
        AuditLogEntry auditEntry = new AuditLogEntry();
        auditEntry.setRequestId(TEST_REQUEST_ID);
        auditEntry.setUserId(TEST_USER_ID);
        auditEntry.setTimestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        auditEntry.setDecisionDetails("Approved request");
        auditEntry.setImmutable(true);

        when(fpmCommonController.getAuditLogForRequest(TEST_REQUEST_ID))
            .thenReturn(List.of(auditEntry));

        // Mock approval action to succeed
        when(fpmCommonController.approveRequest(TEST_REQUEST_ID, TEST_USER_ID))
            .thenReturn(true);
    }

    @Test
    public void testApprovalDecisionIsLoggedCorrectly() {
        try {
            // Step 1: Login as approver (simulate login by navigating to login page and submitting form)
            driver.get(BASE_URL + "/login");
            WebElement usernameInput = driver.findElement(By.id("username"));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginBtn"));

            usernameInput.sendKeys("approver.user");
            passwordInput.sendKeys("securePassword123");
            loginButton.click();

            // Wait for redirect to dashboard
            Thread.sleep(1500);

            // Step 2: Navigate to pending requests page
            driver.get(BASE_URL + "/requests/pending");

            // Find the pending request by ID
            WebElement requestRow = driver.findElement(By.xpath("//tr[td/text()='" + TEST_REQUEST_ID + "']"));
            assertThat(requestRow).isNotNull();

            // Click approve button
            WebElement approveButton = requestRow.findElement(By.cssSelector("button.approve-btn"));
            approveButton.click();

            // Wait for approval processing
            Thread.sleep(1000);

            // Step 3: Navigate to audit log page for the request
            driver.get(BASE_URL + "/requests/" + TEST_REQUEST_ID + "/audit-log");

            // Verify audit log entry
            WebElement auditLogTable = driver.findElement(By.id("auditLogTable"));
            List<WebElement> rows = auditLogTable.findElements(By.tagName("tr"));
            assertThat(rows).isNotEmpty();

            boolean foundApprovalEntry = false;
            for (WebElement row : rows) {
                List<WebElement> cols = row.findElements(By.tagName("td"));
                if (cols.size() < 3) continue;

                String userId = cols.get(0).getText();
                String timestampStr = cols.get(1).getText();
                String decision = cols.get(2).getText();

                if (userId.equals(TEST_USER_ID) && decision.toLowerCase().contains("approve")) {
                    foundApprovalEntry = true;

                    // Validate timestamp is recent (within last 5 minutes)
                    Instant timestamp = Instant.parse(timestampStr);
                    Instant now = Instant.now();
                    long diffSeconds = Math.abs(now.getEpochSecond() - timestamp.getEpochSecond());
                    assertThat(diffSeconds).isLessThanOrEqualTo(300);

                    // Check immutability indicator (e.g. a lock icon or disabled edit button)
                    WebElement immutableIcon = row.findElement(By.cssSelector(".immutable-icon"));
                    assertThat(immutableIcon).isNotNull();
                    assertThat(immutableIcon.isDisplayed()).isTrue();

                    break;
                }
            }

            assertThat(foundApprovalEntry).isTrue();

        } catch (Exception e) {
            e.printStackTrace();
            assertThat(false).as("Test failed due to exception: " + e.getMessage()).isTrue();
        }
    }

    // Inner class to mock audit log entry
    public static class AuditLogEntry {
        private String requestId;
        private String userId;
        private Instant timestamp;
        private String decisionDetails;
        private boolean immutable;

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
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

        public String getDecisionDetails() {
            return decisionDetails;
        }

        public void setDecisionDetails(String decisionDetails) {
            this.decisionDetails = decisionDetails;
        }

        public boolean isImmutable() {
            return immutable;
        }

        public void setImmutable(boolean immutable) {
            this.immutable = immutable;
        }
    }
}
