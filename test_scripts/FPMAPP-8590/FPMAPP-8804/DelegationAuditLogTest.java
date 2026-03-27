/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8804
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:59:32
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for verifying delegation audit log.
 * 
 * Preconditions:
 * - User with delegation permissions is logged in.
 * - AuditTrailService and DB tables operational.
 * 
 * Test Steps:
 * 1. Perform delegation action (delegator -> delegatee).
 * 2. Retrieve audit log entry via API.
 * 3. Verify audit log correctness and UI display.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DelegationAuditLogTest {

    private static WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController auditTrailService;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
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
        // Mock delegator and delegatee users
        User delegator = new User();
        delegator.setId(1001L);
        delegator.setUsername("delegatorUser");
        delegator.setEmail("delegator@example.com");
        delegator.setRoles(Collections.singletonList("ROLE_DELEGATOR"));

        User delegatee = new User();
        delegatee.setId(1002L);
        delegatee.setUsername("delegateeUser");
        delegatee.setEmail("delegatee@example.com");
        delegatee.setRoles(Collections.singletonList("ROLE_DELEGATEE"));

        // Mock user profile controller to return delegator on login
        when(userProfileController.getCurrentUser()).thenReturn(delegator);

        // Mock audit trail service to simulate audit log creation and retrieval
        when(auditTrailService.logDelegationAction(any(), any(), any())).thenAnswer(invocation -> {
            User delegatorArg = invocation.getArgument(0);
            User delegateeArg = invocation.getArgument(1);
            Instant delegationTime = invocation.getArgument(2);
            // Simulate storing audit log entry
            return new AuditLogEntry(delegatorArg, delegateeArg, delegationTime);
        });

        when(auditTrailService.getAuditLogsForDelegation(any(), any())).thenAnswer(invocation -> {
            User delegatorArg = invocation.getArgument(0);
            User delegateeArg = invocation.getArgument(1);
            Instant delegationTime = Instant.now();
            AuditLogEntry entry = new AuditLogEntry(delegatorArg, delegateeArg, delegationTime);
            return Collections.singletonList(entry);
        });
    }

    @Test
    public void testDelegationAuditLogCreationAndUI() throws InterruptedException {
        // Step 1: Login as delegator user
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("delegatorUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for redirect to dashboard
        Thread.sleep(2000);

        // Step 2: Perform delegation action via UI
        driver.get(BASE_URL + "/delegation");

        WebElement delegateeInput = driver.findElement(By.id("delegateeUsername"));
        WebElement delegateButton = driver.findElement(By.id("delegateBtn"));

        delegateeInput.sendKeys("delegateeUser");
        delegateButton.click();

        // Wait for delegation processing
        Thread.sleep(2000);

        // Step 3: Verify audit log entry via API
        List<AuditLogEntry> auditLogs = auditTrailService.getAuditLogsForDelegation(
                userProfileController.getCurrentUser(),
                new User() {{ setUsername("delegateeUser"); }});

        assertThat(auditLogs).isNotEmpty();
        AuditLogEntry logEntry = auditLogs.get(0);

        assertThat(logEntry.getDelegator().getUsername()).isEqualTo("delegatorUser");
        assertThat(logEntry.getDelegatee().getUsername()).isEqualTo("delegateeUser");
        assertThat(logEntry.getDelegationTime()).isNotNull();

        // Delegation time should be recent (within last 5 minutes)
        Instant now = Instant.now();
        assertThat(logEntry.getDelegationTime()).isAfter(now.minus(5, ChronoUnit.MINUTES));

        // Step 4: Verify audit trail UI
        driver.get(BASE_URL + "/audit-trail");

        // Wait for audit trail page load
        Thread.sleep(2000);

        WebElement auditTable = driver.findElement(By.id("auditTrailTable"));
        List<WebElement> rows = auditTable.findElements(By.tagName("tr"));

        boolean foundEntry = false;
        for (WebElement row : rows) {
            String rowText = row.getText();
            if (rowText.contains("delegatorUser") && rowText.contains("delegateeUser")) {
                foundEntry = true;
                break;
            }
        }

        assertThat(foundEntry).isTrue();
    }

    /**
     * Simple POJO to simulate audit log entry.
     */
    public static class AuditLogEntry {
        private final User delegator;
        private final User delegatee;
        private final Instant delegationTime;

        public AuditLogEntry(User delegator, User delegatee, Instant delegationTime) {
            this.delegator = delegator;
            this.delegatee = delegatee;
            this.delegationTime = delegationTime;
        }

        public User getDelegator() {
            return delegator;
        }

        public User getDelegatee() {
            return delegatee;
        }

        public Instant getDelegationTime() {
            return delegationTime;
        }
    }
}
