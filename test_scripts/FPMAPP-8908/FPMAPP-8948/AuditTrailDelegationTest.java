/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8948
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:42:23
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
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
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test for audit trail delegation history and delegate actions.
 * 
 * Preconditions:
 * - A delegation has been successfully created.
 * - The delegate user has performed approval actions.
 * 
 * This test mocks the audit log service responses and verifies UI audit log display.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuditTrailDelegationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @Autowired
    private CurrencyConvertionController currencyConvertionController; // just to show usage if needed

    private final String baseUrl = "http://localhost:";

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
        // Mock user profile for delegate user
        User delegateUser = new User();
        delegateUser.setId(2001L);
        delegateUser.setUsername("delegateUser");
        delegateUser.setFullName("Delegate User");

        when(fpmUserProfileController.getUserByUsername("delegateUser")).thenReturn(delegateUser);

        // Mock audit log entries
        List<AuditLogEntry> auditEntries = Arrays.asList(
            new AuditLogEntry("delegation_creation", "ApproverUser", "DelegateUser", LocalDateTime.now().minusDays(2), "Delegation created with controlled permissions."),
            new AuditLogEntry("approval_action", "DelegateUser", "ProjectX", LocalDateTime.now().minusDays(1), "Approved cost plan #1234 with delegation flag."),
            new AuditLogEntry("approval_action", "DelegateUser", "ProjectY", LocalDateTime.now().minusHours(5), "Modified approval request with delegation flag."),
            new AuditLogEntry("approval_action", "ApproverUser", "ProjectZ", LocalDateTime.now().minusDays(3), "Approved cost plan #5678.")
        );

        when(fpmCommonController.getAuditLogs(any())).thenReturn(auditEntries);
    }

    @Test
    public void testAuditTrailDelegationHistoryAndDelegateActions() {
        try {
            driver.get(baseUrl + port + "/audit-logs");

            // Wait for page load and audit log table presence
            Thread.sleep(2000); // simple wait for demo; in prod use WebDriverWait

            WebElement auditTable = driver.findElement(By.id("auditLogTable"));
            assertThat(auditTable).isNotNull();

            List<WebElement> rows = auditTable.findElements(By.tagName("tr"));
            assertThat(rows.size()).isGreaterThan(1); // header + entries

            boolean delegationCreationFound = false;
            boolean delegateApprovalFound = false;
            boolean originalApproverApprovalFound = false;

            for (WebElement row : rows) {
                List<WebElement> cols = row.findElements(By.tagName("td"));
                if (cols.size() < 4) continue; // skip header or malformed

                String actionType = cols.get(0).getText().toLowerCase();
                String user = cols.get(1).getText();
                String details = cols.get(3).getText();

                if (actionType.contains("delegation_creation") && user.equalsIgnoreCase("ApproverUser")) {
                    delegationCreationFound = true;
                    assertThat(details).contains("delegation");
                }

                if (actionType.contains("approval_action") && user.equalsIgnoreCase("DelegateUser")) {
                    delegateApprovalFound = true;
                    assertThat(details.toLowerCase()).contains("delegation flag");
                }

                if (actionType.contains("approval_action") && user.equalsIgnoreCase("ApproverUser")) {
                    originalApproverApprovalFound = true;
                }
            }

            assertThat(delegationCreationFound).as("Delegation creation entry should be present").isTrue();
            assertThat(delegateApprovalFound).as("Delegate approval actions with delegation flags should be present").isTrue();
            assertThat(originalApproverApprovalFound).as("Original approver approval actions should be present").isTrue();

        } catch (Exception e) {
            e.printStackTrace();
            assertThat(false).as("Exception during Selenium test: " + e.getMessage()).isTrue();
        }
    }

    /**
     * Simple POJO to mock audit log entries returned by FpmCommonController.
     */
    public static class AuditLogEntry {
        private String actionType;
        private String performedBy;
        private String relatedEntity;
        private LocalDateTime timestamp;
        private String details;

        public AuditLogEntry(String actionType, String performedBy, String relatedEntity, LocalDateTime timestamp, String details) {
            this.actionType = actionType;
            this.performedBy = performedBy;
            this.relatedEntity = relatedEntity;
            this.timestamp = timestamp;
            this.details = details;
        }

        public String getActionType() {
            return actionType;
        }

        public String getPerformedBy() {
            return performedBy;
        }

        public String getRelatedEntity() {
            return relatedEntity;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getDetails() {
            return details;
        }
    }
}
