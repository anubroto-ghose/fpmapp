/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8614
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 08:04:26
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test for audit trail delegation feature.
 * 
 * Preconditions:
 * - Delegation feature enabled and configured.
 * - Audit logging framework integrated.
 * 
 * This test:
 * 1. Delegates approval rights from one user to another.
 * 2. Performs approval action by delegated user.
 * 3. Verifies audit log entries for delegation info and data integrity.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuditTrailDelegationTest {

    private static WebDriver driver;

    @Autowired
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController userProfileController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String DELEGATOR_USERNAME = "john.manager";
    private static final String DELEGATEE_USERNAME = "jane.delegate";

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
        // Mock user profile service to return delegator and delegatee users
        when(userProfileController.getUserByUsername(DELEGATOR_USERNAME))
            .thenReturn(new com.webapp.fpmapp.entities.User(DELEGATOR_USERNAME, "John Manager", "Manager"));
        when(userProfileController.getUserByUsername(DELEGATEE_USERNAME))
            .thenReturn(new com.webapp.fpmapp.entities.User(DELEGATEE_USERNAME, "Jane Delegate", "Staff"));

        // Mock delegation feature enabled
        when(fpmCommonController.isDelegationEnabled()).thenReturn(true);

        // Mock audit log retrieval with empty initial logs
        when(fpmCommonController.getAuditLogs(any())).thenReturn(List.of());
    }

    @Test
    public void testAuditTrailCapturesDelegationInfoAndMaintainsDataIntegrity() throws InterruptedException {
        // Step 1: Delegate approval rights from delegator to delegatee
        boolean delegationResult = dealsheetController.delegateApprovalRights(DELEGATOR_USERNAME, DELEGATEE_USERNAME);
        assertThat(delegationResult).as("Delegation should succeed").isTrue();

        // Step 2: Perform approval action by the delegated user
        // Simulate login as delegatee and approve a dealsheet
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(DELEGATEE_USERNAME);
        passwordInput.sendKeys("delegateePassword123"); // assuming test password
        loginButton.click();

        // Wait for login to complete and redirect
        Thread.sleep(1500);

        // Navigate to dealsheet approval page
        driver.get(BASE_URL + "/dealsheet/approval/12345");

        WebElement approveButton = driver.findElement(By.id("approveBtn"));
        approveButton.click();

        // Wait for approval processing
        Thread.sleep(1500);

        // Step 3: Verify audit log entry includes delegation_info linking delegator and delegatee
        List<Map<String, Object>> auditLogs = fpmCommonController.getAuditLogs(Map.of("dealsheetId", "12345"));
        assertThat(auditLogs).isNotEmpty();

        Map<String, Object> latestLog = auditLogs.get(auditLogs.size() - 1);

        assertThat(latestLog).containsKey("action");
        assertThat(latestLog.get("action")).isEqualTo("APPROVAL");

        assertThat(latestLog).containsKey("performedBy");
        assertThat(latestLog.get("performedBy")).isEqualTo(DELEGATEE_USERNAME);

        assertThat(latestLog).containsKey("delegationInfo");
        Map<String, String> delegationInfo = (Map<String, String>) latestLog.get("delegationInfo");
        assertThat(delegationInfo).isNotNull();
        assertThat(delegationInfo.get("delegator")).isEqualTo(DELEGATOR_USERNAME);
        assertThat(delegationInfo.get("delegatee")).isEqualTo(DELEGATEE_USERNAME);

        // Step 4: Check audit logs across services for consistency
        // Mock cross-service audit log retrieval
        List<Map<String, Object>> dealsheetAuditLogs = fpmCommonController.getAuditLogs(Map.of("service", "FpmDealsheetController", "dealsheetId", "12345"));
        List<Map<String, Object>> travelAuditLogs = fpmCommonController.getAuditLogs(Map.of("service", "FpmTravelController", "dealsheetId", "12345"));

        assertThat(dealsheetAuditLogs).isNotEmpty();
        assertThat(travelAuditLogs).isNotEmpty();

        // Verify delegation info consistency
        dealsheetAuditLogs.forEach(log -> {
            if ("APPROVAL".equals(log.get("action"))) {
                Map<String, String> dInfo = (Map<String, String>) log.get("delegationInfo");
                assertThat(dInfo).isNotNull();
                assertThat(dInfo.get("delegator")).isEqualTo(DELEGATOR_USERNAME);
                assertThat(dInfo.get("delegatee")).isEqualTo(DELEGATEE_USERNAME);
            }
        });

        travelAuditLogs.forEach(log -> {
            if ("APPROVAL".equals(log.get("action"))) {
                Map<String, String> tInfo = (Map<String, String>) log.get("delegationInfo");
                assertThat(tInfo).isNotNull();
                assertThat(tInfo.get("delegator")).isEqualTo(DELEGATOR_USERNAME);
                assertThat(tInfo.get("delegatee")).isEqualTo(DELEGATEE_USERNAME);
            }
        });

        // Verify timestamps are accurate and consistent
        Instant approvalTimestamp = Instant.parse((String) latestLog.get("timestamp"));
        assertThat(approvalTimestamp).isBefore(Instant.now().plusSeconds(5));
        assertThat(approvalTimestamp).isAfter(Instant.now().minusSeconds(60));

        // Verify audit data integrity - no discrepancies
        // For simplicity, check that all logs have non-null IDs and consistent dealsheetId
        auditLogs.forEach(log -> {
            assertThat(log.get("id")).isNotNull();
            assertThat(log.get("dealsheetId")).isEqualTo("12345");
        });
    }
}
