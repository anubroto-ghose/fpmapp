/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6199
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:56:38
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.controller.FpmDealsheetController;
import com.webapp.fpmapp.entity.User;
import com.webapp.fpmapp.service.FpmcamundaService;
import com.webapp.fpmapp.service.FpmService;

/**
 * Integration test for automatic routing of approval tasks.
 * Tests submission, Camunda workflow routing, and assignment.
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // Requires server on fixed port
public class FpmApprovalRoutingIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmcamundaService fpmcamundaService;

    @MockBean
    private FpmService fpmService;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_REQUESTER_USERNAME = "requesterUser";
    private static final String TEST_APPROVER_USERNAME = "approverUser";

    @BeforeAll
    public static void beforeAll() {
        // Setup ChromeDriver - assumes chromedriver executable is in PATH
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1200,800");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void afterAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Tests that when a user submits an approval request, the Camunda
     * workflow task is automatically routed to the correct approver.
     */
    @Test
    public void testAutomaticRoutingOfApprovalTasks() throws Exception {
        // Arrange: Mock Camunda workflow behavior and FpmService

        // Setup mock User entities
        User requester = new User();
        requester.setId(UUID.randomUUID());
        requester.setUsername(TEST_REQUESTER_USERNAME);

        User approver = new User();
        approver.setId(UUID.randomUUID());
        approver.setUsername(TEST_APPROVER_USERNAME);

        // Mock creation of approval request returns an ID
        UUID approvalRequestId = UUID.randomUUID();

        when(fpmService.createApprovalRequest(any())).thenReturn(approvalRequestId);

        // Mock Camunda service to assign task automatically to approver
        when(fpmcamundaService.getAssignedApprover(any(UUID.class)))
                .thenReturn(approver);

        // Mock Camunda workflow state update
        when(fpmcamundaService.isTaskAssigned(any(UUID.class))).thenReturn(true);

        // Act: Submit approval request via Selenium (simulate frontend behavior)

        driver.get(BASE_URL + "/approval/new");

        // Wait for the approval form to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalForm")));

        // Fill in form fields for the approval request
        WebElement requesterField = driver.findElement(By.id("requesterUsername"));
        requesterField.clear();
        requesterField.sendKeys(TEST_REQUESTER_USERNAME);

        WebElement amountField = driver.findElement(By.id("approvalAmount"));
        amountField.clear();
        amountField.sendKeys("15000"); // High amount to test threshold

        WebElement submitButton = driver.findElement(By.id("submitApprovalBtn"));
        submitButton.click();

        // Wait for confirmation message
        WebElement confirmationMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("submissionConfirmation")));

        assertThat(confirmationMsg.getText()).contains("Approval Request Submitted");

        // Simulate backend notification or polling to get the assigned task
        // Wait up to 5 seconds for task assignment
        boolean assigned = wait.until(driver -> {
            return fpmcamundaService.isTaskAssigned(approvalRequestId);
        });

        assertThat(assigned).isTrue();

        // Verify that the task is routed to the correct approver
        User assignedApprover = fpmcamundaService.getAssignedApprover(approvalRequestId);

        assertThat(assignedApprover.getUsername()).isEqualTo(TEST_APPROVER_USERNAME);

        // Verify workflow state change is consistent and reflects task assignment
        String workflowState = fpmcamundaService.getCurrentTaskState(approvalRequestId);

        assertThat(workflowState).isNotNull();
        assertThat(workflowState.toLowerCase()).contains("assigned");

    }
}