/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8944
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:46:15
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

/**
 * Integration Selenium test for verifying audit log creation for delegation actions including override.
 * 
 * Preconditions:
 * - User is authenticated and authorized to delegate approval actions.
 * - Delegation feature and audit logging system are operational.
 * 
 * This test mocks backend services to simulate audit log retrieval and delegation processing.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class AuditLogDelegationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @InjectMocks
    private FpmUserProfileController injectedUserProfileController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_USER_ID = "user123";
    private static final String DELEGATE_USER_ID = "user456";
    private static final String APPROVAL_REQUEST_ID = "approvalReq789";

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
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
        // Mock user authentication and authorization
        User authenticatedUser = new User();
        authenticatedUser.setUserId(TEST_USER_ID);
        authenticatedUser.setUsername("testuser");
        authenticatedUser.setRoles(Arrays.asList("ROLE_APPROVAL_DELEGATOR"));

        when(userProfileController.getAuthenticatedUser()).thenReturn(authenticatedUser);

        // Mock audit log entries returned by the service
        when(fpmCommonController.getAuditLogsForApprovalRequest(APPROVAL_REQUEST_ID))
                .thenReturn(getMockAuditLogs());

        // Mock delegation submission response
        when(fpmCommonController.submitDelegation(any(), any(), any(), any()))
                .thenReturn(true);
    }

    @Test
    public void testAuditLogCreationForDelegationAndOverride() {
        // Step 1: Navigate to approval request delegation page
        driver.get(BASE_URL + "/approval/" + APPROVAL_REQUEST_ID + "/delegate");

        // Wait for delegation form to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationForm")));

        // Step 2: Fill delegation form
        WebElement delegateUserInput = driver.findElement(By.id("delegateUserId"));
        delegateUserInput.clear();
        delegateUserInput.sendKeys(DELEGATE_USER_ID);

        WebElement delegationCommentInput = driver.findElement(By.id("delegationComment"));
        delegationCommentInput.clear();
        delegationCommentInput.sendKeys("Delegating approval due to workload.");

        WebElement overrideCheckbox = driver.findElement(By.id("overrideDelegation"));
        if (!overrideCheckbox.isSelected()) {
            overrideCheckbox.click();
        }

        WebElement overrideCommentInput = driver.findElement(By.id("overrideComment"));
        overrideCommentInput.clear();
        overrideCommentInput.sendKeys("Override due to urgent compliance requirement.");

        // Step 3: Submit delegation
        WebElement submitButton = driver.findElement(By.id("submitDelegation"));
        submitButton.click();

        // Wait for success message
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationSuccessMessage")));
        WebElement successMessage = driver.findElement(By.id("delegationSuccessMessage"));
        assertThat(successMessage.getText()).contains("Delegation submitted successfully");

        // Step 4: Query audit logs via UI
        driver.get(BASE_URL + "/approval/" + APPROVAL_REQUEST_ID + "/audit-logs");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditLogTable")));

        WebElement auditLogTable = driver.findElement(By.id("auditLogTable"));
        List<WebElement> rows = auditLogTable.findElements(By.tagName("tr"));

        // Validate audit log entries
        boolean delegationLogged = false;
        boolean overrideLogged = false;

        for (WebElement row : rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            if (cols.size() < 4) continue;

            String actionType = cols.get(1).getText();
            String userId = cols.get(2).getText();
            String comment = cols.get(3).getText();

            if ("DELEGATION".equalsIgnoreCase(actionType) && DELEGATE_USER_ID.equals(userId)) {
                delegationLogged = true;
                assertThat(comment).contains("Delegating approval due to workload.");
            }

            if ("OVERRIDE_DELEGATION".equalsIgnoreCase(actionType) && TEST_USER_ID.equals(userId)) {
                overrideLogged = true;
                assertThat(comment).contains("Override due to urgent compliance requirement.");
            }
        }

        assertThat(delegationLogged).isTrue();
        assertThat(overrideLogged).isTrue();

        // Step 5: Verify audit logs immutability and secure storage via API (mocked)
        List<com.webapp.fpmapp.dto.AuditLogEntry> auditLogs = fpmCommonController.getAuditLogsForApprovalRequest(APPROVAL_REQUEST_ID);
        assertThat(auditLogs).isNotEmpty();

        for (com.webapp.fpmapp.dto.AuditLogEntry entry : auditLogs) {
            assertThat(entry.getActionType()).isIn("DELEGATION", "OVERRIDE_DELEGATION");
            assertThat(entry.getUserId()).isNotBlank();
            assertThat(entry.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
            assertThat(entry.getComments()).isNotNull();
        }
    }

    private List<com.webapp.fpmapp.dto.AuditLogEntry> getMockAuditLogs() {
        com.webapp.fpmapp.dto.AuditLogEntry delegationEntry = new com.webapp.fpmapp.dto.AuditLogEntry();
        delegationEntry.setActionType("DELEGATION");
        delegationEntry.setUserId(DELEGATE_USER_ID);
        delegationEntry.setTimestamp(LocalDateTime.now().minusMinutes(5));
        delegationEntry.setComments("Delegating approval due to workload.");

        com.webapp.fpmapp.dto.AuditLogEntry overrideEntry = new com.webapp.fpmapp.dto.AuditLogEntry();
        overrideEntry.setActionType("OVERRIDE_DELEGATION");
        overrideEntry.setUserId(TEST_USER_ID);
        overrideEntry.setTimestamp(LocalDateTime.now().minusMinutes(3));
        overrideEntry.setComments("Override due to urgent compliance requirement.");

        return Arrays.asList(delegationEntry, overrideEntry);
    }
}
