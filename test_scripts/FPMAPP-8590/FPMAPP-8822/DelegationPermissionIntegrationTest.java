/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8822
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:53:04
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.Optional;

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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import org.openqa.selenium.support.ui.ExpectedCondition;

import java.time.Duration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DelegationPermissionIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

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
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock user profiles with roles
        User roleWithDelegation = new User();
        roleWithDelegation.setId(1L);
        roleWithDelegation.setUsername("approver_with_delegation");
        roleWithDelegation.setRole("Manager");
        roleWithDelegation.setDelegationAllowed(true);

        User roleWithoutDelegation = new User();
        roleWithoutDelegation.setId(2L);
        roleWithoutDelegation.setUsername("approver_without_delegation");
        roleWithoutDelegation.setRole("Analyst");
        roleWithoutDelegation.setDelegationAllowed(false);

        doReturn(roleWithDelegation).when(fpmUserProfileController).getUserByUsername("approver_with_delegation");
        doReturn(roleWithoutDelegation).when(fpmUserProfileController).getUserByUsername("approver_without_delegation");

        // Mock approval requests
        // Approval request for role with delegation
        doReturn(true).when(fpmDealsheetController).canDelegate(any(Long.class), any(Long.class));
        // Approval request for role without delegation
        doReturn(false).when(fpmDealsheetController).canDelegate(any(Long.class), any(Long.class));
    }

    @Test
    public void testDelegationAllowedForAuthorizedRole() {
        // Simulate login as user with delegation permission
        driver.get("http://localhost:" + port + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("approver_with_delegation");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Navigate to approval requests page
        wait.until(ExpectedConditions.urlContains("/approvals"));

        // Select an approval request pending for this role
        WebElement approvalRequestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tr[data-approval-id='1001']")));
        approvalRequestRow.click();

        // Click delegate button
        WebElement delegateButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("delegateBtn")));
        delegateButton.click();

        // Select user to delegate to
        WebElement delegateUserSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateUserSelect")));
        delegateUserSelect.click();
        WebElement delegateUserOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("option[value='3']")));// user id 3
        delegateUserOption.click();

        // Confirm delegation
        WebElement confirmDelegateBtn = driver.findElement(By.id("confirmDelegateBtn"));
        confirmDelegateBtn.click();

        // Verify success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateSuccessMsg")));
        assertThat(successMsg.getText()).contains("Delegation successful");

        // Verify delegation recorded via backend call
        verify(fpmDealsheetController, times(1)).delegateApprovalRequest(1001L, 3L, "Manager");

        // Verify audit log recorded (simulate via mock)
        verify(fpmCommonController, times(1)).logDelegationAction(1001L, 1L, 3L, "Manager", LocalDateTime.now());
    }

    @Test
    public void testDelegationDeniedForUnauthorizedRole() {
        // Simulate login as user without delegation permission
        driver.get("http://localhost:" + port + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("approver_without_delegation");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Navigate to approval requests page
        wait.until(ExpectedConditions.urlContains("/approvals"));

        // Select an approval request pending for this role
        WebElement approvalRequestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tr[data-approval-id='2001']")));
        approvalRequestRow.click();

        // Delegate button should be disabled or not present
        boolean delegateButtonPresent = driver.findElements(By.id("delegateBtn")).size() > 0;
        if (delegateButtonPresent) {
            WebElement delegateButton = driver.findElement(By.id("delegateBtn"));
            assertThat(delegateButton.isEnabled()).isFalse();

            // Attempt to click delegate button
            delegateButton.click();

            // Verify error message shown
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateErrorMsg")));
            assertThat(errorMsg.getText()).contains("Delegation not allowed for your role");

            // Verify no delegation backend call
            verify(fpmDealsheetController, times(0)).delegateApprovalRequest(any(Long.class), any(Long.class), any(String.class));
        } else {
            // If button not present, test passes as delegation is blocked
            assertThat(delegateButtonPresent).isFalse();
        }
    }
}
