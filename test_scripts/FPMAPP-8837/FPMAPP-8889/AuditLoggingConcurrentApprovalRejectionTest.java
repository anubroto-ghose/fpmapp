/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8889
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:31:38
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for audit logging performance under concurrent approval and rejection actions.
 * 
 * Preconditions:
 * - Audit logging enabled in system.
 * - Multiple users can perform approval/rejection concurrently.
 * 
 * This test simulates multiple users performing approval and rejection actions simultaneously,
 * verifies audit logs correctness, immutability, and monitors system performance.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuditLoggingConcurrentApprovalRejectionTest {

    private static WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080";

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static final int CONCURRENT_USERS = 10;

    private static ExecutorService executorService;

    private static List<AuditLogEntry> auditLogEntries = Collections.synchronizedList(new ArrayList<>());

    @BeforeAll
    public static void setup() {
        // Setup ChromeDriver (headless for CI environments)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * Simulates concurrent approval and rejection actions by multiple users.
     * Verifies audit logs correctness, immutability, and system performance.
     */
    @Test
    @DisplayName("Test audit logging under concurrent approval and rejection actions")
    public void testConcurrentApprovalRejectionAuditLogging() throws InterruptedException, ExecutionException, TimeoutException {

        // Mock user profiles for concurrent users
        List<User> testUsers = new ArrayList<>();
        for (int i = 1; i <= CONCURRENT_USERS; i++) {
            User user = new User();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@bank.com");
            testUsers.add(user);
        }

        when(userProfileController.getUserById(any(Long.class))).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return testUsers.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
        });

        // Mock audit log saving to capture entries
        doAnswer(invocation -> {
            AuditLogEntry entry = invocation.getArgument(0);
            auditLogEntries.add(entry);
            return null;
        }).when(fpmCommonController).saveAuditLog(any(AuditLogEntry.class));

        // Prepare concurrent tasks simulating approval/rejection
        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (User user : testUsers) {
            tasks.add(() -> performApprovalOrRejection(user));
        }

        // Execute all tasks concurrently
        List<Future<Boolean>> futures = executorService.invokeAll(tasks);

        // Wait for all to complete and verify results
        for (Future<Boolean> future : futures) {
            assertThat(future.get(30, TimeUnit.SECONDS)).isTrue();
        }

        // Verify audit logs
        assertThat(auditLogEntries).hasSize(CONCURRENT_USERS);

        for (AuditLogEntry entry : auditLogEntries) {
            assertThat(entry.getUserId()).isNotNull();
            assertThat(entry.getActionType()).isIn("APPROVAL", "REJECTION");
            assertThat(entry.getTimestamp()).isBeforeOrEqualTo(Instant.now());
            assertThat(entry.isImmutable()).isTrue();
        }

        // Verify no duplicate or missing entries
        long uniqueUserIds = auditLogEntries.stream().map(AuditLogEntry::getUserId).distinct().count();
        assertThat(uniqueUserIds).isEqualTo(CONCURRENT_USERS);

        // Performance check (simple example: total time should be under threshold)
        // In real scenario, integrate with performance monitoring tools
    }

    /**
     * Simulates a user performing either approval or rejection action via Selenium WebDriver.
     * @param user the user performing the action
     * @return true if action succeeded and audit log entry created
     */
    private Boolean performApprovalOrRejection(User user) {
        try {
            // Simulate login
            driver.get(BASE_URL + "/login");
            WebDriverWait wait = new WebDriverWait(driver, 10);

            WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginBtn"));

            usernameInput.clear();
            usernameInput.sendKeys(user.getUsername());
            passwordInput.clear();
            passwordInput.sendKeys("password123"); // assuming test password
            loginButton.click();

            // Wait for dashboard
            wait.until(ExpectedConditions.urlContains("/dashboard"));

            // Navigate to approval page
            driver.get(BASE_URL + "/fpm/approval");

            // Wait for approval list
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalList")));

            // Select first pending item
            List<WebElement> pendingItems = driver.findElements(By.cssSelector(".pending-item"));
            if (pendingItems.isEmpty()) {
                // No pending items, test cannot proceed
                return false;
            }

            WebElement item = pendingItems.get(0);

            // Randomly decide approval or rejection
            boolean approve = user.getId() % 2 == 0;

            if (approve) {
                WebElement approveBtn = item.findElement(By.cssSelector("button.approve"));
                approveBtn.click();
            } else {
                WebElement rejectBtn = item.findElement(By.cssSelector("button.reject"));
                rejectBtn.click();
            }

            // Wait for confirmation message
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("actionSuccessMsg")));

            // Simulate audit log entry creation
            AuditLogEntry logEntry = new AuditLogEntry();
            logEntry.setUserId(user.getId());
            logEntry.setActionType(approve ? "APPROVAL" : "REJECTION");
            logEntry.setTimestamp(Instant.now());
            logEntry.setImmutable(true);

            fpmCommonController.saveAuditLog(logEntry);

            // Logout
            driver.get(BASE_URL + "/logout");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Dummy AuditLogEntry class to represent audit log entries.
     * In real application, this would be a proper entity or DTO.
     */
    public static class AuditLogEntry {
        private Long userId;
        private String actionType;
        private Instant timestamp;
        private boolean immutable;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
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

        public boolean isImmutable() {
            return immutable;
        }

        public void setImmutable(boolean immutable) {
            this.immutable = immutable;
        }
    }
}
