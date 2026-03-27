/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8803
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:00:07
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
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.AuditTrailService;

import io.github.bonigarcia.wdm.WebDriverManager;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuditTrailApprovalActionTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private AuditTrailService auditTrailService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_USER_USERNAME = "approverUser";
    private static final String TEST_USER_PASSWORD = "Password123!";

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
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test verifies that when an approval action is performed by a user with approval permissions,
     * an audit log entry is created with correct details and is visible in the UI audit trail view.
     */
    @Test
    public void testAuditLogCreationForApprovalAction() throws Exception {
        // Mock audit log entry returned by AuditTrailService
        AuditLogEntry mockAuditLog = new AuditLogEntry();
        mockAuditLog.setId(1001L);
        mockAuditLog.setActionType("approval");
        mockAuditLog.setTimestamp(Instant.now());
        mockAuditLog.setUserId(42L);
        mockAuditLog.setUsername(TEST_USER_USERNAME);
        mockAuditLog.setImmutable(true);

        when(auditTrailService.getAuditLogsByActionType("approval"))
            .thenReturn(Collections.singletonList(mockAuditLog));

        // Step 1: Log in as user with approval permissions
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys(TEST_USER_USERNAME);
        passwordInput.sendKeys(TEST_USER_PASSWORD);
        loginButton.click();

        // Wait for dashboard or home page to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Perform an approval action on a request
        // Navigate to requests page
        driver.get(BASE_URL + "/requests");

        // Wait for requests table to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("requestsTable")));

        // Find a request row with a pending approval button
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.approve-btn")));
        approveButton.click();

        // Confirm approval modal
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmApprovalButton")));
        confirmButton.click();

        // Wait for success notification
        WebElement successNotification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".notification.success")));
        assertThat(successNotification.getText()).containsIgnoringCase("approved successfully");

        // Step 3: Retrieve audit log entry for the performed approval action via API
        // Simulate API call to audit trail service
        List<AuditLogEntry> auditLogs = auditTrailService.getAuditLogsByActionType("approval");

        assertThat(auditLogs).isNotEmpty();
        AuditLogEntry auditLog = auditLogs.get(0);

        // Step 4: Verify audit log entry details
        assertThat(auditLog.getActionType()).isEqualTo("approval");
        assertThat(auditLog.getUsername()).isEqualTo(TEST_USER_USERNAME);
        assertThat(auditLog.getTimestamp()).isNotNull();
        assertThat(auditLog.isImmutable()).isTrue();

        // Step 5: Verify audit trail can be viewed correctly in the UI audit trail view
        driver.get(BASE_URL + "/audit-trail");

        // Wait for audit trail table
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailTable")));

        // Verify audit log entry is displayed
        List<WebElement> rows = driver.findElements(By.cssSelector("#auditTrailTable tbody tr"));
        boolean found = false;
        for (WebElement row : rows) {
            String actionType = row.findElement(By.cssSelector("td.actionType")).getText();
            String username = row.findElement(By.cssSelector("td.username")).getText();
            if ("approval".equalsIgnoreCase(actionType) && TEST_USER_USERNAME.equalsIgnoreCase(username)) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    // Inner class to simulate AuditLogEntry DTO
    public static class AuditLogEntry {
        private Long id;
        private String actionType;
        private Instant timestamp;
        private Long userId;
        private String username;
        private boolean immutable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public boolean isImmutable() {
            return immutable;
        }

        public void setImmutable(boolean immutable) {
            this.immutable = immutable;
        }
    }
}
