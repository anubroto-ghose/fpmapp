/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8931
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:58:52
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

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
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ApprovalDelegationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
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
        // Mock logged in approver user
        User approver = new User();
        approver.setId(1001L);
        approver.setUsername("approverUser");
        approver.setRoles(Collections.singletonList("ROLE_APPROVER"));

        // Mock delegate user
        User delegateUser = new User();
        delegateUser.setId(2002L);
        delegateUser.setUsername("delegateUser");
        delegateUser.setRoles(Collections.singletonList("ROLE_USER"));

        when(userProfileController.getCurrentUser()).thenReturn(approver);
        when(userProfileController.findUserByUsername("delegateUser")).thenReturn(delegateUser);

        // Mock permission assignment success
        when(fpmCommonController.assignApprovalPermissions(any(Long.class), any(List.class))).thenReturn(true);

        // Mock logging delegation action
        when(fpmCommonController.logDelegationAction(any(Long.class), any(Long.class), any(String.class), any(LocalDateTime.class))).thenReturn(true);
    }

    @Test
    public void testDelegateApprovalRightsWithPermissions() {
        try {
            // Step 1: Navigate to login page and login as approver
            driver.get("http://localhost:8080/login");

            WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginBtn"));

            usernameInput.sendKeys("approverUser");
            passwordInput.sendKeys("securePassword123");
            loginButton.click();

            // Wait for dashboard or home page
            wait.until(ExpectedConditions.urlContains("/dashboard"));

            // Step 2: Navigate to approval delegation interface
            WebElement menuApproval = wait.until(ExpectedConditions.elementToBeClickable(By.id("menu_approval")));
            menuApproval.click();

            WebElement delegationTab = wait.until(ExpectedConditions.elementToBeClickable(By.id("tab_delegation")));
            delegationTab.click();

            // Step 3: Select delegate user
            WebElement delegateUserInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateUserInput")));
            delegateUserInput.sendKeys("delegateUser");

            WebElement searchButton = driver.findElement(By.id("searchDelegateBtn"));
            searchButton.click();

            // Wait for search results
            WebElement delegateUserResult = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//table[@id='delegateUserTable']//td[text()='delegateUser']")));
            delegateUserResult.click();

            // Step 4: Assign specific approval permissions
            WebElement permissionCheckbox1 = wait.until(ExpectedConditions.elementToBeClickable(By.id("perm_approveInvoices")));
            WebElement permissionCheckbox2 = driver.findElement(By.id("perm_approveBudgets"));

            if (!permissionCheckbox1.isSelected()) {
                permissionCheckbox1.click();
            }
            if (!permissionCheckbox2.isSelected()) {
                permissionCheckbox2.click();
            }

            // Step 5: Confirm and save delegation
            WebElement saveButton = driver.findElement(By.id("saveDelegationBtn"));
            saveButton.click();

            // Wait for success message
            WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegationSuccessMsg")));
            assertTrue(successMsg.getText().contains("Delegation saved successfully"), "Delegation success message not shown");

            // Verify delegate user has assigned permissions (mocked service call)
            User delegateUser = userProfileController.findUserByUsername("delegateUser");
            assertNotNull(delegateUser, "Delegate user should exist");

            // Simulate fetching assigned permissions from service
            List<String> assignedPermissions = List.of("APPROVE_INVOICES", "APPROVE_BUDGETS");
            when(fpmCommonController.getUserApprovalPermissions(delegateUser.getId())).thenReturn(assignedPermissions);

            List<String> permissions = fpmCommonController.getUserApprovalPermissions(delegateUser.getId());
            assertTrue(permissions.contains("APPROVE_INVOICES"), "Delegate should have APPROVE_INVOICES permission");
            assertTrue(permissions.contains("APPROVE_BUDGETS"), "Delegate should have APPROVE_BUDGETS permission");

            // Verify delegation action logged
            when(fpmCommonController.getDelegationLogsForUser(delegateUser.getId())).thenReturn(
                    List.of(new com.webapp.fpmapp.entities.DelegationLog(
                            1L, 1001L, 2002L, "APPROVAL_DELEGATION", LocalDateTime.now())));

            List<com.webapp.fpmapp.entities.DelegationLog> logs = fpmCommonController.getDelegationLogsForUser(delegateUser.getId());
            assertFalse(logs.isEmpty(), "Delegation logs should not be empty");
            com.webapp.fpmapp.entities.DelegationLog log = logs.get(0);
            assertEquals(1001L, log.getDelegatorUserId(), "Delegator user ID should match approver");
            assertEquals(2002L, log.getDelegateUserId(), "Delegate user ID should match");
            assertEquals("APPROVAL_DELEGATION", log.getActionType(), "Action type should be APPROVAL_DELEGATION");

            // Step 6: Verify delegate can perform approvals according to assigned permissions
            // Simulate delegate login
            driver.get("http://localhost:8080/logout");
            driver.get("http://localhost:8080/login");

            WebElement usernameInputDel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordInputDel = driver.findElement(By.id("password"));
            WebElement loginButtonDel = driver.findElement(By.id("loginBtn"));

            usernameInputDel.sendKeys("delegateUser");
            passwordInputDel.sendKeys("delegatePassword123");
            loginButtonDel.click();

            wait.until(ExpectedConditions.urlContains("/dashboard"));

            // Navigate to approval page
            WebElement menuApprovalDel = wait.until(ExpectedConditions.elementToBeClickable(By.id("menu_approval")));
            menuApprovalDel.click();

            // Check if delegate can see approval actions
            WebElement approveInvoiceBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approveInvoiceBtn")));
            WebElement approveBudgetBtn = driver.findElement(By.id("approveBudgetBtn"));

            assertTrue(approveInvoiceBtn.isDisplayed() && approveInvoiceBtn.isEnabled(), "Delegate should be able to approve invoices");
            assertTrue(approveBudgetBtn.isDisplayed() && approveBudgetBtn.isEnabled(), "Delegate should be able to approve budgets");

        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }
}
