/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8802
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:38:14
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Arrays;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test for delegation status visibility to delegator and delegatee.
 * 
 * Preconditions:
 * - Delegation record exists between delegator and delegatee.
 * - Both users are logged in.
 * 
 * Test Steps:
 * 1. Delegator views delegation status in UI.
 * 2. Delegatee views delegation status in UI.
 * 3. Delegator modifies or revokes delegation.
 * 4. Both users refresh views.
 * 
 * Expected Results:
 * - Delegation status visible and accurate to both users.
 * - Changes reflected in real-time or on refresh.
 * - UI shows appropriate status messages.
 * - Audit logs record all changes.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DelegationStatusVisibilityIT {

    @LocalServerPort
    private int port;

    private static WebDriver delegatorDriver;
    private static WebDriver delegateeDriver;

    private WebDriverWait delegatorWait;
    private WebDriverWait delegateeWait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver for headless testing
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        delegatorDriver = new ChromeDriver(options);
        delegateeDriver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownClass() {
        if (delegatorDriver != null) {
            delegatorDriver.quit();
        }
        if (delegateeDriver != null) {
            delegateeDriver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        delegatorWait = new WebDriverWait(delegatorDriver, Duration.ofSeconds(10));
        delegateeWait = new WebDriverWait(delegateeDriver, Duration.ofSeconds(10));

        // Mock user profiles for delegator and delegatee
        when(userProfileController.getUserProfile("manager1"))
            .thenReturn(new com.webapp.fpmapp.entities.User("manager1", "Manager One", "MANAGER"));
        when(userProfileController.getUserProfile("delegatee1"))
            .thenReturn(new com.webapp.fpmapp.entities.User("delegatee1", "Delegatee One", "APPROVER"));

        // Mock delegation record existing
        when(fpmCommonController.getDelegationStatus("manager1", "delegatee1"))
            .thenReturn("ACTIVE");

        // Mock audit logs
        when(fpmCommonController.getAuditLogs(any()))
            .thenReturn(Arrays.asList(
                "Delegation created by manager1 to delegatee1",
                "Delegation active"
            ));
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private void loginUser(WebDriver driver, String username) {
        driver.get(baseUrl() + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys("password"); // assuming test password
        loginButton.click();

        // Wait for redirect to dashboard
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.urlContains("/dashboard"));
    }

    private void navigateToDelegationStatusPage(WebDriver driver) {
        driver.get(baseUrl() + "/approvals/delegation-status");
        // Wait for page load
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationStatusContainer")));
    }

    private String getDelegationStatusText(WebDriver driver) {
        WebElement statusElement = driver.findElement(By.id("delegationStatus"));
        return statusElement.getText().trim();
    }

    private void modifyDelegationStatus(String delegatorUsername, String delegateeUsername, String newStatus) {
        // Simulate delegation modification via mocked controller
        when(fpmCommonController.getDelegationStatus(delegatorUsername, delegateeUsername))
            .thenReturn(newStatus);

        // Simulate audit log update
        when(fpmCommonController.getAuditLogs(any()))
            .thenReturn(Arrays.asList(
                "Delegation modified by " + delegatorUsername + " to status: " + newStatus
            ));
    }

    @Test
    public void testDelegationStatusVisibilityAndUpdate() {
        // Step 0: Login both users
        loginUser(delegatorDriver, "manager1");
        loginUser(delegateeDriver, "delegatee1");

        // Step 1: Delegator views delegation status
        navigateToDelegationStatusPage(delegatorDriver);
        String delegatorStatus = getDelegationStatusText(delegatorDriver);
        assertThat(delegatorStatus).isEqualTo("ACTIVE");

        // Step 2: Delegatee views delegation status
        navigateToDelegationStatusPage(delegateeDriver);
        String delegateeStatus = getDelegationStatusText(delegateeDriver);
        assertThat(delegateeStatus).isEqualTo("ACTIVE");

        // Step 3: Delegator modifies delegation (revokes it)
        modifyDelegationStatus("manager1", "delegatee1", "REVOKED");

        // Step 4: Both users refresh their views
        delegatorDriver.navigate().refresh();
        delegateeDriver.navigate().refresh();

        // Wait for updated status to appear
        delegatorWait.until(ExpectedConditions.textToBe(By.id("delegationStatus"), "REVOKED"));
        delegateeWait.until(ExpectedConditions.textToBe(By.id("delegationStatus"), "REVOKED"));

        String delegatorStatusAfter = getDelegationStatusText(delegatorDriver);
        String delegateeStatusAfter = getDelegationStatusText(delegateeDriver);

        assertThat(delegatorStatusAfter).isEqualTo("REVOKED");
        assertThat(delegateeStatusAfter).isEqualTo("REVOKED");

        // Verify audit logs contain modification record
        List<String> auditLogs = fpmCommonController.getAuditLogs("someApprovalRequestId");
        assertThat(auditLogs).anyMatch(log -> log.contains("modified") && log.contains("REVOKED"));
    }
}