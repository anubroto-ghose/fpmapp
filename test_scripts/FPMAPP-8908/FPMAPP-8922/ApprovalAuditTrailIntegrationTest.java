/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8922
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 15:07:14
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Integration test verifying audit trail logging of approval decisions.
 * 
 * Preconditions:
 * - User with approval permissions is logged in.
 * - A request is submitted and pending approval.
 * 
 * Test Steps:
 * 1. Approve the pending request using a valid user account.
 * 2. Access the audit log for the approved request.
 * 
 * Expected Results:
 * - Audit log entry is created with user identity, timestamp, decision details.
 * - Audit log entry is immutable.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApprovalAuditTrailIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private final String baseUrl = "http://localhost:";

    private static final String TEST_USERNAME = "approverUser";
    private static final String TEST_PASSWORD = "Password123!";
    private static final long TEST_REQUEST_ID = 1001L;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
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
        // Mock user profile with approval permissions
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(TEST_USERNAME);
        mockUser.setRoles(Collections.singletonList("ROLE_APPROVER"));

        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock audit log retrieval after approval
        AuditLogEntry mockAuditLogEntry = new AuditLogEntry();
        mockAuditLogEntry.setRequestId(TEST_REQUEST_ID);
        mockAuditLogEntry.setUserId(mockUser.getId());
        mockAuditLogEntry.setUsername(mockUser.getUsername());
        mockAuditLogEntry.setDecision("APPROVED");
        mockAuditLogEntry.setTimestamp(LocalDateTime.now());
        mockAuditLogEntry.setImmutable(true);

        when(fpmCommonController.getAuditLogEntriesForRequest(TEST_REQUEST_ID))
            .thenReturn(Collections.singletonList(mockAuditLogEntry));
    }

    @Test
    public void testApprovalDecisionIsLoggedWithAuditTrail() throws InterruptedException {
        driver.get(baseUrl + port + "/login");

        // Login as approver user
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys(TEST_USERNAME);
        passwordInput.sendKeys(TEST_PASSWORD);
        loginButton.click();

        // Wait for redirect to dashboard
        Thread.sleep(1000);

        // Navigate to pending requests page
        driver.get(baseUrl + port + "/requests/pending");

        // Find the request row by request id
        WebElement requestRow = driver.findElement(By.xpath("//tr[td/text()='" + TEST_REQUEST_ID + "']"));
        assertThat(requestRow).isNotNull();

        // Click approve button
        WebElement approveButton = requestRow.findElement(By.cssSelector("button.approve-request"));
        approveButton.click();

        // Wait for approval processing
        Thread.sleep(1000);

        // Navigate to audit log page for the request
        driver.get(baseUrl + port + "/requests/" + TEST_REQUEST_ID + "/audit-log");

        // Verify audit log entry
        List<WebElement> auditEntries = driver.findElements(By.cssSelector(".audit-log-entry"));
        assertThat(auditEntries).isNotEmpty();

        WebElement approvalEntry = auditEntries.stream()
            .filter(e -> e.getText().contains("APPROVED") && e.getText().contains(TEST_USERNAME))
            .findFirst()
            .orElse(null);

        assertThat(approvalEntry).isNotNull();

        // Verify timestamp format and presence
        String entryText = approvalEntry.getText();
        assertThat(entryText).containsPattern("\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}");

        // Verify immutability indicator
        WebElement immutableIndicator = approvalEntry.findElement(By.cssSelector(".immutable-indicator"));
        assertThat(immutableIndicator).isNotNull();
        assertThat(immutableIndicator.getText()).containsIgnoringCase("immutable");
    }

    // Inner class to mock audit log entry DTO
    public static class AuditLogEntry {
        private long requestId;
        private long userId;
        private String username;
        private String decision;
        private LocalDateTime timestamp;
        private boolean immutable;

        public long getRequestId() {
            return requestId;
        }

        public void setRequestId(long requestId) {
            this.requestId = requestId;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getDecision() {
            return decision;
        }

        public void setDecision(String decision) {
            this.decision = decision;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public boolean isImmutable() {
            return immutable;
        }

        public void setImmutable(boolean immutable) {
            this.immutable = immutable;
        }
    }
}
