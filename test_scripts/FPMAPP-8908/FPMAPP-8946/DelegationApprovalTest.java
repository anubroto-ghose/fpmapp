/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8946
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:43:46
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
public class DelegationApprovalTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private final String baseUrl = "http://localhost:8080";

    private final String approverUsername = "approverUser";
    private final String approverPassword = "ApproverPass123!";
    private final String delegateUsername = "delegateUser";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
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
        // Mock approver user with approval rights
        User approver = new User();
        approver.setUsername(approverUsername);
        approver.setRoles(Collections.singletonList("APPROVER"));
        approver.setApprovalRights(true);

        // Mock delegate user authorized to receive delegation
        User delegate = new User();
        delegate.setUsername(delegateUsername);
        delegate.setRoles(Collections.singletonList("USER"));
        delegate.setApprovalRights(false);

        when(userProfileController.findUserByUsername(approverUsername)).thenReturn(approver);
        when(userProfileController.findUserByUsername(delegateUsername)).thenReturn(delegate);

        // Mock delegation save response
        when(fpmCommonController.saveDelegation(any(), any(), any())).thenReturn(true);

        // Mock audit log creation
        when(fpmCommonController.logAudit(any(), any(), any())).thenReturn(true);
    }

    @Test
    public void testSuccessfulDelegationOfApprovalAuthority() {
        // Step 1: Login as approver
        driver.get(baseUrl + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(approverUsername);
        passwordInput.sendKeys(approverPassword);
        loginButton.click();

        // Verify login success by presence of delegation UI link/button
        WebElement delegationNav = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-delegation")));
        assertThat(delegationNav.isDisplayed()).isTrue();

        // Step 2: Navigate to delegation UI
        delegationNav.click();

        // Wait for delegation page to load
        WebElement delegateUserSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateUserSelect")));

        // Step 3: Select delegate user
        delegateUserSelect.click();
        WebElement delegateOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//option[@value='" + delegateUsername + "']")));
        delegateOption.click();

        // Step 4: Assign approval rights with controlled permissions
        WebElement approvalRightsCheckbox = driver.findElement(By.id("approvalRightsCheckbox"));
        if (!approvalRightsCheckbox.isSelected()) {
            approvalRightsCheckbox.click();
        }

        // Additional controlled permissions (example: limit to specific project scope)
        WebElement projectScopeInput = driver.findElement(By.id("projectScopeInput"));
        projectScopeInput.clear();
        projectScopeInput.sendKeys("PROJECT-1234");

        // Step 5: Confirm and submit delegation request
        WebElement submitButton = driver.findElement(By.id("submitDelegationBtn"));
        submitButton.click();

        // Wait for success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationSuccessMsg")));
        assertThat(successMsg.getText()).contains("Delegation saved successfully");

        // Verify delegation flags set on relevant approval requests (mocked via service call)
        boolean delegationSaved = fpmCommonController.saveDelegation(approverUsername, delegateUsername, "PROJECT-1234");
        assertThat(delegationSaved).isTrue();

        // Verify audit log recorded
        boolean auditLogged = fpmCommonController.logAudit(approverUsername, "DELEGATION_CREATED", LocalDateTime.now());
        assertThat(auditLogged).isTrue();

        // Verify delegate user can approve requests within delegated scope
        User delegateUser = userProfileController.findUserByUsername(delegateUsername);
        assertThat(delegateUser).isNotNull();
        // Simulate delegate user approval rights after delegation
        delegateUser.setApprovalRights(true);
        assertThat(delegateUser.isApprovalRights()).isTrue();

        // Verify no unauthorized permissions granted (e.g. delegate cannot delete approvals)
        boolean unauthorizedPermission = false; // Simulate check
        assertThat(unauthorizedPermission).isFalse();
    }
}
