/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8921
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 15:08:18
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuditLogApprovalDecisionTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_USER_ID = "approver123";
    private static final String TEST_REQUEST_ID = "REQ-20240601-001";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
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
        // Mock user profile with approval permissions
        User mockUser = new User();
        mockUser.setUserId(TEST_USER_ID);
        mockUser.setUsername("approverUser");
        mockUser.setRoles(Collections.singletonList("APPROVER"));
        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock audit log retrieval - initially empty
        when(fpmCommonController.getAuditLogEntries(TEST_REQUEST_ID))
            .thenReturn(Collections.emptyList());
    }

    /**
     * Test verifies that no audit log entry is created for a pending request.
     */
    @Test
    public void testNoAuditLogEntryForPendingRequest() {
        // Simulate login
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(TEST_USER_ID);
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.id("loginButton")).click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to request details page
        driver.get(BASE_URL + "/requests/" + TEST_REQUEST_ID);

        // Verify request status is pending
        WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("requestStatus")));
        String statusText = statusElement.getText();
        assertThat(statusText).isEqualToIgnoringCase("Pending");

        // Do NOT approve or reject - leave pending

        // Access audit log tab
        driver.findElement(By.id("auditLogTab")).click();

        // Wait for audit log entries to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditLogEntries")));

        // Verify no audit log entries are present
        List<WebElement> auditEntries = driver.findElements(By.cssSelector("#auditLogEntries .audit-entry"));
        assertThat(auditEntries).isEmpty();
    }

    /**
     * Test verifies audit log entry is created for rejected request with correct details.
     */
    @Test
    public void testAuditLogEntryForRejectedRequest() {
        // Prepare mock audit log entry for rejection
        LocalDateTime rejectionTime = LocalDateTime.now();
        String formattedTimestamp = rejectionTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        AuditLogEntry rejectionEntry = new AuditLogEntry();
        rejectionEntry.setRequestId(TEST_REQUEST_ID);
        rejectionEntry.setUserId(TEST_USER_ID);
        rejectionEntry.setTimestamp(formattedTimestamp);
        rejectionEntry.setDecision("Rejected");
        rejectionEntry.setImmutable(true);

        when(fpmCommonController.getAuditLogEntries(TEST_REQUEST_ID))
            .thenReturn(Collections.singletonList(rejectionEntry));

        // Simulate login
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(TEST_USER_ID);
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.id("loginButton")).click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to request details page
        driver.get(BASE_URL + "/requests/" + TEST_REQUEST_ID);

        // Verify request status is rejected
        WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("requestStatus")));
        String statusText = statusElement.getText();
        assertThat(statusText).isEqualToIgnoringCase("Rejected");

        // Access audit log tab
        driver.findElement(By.id("auditLogTab")).click();

        // Wait for audit log entries to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditLogEntries")));

        // Verify audit log entry is present
        List<WebElement> auditEntries = driver.findElements(By.cssSelector("#auditLogEntries .audit-entry"));
        assertThat(auditEntries).hasSize(1);

        WebElement entry = auditEntries.get(0);

        // Verify user ID
        WebElement userIdElement = entry.findElement(By.cssSelector(".audit-userId"));
        assertThat(userIdElement.getText()).isEqualTo(TEST_USER_ID);

        // Verify timestamp format and presence
        WebElement timestampElement = entry.findElement(By.cssSelector(".audit-timestamp"));
        assertThat(timestampElement.getText()).isEqualTo(formattedTimestamp);

        // Verify decision details
        WebElement decisionElement = entry.findElement(By.cssSelector(".audit-decision"));
        assertThat(decisionElement.getText()).isEqualToIgnoringCase("Rejected");

        // Verify immutability indicator (e.g. disabled edit button or lock icon)
        WebElement immutableIndicator = entry.findElement(By.cssSelector(".audit-immutable"));
        assertThat(immutableIndicator.isDisplayed()).isTrue();
    }

    // Inner class to simulate audit log entry DTO
    public static class AuditLogEntry {
        private String requestId;
        private String userId;
        private String timestamp;
        private String decision;
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

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getDecision() {
            return decision;
        }

        public void setDecision(String decision) {
            this.decision = decision;
        }

        public boolean isImmutable() {
            return immutable;
        }

        public void setImmutable(boolean immutable) {
            this.immutable = immutable;
        }
    }
}
