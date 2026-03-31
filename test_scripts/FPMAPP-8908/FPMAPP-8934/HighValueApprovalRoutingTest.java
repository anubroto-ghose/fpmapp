/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8934
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:55:38
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
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

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for approval routing of high-value financial requests.
 * 
 * Preconditions:
 * - Role mappings configured with thresholds.
 * - Director user exists and active.
 * - Financial request above high-value threshold.
 * 
 * This test mocks backend services and verifies UI workflow.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class HighValueApprovalRoutingTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController commonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController forecastController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final double HIGH_VALUE_THRESHOLD = 100000.00;

    private static final String DIRECTOR_ROLE = "director";

    private static final String TEST_DIRECTOR_USERNAME = "directorUser";

    private static final String TEST_REQUEST_ID = "REQ-12345";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
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
        // Mock user profile service to return a director user
        User directorUser = new User();
        directorUser.setUsername(TEST_DIRECTOR_USERNAME);
        directorUser.setRole(DIRECTOR_ROLE);
        directorUser.setActive(true);

        when(userProfileController.getUsersByRole(DIRECTOR_ROLE))
            .thenReturn(Collections.singletonList(directorUser));

        // Mock role threshold configuration
        when(commonController.getHighValueThreshold()).thenReturn(HIGH_VALUE_THRESHOLD);

        // Mock dealsheet controller to accept request submission
        when(dealsheetController.submitFinancialRequest(any()))
            .thenAnswer(invocation -> {
                // Return a mock request ID
                return TEST_REQUEST_ID;
            });

        // Mock dealsheet controller to return request routed to director
        when(dealsheetController.getApprovalRoute(TEST_REQUEST_ID))
            .thenReturn(Collections.singletonList(directorUser));

        // Mock approval update
        when(dealsheetController.approveRequest(TEST_REQUEST_ID, TEST_DIRECTOR_USERNAME))
            .thenReturn(true);

        // Mock request status after approval
        when(dealsheetController.getRequestStatus(TEST_REQUEST_ID))
            .thenReturn("APPROVED");
    }

    @Test
    public void testHighValueRequestApprovalRouting() {
        try {
            // Step 1: Navigate to login page and login as a financial requester
            driver.get(BASE_URL + "/login");

            WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginBtn"));

            usernameInput.sendKeys("financialUser");
            passwordInput.sendKeys("password123");
            loginButton.click();

            // Wait for dashboard
            wait.until(ExpectedConditions.urlContains("/dashboard"));

            // Step 2: Submit a high-value financial approval request
            driver.get(BASE_URL + "/financial-requests/new");

            WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount")));
            WebElement descriptionInput = driver.findElement(By.id("description"));
            WebElement submitButton = driver.findElement(By.id("submitRequestBtn"));

            double highValueAmount = HIGH_VALUE_THRESHOLD + 50000.00;
            amountInput.sendKeys(String.valueOf(highValueAmount));
            descriptionInput.sendKeys("Capital expenditure for new project infrastructure");
            submitButton.click();

            // Wait for confirmation page
            WebElement confirmationMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmationMessage")));
            assertTrue(confirmationMsg.getText().contains("Request submitted successfully"), "Request submission failed");

            // Step 3: Verify the request is routed to a user with the director role
            driver.get(BASE_URL + "/financial-requests/" + TEST_REQUEST_ID + "/approval-route");

            List<WebElement> approvers = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".approver-role")));
            boolean directorFound = approvers.stream().anyMatch(e -> e.getText().equalsIgnoreCase(DIRECTOR_ROLE));
            assertTrue(directorFound, "Request was not routed to a director");

            // Step 4: Login as director and approve the request
            driver.get(BASE_URL + "/logout");
            driver.get(BASE_URL + "/login");

            WebElement dirUsernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement dirPasswordInput = driver.findElement(By.id("password"));
            WebElement dirLoginButton = driver.findElement(By.id("loginBtn"));

            dirUsernameInput.sendKeys(TEST_DIRECTOR_USERNAME);
            dirPasswordInput.sendKeys("directorPass");
            dirLoginButton.click();

            wait.until(ExpectedConditions.urlContains("/dashboard"));

            driver.get(BASE_URL + "/financial-requests/" + TEST_REQUEST_ID);

            WebElement approveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("approveBtn")));
            approveBtn.click();

            // Wait for approval confirmation
            WebElement approvalStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatus")));
            assertEquals("APPROVED", approvalStatus.getText(), "Approval status not updated correctly");

            // Step 5: Verify no bypass of role hierarchy (only director can approve)
            // Attempt approval as a non-director user
            driver.get(BASE_URL + "/logout");
            driver.get(BASE_URL + "/login");

            WebElement userUsernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement userPasswordInput = driver.findElement(By.id("password"));
            WebElement userLoginButton = driver.findElement(By.id("loginBtn"));

            userUsernameInput.sendKeys("financialUser");
            userPasswordInput.sendKeys("password123");
            userLoginButton.click();

            wait.until(ExpectedConditions.urlContains("/dashboard"));

            driver.get(BASE_URL + "/financial-requests/" + TEST_REQUEST_ID);

            List<WebElement> approveButtons = driver.findElements(By.id("approveBtn"));
            assertTrue(approveButtons.isEmpty() || !approveButtons.get(0).isDisplayed(), "Non-director user should not see approve button");

        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }
}
