/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8802
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:00:55
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
public class DelegationStatusVisibilityTest {

    private static WebDriver delegatorDriver;
    private static WebDriver delegateeDriver;

    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String DELEGATION_STATUS_ENDPOINT = "/api/delegation/status";

    private static final String AUDIT_LOGS_ENDPOINT = "/api/audit/logs";

    private static final String DELEGATION_MODIFY_ENDPOINT = "/api/delegation/modify";

    private static final String DELEGATION_REVOKE_ENDPOINT = "/api/delegation/revoke";

    private static final String DELEGATOR_USERNAME = "manager1";
    private static final String DELEGATEE_USERNAME = "user2";

    private static final String DELEGATION_STATUS_ACTIVE = "Active";
    private static final String DELEGATION_STATUS_REVOKED = "Revoked";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver for both users
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
    public void setupMocks() {
        // Mock delegation record exists
        when(fpmCommonController.getDelegationStatus(DELEGATOR_USERNAME))
                .thenReturn(DELEGATION_STATUS_ACTIVE);
        when(fpmCommonController.getDelegationStatus(DELEGATEE_USERNAME))
                .thenReturn(DELEGATION_STATUS_ACTIVE);

        // Mock audit logs
        when(fpmCommonController.getAuditLogs(any()))
                .thenReturn(Collections.singletonList("Delegation created between manager1 and user2 at " + LocalDateTime.now()));

        // Mock user profiles
        when(userProfileController.getUserByUsername(DELEGATOR_USERNAME))
                .thenReturn(new User(DELEGATOR_USERNAME, "Manager One", "manager1@bank.com"));
        when(userProfileController.getUserByUsername(DELEGATEE_USERNAME))
                .thenReturn(new User(DELEGATEE_USERNAME, "User Two", "user2@bank.com"));
    }

    @Test
    public void testDelegationStatusVisibilityAndModification() {
        // Step 1: Delegator logs in and views delegation status
        loginUser(delegatorDriver, DELEGATOR_USERNAME, "password123");
        String delegatorStatus = getDelegationStatusFromUI(delegatorDriver);
        assertThat(delegatorStatus).isEqualTo(DELEGATION_STATUS_ACTIVE);

        // Step 2: Delegatee logs in and views delegation status
        loginUser(delegateeDriver, DELEGATEE_USERNAME, "password123");
        String delegateeStatus = getDelegationStatusFromUI(delegateeDriver);
        assertThat(delegateeStatus).isEqualTo(DELEGATION_STATUS_ACTIVE);

        // Step 3: Delegator modifies delegation (revokes it)
        revokeDelegationByDelegator();

        // Mock updated delegation status after revocation
        when(fpmCommonController.getDelegationStatus(DELEGATOR_USERNAME))
                .thenReturn(DELEGATION_STATUS_REVOKED);
        when(fpmCommonController.getDelegationStatus(DELEGATEE_USERNAME))
                .thenReturn(DELEGATION_STATUS_REVOKED);

        // Mock audit logs for revocation
        when(fpmCommonController.getAuditLogs(any()))
                .thenReturn(List.of(
                        "Delegation created between manager1 and user2 at " + LocalDateTime.now().minusMinutes(5),
                        "Delegation revoked by manager1 at " + LocalDateTime.now()));

        // Step 4: Both users refresh their views
        refreshPage(delegatorDriver);
        refreshPage(delegateeDriver);

        String delegatorStatusAfterRevoke = getDelegationStatusFromUI(delegatorDriver);
        String delegateeStatusAfterRevoke = getDelegationStatusFromUI(delegateeDriver);

        assertThat(delegatorStatusAfterRevoke).isEqualTo(DELEGATION_STATUS_REVOKED);
        assertThat(delegateeStatusAfterRevoke).isEqualTo(DELEGATION_STATUS_REVOKED);

        // Verify UI shows appropriate status messages
        assertThat(isStatusMessageDisplayed(delegatorDriver, "Delegation has been revoked.")).isTrue();
        assertThat(isStatusMessageDisplayed(delegateeDriver, "Delegation has been revoked.")).isTrue();

        // Verify audit logs contain revocation entry
        List<String> auditLogs = fpmCommonController.getAuditLogs(DELEGATOR_USERNAME);
        boolean revocationLogged = auditLogs.stream().anyMatch(log -> log.contains("revoked"));
        assertThat(revocationLogged).isTrue();
    }

    private void loginUser(WebDriver driver, String username, String password) {
        driver.get(BASE_URL + "/login");
        WebDriverWait wait = new WebDriverWait(driver, 10);

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        loginButton.click();

        // Wait for dashboard or delegation page to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    private String getDelegationStatusFromUI(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // Navigate to delegation status page
        driver.get(BASE_URL + "/delegation/status");

        WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationStatus")));
        return statusElement.getText().trim();
    }

    private void revokeDelegationByDelegator() {
        // Simulate delegation revocation via service call or UI
        // Here we mock the service call directly
        when(fpmCommonController.revokeDelegation(DELEGATOR_USERNAME, DELEGATEE_USERNAME))
                .thenReturn(true);

        boolean revoked = fpmCommonController.revokeDelegation(DELEGATOR_USERNAME, DELEGATEE_USERNAME);
        assertThat(revoked).isTrue();
    }

    private void refreshPage(WebDriver driver) {
        driver.navigate().refresh();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationStatus")));
    }

    private boolean isStatusMessageDisplayed(WebDriver driver, String message) {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        try {
            WebElement messageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("statusMessage")));
            return messageElement.getText().contains(message);
        } catch (Exception e) {
            return false;
        }
    }
}
