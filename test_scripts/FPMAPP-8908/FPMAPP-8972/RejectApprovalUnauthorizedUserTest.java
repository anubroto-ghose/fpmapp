/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8972
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:32:08
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test for rejecting approval attempt by unauthorized 'Employee' role user.
 * 
 * Preconditions:
 * - User accounts exist with roles 'Employee' (unauthorized), 'Manager', and 'Director'.
 * - Approval requests exist that require approval by either a manager or director.
 * 
 * Test Steps:
 * 1. Log in as a user with the 'Employee' role.
 * 2. Attempt to approve a mid-tier approval request.
 * 
 * Expected Results:
 * - The approval attempt is rejected with an authorization error.
 * - The system prevents the 'Employee' role from approving requests.
 * - An appropriate error message indicating lack of authorization is displayed.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RejectApprovalUnauthorizedUserTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:8080"; // Adjust if needed

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmCommonController commonController;

    @BeforeAll
    public void setUp() {
        // Setup ChromeDriver (assumes chromedriver is in system PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock user profile service to return 'Employee' role for test user
        Mockito.when(userProfileController.getUserRole("employeeUser"))
                .thenReturn("Employee");

        // Mock dealsheet approval attempt to reject unauthorized approval
        Mockito.when(dealsheetController.approveRequest(Mockito.anyString(), Mockito.eq("employeeUser")))
                .thenThrow(new SecurityException("User role 'Employee' is not authorized to approve this request."));

        // Mock common controller to provide error messages
        Mockito.when(commonController.getAuthorizationErrorMessage())
                .thenReturn("You are not authorized to approve this request.");
    }

    @AfterAll
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Reject approval attempt by 'Employee' role on mid-tier request")
    public void testRejectApprovalByEmployeeRole() {
        try {
            // Step 1: Navigate to login page
            driver.get(BASE_URL + "/login");

            // Step 2: Log in as 'Employee' user
            WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginButton"));

            usernameInput.clear();
            usernameInput.sendKeys("employeeUser");
            passwordInput.clear();
            passwordInput.sendKeys("password123"); // Use test password
            loginButton.click();

            // Wait for dashboard or approval requests page
            wait.until(ExpectedConditions.urlContains("/dashboard"));

            // Step 3: Navigate to approval requests page
            driver.get(BASE_URL + "/approval-requests");

            // Wait for approval requests table to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalRequestsTable")));

            // Step 4: Find a mid-tier approval request (simulate by selecting first request requiring manager/director approval)
            WebElement firstRequestApproveButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#approvalRequestsTable tbody tr[data-approval-level='mid-tier'] button.approve-btn")));

            // Step 5: Attempt to approve the request
            firstRequestApproveButton.click();

            // Step 6: Wait for error message to appear
            WebElement errorMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalErrorMessage")));

            String actualErrorMessage = errorMessageElement.getText();

            // Step 7: Assert the error message is as expected
            assertTrue(actualErrorMessage.contains("not authorized"),
                    "Expected authorization error message but got: " + actualErrorMessage);

            // Step 8: Verify user remains on approval requests page
            assertTrue(driver.getCurrentUrl().contains("/approval-requests"), "User should remain on approval requests page after failed approval.");

        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError("Test failed due to unexpected exception: " + e.getMessage());
        }
    }
}
