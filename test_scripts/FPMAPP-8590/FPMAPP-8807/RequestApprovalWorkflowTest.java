/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8807
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:57:19
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
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration Selenium test for approval workflow: rejection and delegation.
 * 
 * Preconditions:
 * - User logged in as director/manager with approval and delegation rights.
 * - A request is pending approval.
 * 
 * Tests rejection and delegation flows with real-time status updates.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RequestApprovalWorkflowTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmTravelController travelController;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private final String baseUrl = "http://localhost:8080";

    private final String testRequestId = "REQ12345";
    private final String delegatedApproverUsername = "approver2";

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
        // Mock user profile with director/manager role and approval rights
        User loggedInUser = new User();
        loggedInUser.setUsername("directorUser");
        loggedInUser.setRoles(List.of("ROLE_DIRECTOR", "ROLE_APPROVER", "ROLE_DELEGATOR"));

        when(userProfileController.getCurrentUser()).thenReturn(loggedInUser);

        // Mock pending request
        when(dealsheetController.getRequestStatus(testRequestId)).thenReturn("Pending");

        // Mock delegation target user
        User delegatedApprover = new User();
        delegatedApprover.setUsername(delegatedApproverUsername);
        delegatedApprover.setRoles(List.of("ROLE_APPROVER"));
        when(userProfileController.findUserByUsername(delegatedApproverUsername)).thenReturn(delegatedApprover);

        // Mock service calls for rejection and delegation
        when(dealsheetController.rejectRequest(testRequestId, loggedInUser.getUsername())).thenReturn(true);
        when(dealsheetController.delegateRequest(testRequestId, delegatedApproverUsername, loggedInUser.getUsername())).thenReturn(true);

        // Mock retrieval of updated status after actions
        when(dealsheetController.getRequestStatus(testRequestId))
            .thenReturn("Pending") // initial
            .thenReturn("Rejected") // after rejection
            .thenReturn("Delegated"); // after delegation

        // Mock retrieval of requests for delegated approver
        when(dealsheetController.getRequestsForUser(delegatedApproverUsername))
            .thenReturn(Collections.singletonList(testRequestId));
    }

    @Test
    public void testRejectAndDelegateRequestStatusUpdates() {
        // Step 0: Login as director/manager
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("directorUser");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("loginButton")).click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 1: Navigate to pending requests
        driver.get(baseUrl + "/requests/pending");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("requestTable")));

        // Locate the request row by request id
        WebElement requestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[td[text()='" + testRequestId + "']]")
        ));

        // Verify initial status is Pending
        WebElement statusCell = requestRow.findElement(By.className("status"));
        assertEquals("Pending", statusCell.getText(), "Initial request status should be Pending");

        // Step 2: Reject the request
        WebElement rejectButton = requestRow.findElement(By.className("btn-reject"));
        rejectButton.click();

        // Confirm rejection modal
        WebElement confirmRejectBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmReject")));
        confirmRejectBtn.click();

        // Wait for status update
        wait.until(ExpectedConditions.textToBePresentInElement(statusCell, "Rejected"));

        // Step 3: Verify status updated to Rejected
        assertEquals("Rejected", statusCell.getText(), "Request status should update to Rejected immediately after rejection");

        // Step 4: Delegate the request
        // For delegation, we need to reset the request status to Pending to simulate a new request
        // (In real scenario, delegation happens on a pending request, so we simulate by navigating to another request or resetting mocks)
        // Here, we simulate by navigating to delegation page

        // Navigate to delegation page for the request
        driver.get(baseUrl + "/requests/" + testRequestId + "/delegate");

        // Wait for delegation form
        WebElement delegateUserInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateTo")));
        delegateUserInput.clear();
        delegateUserInput.sendKeys(delegatedApproverUsername);

        WebElement delegateSubmitBtn = driver.findElement(By.id("delegateSubmit"));
        delegateSubmitBtn.click();

        // Wait for delegation success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delegateSuccessMsg")));
        assertTrue(successMsg.getText().contains("Delegation successful"), "Delegation success message should be displayed");

        // Step 5: Verify status updated to Delegated
        driver.get(baseUrl + "/requests/pending");
        WebElement delegatedRequestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[td[text()='" + testRequestId + "']]")
        ));
        WebElement delegatedStatusCell = delegatedRequestRow.findElement(By.className("status"));

        wait.until(ExpectedConditions.textToBePresentInElement(delegatedStatusCell, "Delegated"));
        assertEquals("Delegated", delegatedStatusCell.getText(), "Request status should update to Delegated immediately after delegation");

        // Step 6: Confirm delegated approver receives the request
        // Simulate delegated approver login
        driver.get(baseUrl + "/logout");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(delegatedApproverUsername);
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("loginButton")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to delegated requests
        driver.get(baseUrl + "/requests/delegated");
        WebElement delegatedApproverRequestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[td[text()='" + testRequestId + "']]")
        ));
        assertNotNull(delegatedApproverRequestRow, "Delegated approver should see the delegated request");

        // Step 7: Check requester sees updated status in real-time
        // Simulate requester login
        driver.get(baseUrl + "/logout");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("requesterUser");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("loginButton")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to my requests
        driver.get(baseUrl + "/requests/my");
        WebElement requesterRequestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[td[text()='" + testRequestId + "']]")
        ));
        WebElement requesterStatusCell = requesterRequestRow.findElement(By.className("status"));

        // Wait and assert status is Delegated (latest status)
        wait.until(ExpectedConditions.textToBePresentInElement(requesterStatusCell, "Delegated"));
        assertEquals("Delegated", requesterStatusCell.getText(), "Requester should see the current status in real-time");

        // Step 8: Verify only authorized roles can reject or delegate
        // Simulate unauthorized user login
        driver.get(baseUrl + "/logout");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("unauthorizedUser");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("loginButton")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to pending requests
        driver.get(baseUrl + "/requests/pending");
        WebElement unauthorizedRequestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[td[text()='" + testRequestId + "']]")
        ));

        // Check reject and delegate buttons are not visible
        List<WebElement> rejectButtons = unauthorizedRequestRow.findElements(By.className("btn-reject"));
        List<WebElement> delegateButtons = unauthorizedRequestRow.findElements(By.className("btn-delegate"));

        assertTrue(rejectButtons.isEmpty(), "Reject button should not be visible to unauthorized users");
        assertTrue(delegateButtons.isEmpty(), "Delegate button should not be visible to unauthorized users");
    }
}
