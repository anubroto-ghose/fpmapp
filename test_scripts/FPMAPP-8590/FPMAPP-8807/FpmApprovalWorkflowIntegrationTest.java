/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8807
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:42:22
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

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration test for role-based approval workflow including rejection and delegation.
 * 
 * Preconditions:
 * - User logged in as director or manager with approval and delegation rights.
 * - A request is pending approval.
 * 
 * Test Steps:
 * 1. Reject the request and verify status update.
 * 2. Delegate the request to another authorized approver.
 * 3. Verify status updates "Rejected" and "Delegated" respectively.
 * 4. Confirm delegated approver receives the request.
 * 5. Confirm requester sees real-time status updates.
 * 6. Verify only authorized roles can perform actions.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FpmApprovalWorkflowIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private final String baseUrl = "http://localhost:8080";

    private final String pendingRequestId = "REQ12345";
    private final String delegatedUserId = "user_manager_2";
    private final String loggedInUserId = "user_director_1";

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
        MockitoAnnotations.openMocks(this);

        // Mock user profile controller to return logged in user with director role and delegation rights
        User loggedInUser = new User();
        loggedInUser.setId(loggedInUserId);
        loggedInUser.setRole("DIRECTOR");
        loggedInUser.setDelegationAllowed(true);

        doReturn(loggedInUser).when(fpmUserProfileController).getCurrentUser();

        // Mock pending request retrieval
        doReturn(createPendingRequest()).when(fpmDealsheetController).getRequestById(pendingRequestId);

        // Mock delegation user retrieval
        User delegatedUser = new User();
        delegatedUser.setId(delegatedUserId);
        delegatedUser.setRole("MANAGER");
        delegatedUser.setDelegationAllowed(true);
        doReturn(delegatedUser).when(fpmUserProfileController).getUserById(delegatedUserId);

        // Mock delegation acceptance
        doReturn(true).when(fpmDealsheetController).delegateRequest(pendingRequestId, delegatedUserId, loggedInUserId);

        // Mock rejection acceptance
        doReturn(true).when(fpmDealsheetController).rejectRequest(pendingRequestId, loggedInUserId);

        // Mock retrieval of requests for delegated user
        doReturn(Collections.singletonList(createPendingRequest())).when(fpmDealsheetController).getPendingRequestsForUser(delegatedUserId);
    }

    private com.webapp.fpmapp.dto.Request createPendingRequest() {
        com.webapp.fpmapp.dto.Request request = new com.webapp.fpmapp.dto.Request();
        request.setId(pendingRequestId);
        request.setStatus("Pending");
        request.setRequesterId("user_requester_1");
        request.setApproverId(loggedInUserId);
        request.setFinancialAmount(50000.00);
        return request;
    }

    @Test
    public void testRejectAndDelegateRequestStatusUpdateRealTime() throws InterruptedException {
        // Step 0: Login simulation - navigate to login page and login as director
        driver.get(baseUrl + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("director_user");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 1: Navigate to pending requests page
        driver.get(baseUrl + "/approvals/pending");

        // Wait for request list to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("request-list")));

        // Find the request row by request id
        WebElement requestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[@data-request-id='" + pendingRequestId + "']")));
        assertThat(requestRow).isNotNull();

        // Step 2: Reject the request
        WebElement rejectButton = requestRow.findElement(By.cssSelector("button.reject-btn"));
        rejectButton.click();

        // Confirm rejection modal
        WebElement confirmRejectBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmRejectBtn")));
        confirmRejectBtn.click();

        // Wait for status update to "Rejected"
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//tr[@data-request-id='" + pendingRequestId + "']/td[@class='status']"), "Rejected"));

        WebElement statusCell = driver.findElement(By.xpath("//tr[@data-request-id='" + pendingRequestId + "']/td[@class='status']"));
        assertThat(statusCell.getText()).isEqualToIgnoringCase("Rejected");

        // Step 3: Reset mock to pending for delegation test
        doReturn(createPendingRequest()).when(fpmDealsheetController).getRequestById(pendingRequestId);

        // Refresh page to simulate real-time update
        driver.navigate().refresh();

        // Step 4: Delegate the request to another authorized approver
        requestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[@data-request-id='" + pendingRequestId + "']")));
        WebElement delegateButton = requestRow.findElement(By.cssSelector("button.delegate-btn"));
        delegateButton.click();

        // Delegate modal appears
        WebElement delegateUserSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateUserSelect")));
        delegateUserSelect.click();

        // Select delegated user
        WebElement delegatedUserOption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//option[@value='" + delegatedUserId + "']")));
        delegatedUserOption.click();

        WebElement confirmDelegateBtn = driver.findElement(By.id("confirmDelegateBtn"));
        confirmDelegateBtn.click();

        // Wait for status update to "Delegated"
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//tr[@data-request-id='" + pendingRequestId + "']/td[@class='status']"), "Delegated"));

        statusCell = driver.findElement(By.xpath("//tr[@data-request-id='" + pendingRequestId + "']/td[@class='status']"));
        assertThat(statusCell.getText()).isEqualToIgnoringCase("Delegated");

        // Step 5: Confirm delegated approver receives the request
        // Simulate delegated user login
        driver.get(baseUrl + "/logout");
        driver.get(baseUrl + "/login");

        usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        passwordInput = driver.findElement(By.id("password"));
        loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("manager_user2");
        passwordInput.sendKeys("password123");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));

        driver.get(baseUrl + "/approvals/pending");

        // Check delegated request is visible
        WebElement delegatedRequestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[@data-request-id='" + pendingRequestId + "']")));
        assertThat(delegatedRequestRow).isNotNull();

        // Step 6: Check requester sees updated status in real-time
        // Simulate requester login
        driver.get(baseUrl + "/logout");
        driver.get(baseUrl + "/login");

        usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        passwordInput = driver.findElement(By.id("password"));
        loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("user_requester_1");
        passwordInput.sendKeys("password123");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));

        driver.get(baseUrl + "/requests/status");

        // Wait for request status to be visible and updated
        WebElement requesterRequestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[@data-request-id='" + pendingRequestId + "']")));
        WebElement requesterStatusCell = requesterRequestRow.findElement(By.cssSelector("td.status"));

        // Wait up to 10 seconds for real-time update
        boolean statusUpdated = false;
        for (int i = 0; i < 10; i++) {
            if ("Delegated".equalsIgnoreCase(requesterStatusCell.getText()) || "Rejected".equalsIgnoreCase(requesterStatusCell.getText())) {
                statusUpdated = true;
                break;
            }
            TimeUnit.SECONDS.sleep(1);
            driver.navigate().refresh();
            requesterRequestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[@data-request-id='" + pendingRequestId + "']")));
            requesterStatusCell = requesterRequestRow.findElement(By.cssSelector("td.status"));
        }

        assertThat(statusUpdated).isTrue();

        // Step 7: Verify unauthorized user cannot reject or delegate
        // Simulate unauthorized user login
        driver.get(baseUrl + "/logout");
        driver.get(baseUrl + "/login");

        usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        passwordInput = driver.findElement(By.id("password"));
        loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("regular_user");
        passwordInput.sendKeys("password123");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));

        driver.get(baseUrl + "/approvals/pending");

        // The reject and delegate buttons should not be visible for unauthorized user
        List<WebElement> rejectButtons = driver.findElements(By.cssSelector("button.reject-btn"));
        List<WebElement> delegateButtons = driver.findElements(By.cssSelector("button.delegate-btn"));

        assertThat(rejectButtons).isEmpty();
        assertThat(delegateButtons).isEmpty();
    }
}
