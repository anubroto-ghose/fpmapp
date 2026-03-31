/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8970
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:33:53
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RejectApprovalRequestTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "http://localhost:8080";

    // Mock data for approval request
    private static final long APPROVAL_REQUEST_ID = 1001L;

    private static final String APPROVAL_STATUS_PENDING = "Pending";
    private static final String APPROVAL_STATUS_REJECTED = "Rejected";

    private static final String APPROVER_ROLE = "FinanceManager";
    private static final String NEXT_APPROVER_ROLE = null; // workflow ends on reject

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
    public void setupMocks() throws Exception {
        // Mock the approval request in pending state
        ApprovalRequest pendingRequest = new ApprovalRequest();
        pendingRequest.setId(APPROVAL_REQUEST_ID);
        pendingRequest.setStatus(APPROVAL_STATUS_PENDING);
        pendingRequest.setCurrentApproverRole(APPROVER_ROLE);

        // Mock the service call to get approval request
        when(fpmCommonController.getApprovalRequest(APPROVAL_REQUEST_ID)).thenReturn(pendingRequest);

        // Mock the service call to update approval request on reject
        ApprovalRequest rejectedRequest = new ApprovalRequest();
        rejectedRequest.setId(APPROVAL_REQUEST_ID);
        rejectedRequest.setStatus(APPROVAL_STATUS_REJECTED);
        rejectedRequest.setCurrentApproverRole(NEXT_APPROVER_ROLE);

        when(fpmCommonController.rejectApprovalRequest(APPROVAL_REQUEST_ID, APPROVER_ROLE)).thenReturn(rejectedRequest);
    }

    @Test
    public void testRejectApprovalRequestUpdatesStatusAndRole() throws Exception {
        // Step 1: Login as authorized approver
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("finance.manager");
        passwordInput.sendKeys("SecurePass123");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to approval requests page
        driver.get(BASE_URL + "/approvals/pending");

        // Wait for approval request list
        WebElement approvalRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[@data-approval-id='" + APPROVAL_REQUEST_ID + "']")));

        // Step 3: Click reject button
        WebElement rejectButton = approvalRow.findElement(By.cssSelector("button.reject-btn"));
        rejectButton.click();

        // Confirm reject in modal
        WebElement confirmRejectBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmRejectBtn")));
        confirmRejectBtn.click();

        // Step 4: Verify UI shows rejected status
        WebElement statusCell = approvalRow.findElement(By.cssSelector("td.status"));
        wait.until(ExpectedConditions.textToBePresentInElement(statusCell, APPROVAL_STATUS_REJECTED));
        assertThat(statusCell.getText()).isEqualTo(APPROVAL_STATUS_REJECTED);

        // Step 5: Verify backend data consistency via mocked service
        ApprovalRequest updatedRequest = fpmCommonController.getApprovalRequest(APPROVAL_REQUEST_ID);
        assertThat(updatedRequest).isNotNull();
        assertThat(updatedRequest.getStatus()).isEqualTo(APPROVAL_STATUS_REJECTED);
        assertThat(updatedRequest.getCurrentApproverRole()).isNull(); // cleared on reject
    }

    // DTO for ApprovalRequest (mocked for test)
    public static class ApprovalRequest {
        private Long id;
        private String status;
        private String currentApproverRole;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCurrentApproverRole() {
            return currentApproverRole;
        }

        public void setCurrentApproverRole(String currentApproverRole) {
            this.currentApproverRole = currentApproverRole;
        }
    }
}
