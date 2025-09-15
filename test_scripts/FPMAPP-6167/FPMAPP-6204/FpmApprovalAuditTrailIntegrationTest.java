/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6204
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:52:30
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.dtos.FpmDealsheetController.ApprovalActionRequest;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.ApprovalAuditService;
import com.webapp.fpmapp.services.DelegationManagementService;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Spring Boot Integration Test with Selenium WebDriver.
 * 
 * Tests audit trail logging for approve, reject, and delegate actions on approval requests.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FpmApprovalAuditTrailIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private ApprovalAuditService auditService;

    @MockBean
    private DelegationManagementService delegationService;

    @Autowired
    private FpmDealsheetController dealsheetController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "http://localhost:8080";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setupClass() {
        // Set up ChromeDriver in headless mode for CI
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        reset(auditService, delegationService);
    }

    /**
     * Test scenario:
     * Preconditions:
     * - An approval request exists with pending status.
     * - A user with appropriate approver role is logged in.
     * 
     * Steps:
     * - Perform approve action, verify audit log and UI update.
     * - Perform reject action, verify audit log and UI update.
     * - Perform delegate action, verify delegation service and audit log.
     */
    @Test
    public void testApprovalAuditTrailLoggingApproveRejectDelegate() throws Exception {
        // Mock user and approval request
        Long approvalId = 12345L;
        Long userId = 1001L;

        User loggedInUser = new User();
        loggedInUser.setId(userId);
        loggedInUser.setUsername("approverUser");
        loggedInUser.setRole("ROLE_APPROVER_LEVEL_2");

        // Mock audit service behavior
        when(auditService.logAction(eq(approvalId), eq(userId), eq("APPROVE"), any(), any(), any()))
            .thenReturn(5551L);
        when(auditService.logAction(eq(approvalId), eq(userId), eq("REJECT"), any(), any(), any()))
            .thenReturn(5552L);
        when(auditService.logAction(eq(approvalId), eq(userId), eq("DELEGATE"), any(), any(), any()))
            .thenReturn(5553L);

        // Mock delegation service behavior for delegation
        when(delegationService.assignDelegation(eq(userId), anyLong(), eq(approvalId), any())).thenReturn(true);

        // --------------------- APPROVE ACTION ---------------------
        ApprovalActionRequest approveRequest = new ApprovalActionRequest();
        approveRequest.setApprovalId(approvalId);
        approveRequest.setUserId(userId);
        approveRequest.setAction("approve");
        approveRequest.setComments("Approving the deal sheet after review.");

        MvcResult approveResult = mockMvc.perform(MockMvcRequestBuilders.post("/dealsheets/approval/action")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approveRequest)))
                .andReturn();

        int approveStatus = approveResult.getResponse().getStatus();
        assertEquals(200, approveStatus, "Approve action API should return 200 OK");

        String approveContent = approveResult.getResponse().getContentAsString();
        assertTrue(approveContent.contains("auditLogId"), "Approve response should include auditLogId");

        verify(auditService, times(1)).logAction(eq(approvalId), eq(userId), eq("APPROVE"), any(), any(), any());

        // --------------------- REJECT ACTION ---------------------
        ApprovalActionRequest rejectRequest = new ApprovalActionRequest();
        rejectRequest.setApprovalId(approvalId);
        rejectRequest.setUserId(userId);
        rejectRequest.setAction("reject");
        rejectRequest.setComments("Rejecting due to incomplete info.");

        when(auditService.logAction(eq(approvalId), eq(userId), eq("REJECT"), any(), any(), any())).thenReturn(5554L);

        MvcResult rejectResult = mockMvc.perform(MockMvcRequestBuilders.post("/dealsheets/approval/action")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rejectRequest)))
                .andReturn();

        int rejectStatus = rejectResult.getResponse().getStatus();
        assertEquals(200, rejectStatus, "Reject action API should return 200 OK");

        String rejectContent = rejectResult.getResponse().getContentAsString();
        assertTrue(rejectContent.contains("auditLogId"), "Reject response should include auditLogId");

        verify(auditService, times(1)).logAction(eq(approvalId), eq(userId), eq("REJECT"), any(), any(), any());

        // --------------------- DELEGATE ACTION ---------------------
        Long delegateeUserId = 2002L;

        // Create delegation input
        ApprovalActionRequest delegateRequest = new ApprovalActionRequest();
        delegateRequest.setApprovalId(approvalId);
        delegateRequest.setUserId(userId);
        delegateRequest.setAction("delegate");
        delegateRequest.setComments("Delegating to junior approver.");
        delegateRequest.setDelegateeUserId(delegateeUserId);
        delegateRequest.setDelegationDurationMinutes(60L);

        when(delegationService.assignDelegation(eq(userId), eq(delegateeUserId), eq(approvalId), any()))
            .thenReturn(true);

        MvcResult delegateResult = mockMvc.perform(MockMvcRequestBuilders.post("/dealsheets/approval/action")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(delegateRequest)))
                .andReturn();

        int delegateStatus = delegateResult.getResponse().getStatus();
        assertEquals(200, delegateStatus, "Delegate action API should return 200 OK");

        String delegateContent = delegateResult.getResponse().getContentAsString();
        assertTrue(delegateContent.contains("auditLogId"), "Delegate response should include auditLogId");

        verify(delegationService, times(1)).assignDelegation(eq(userId), eq(delegateeUserId), eq(approvalId), any());
        verify(auditService, times(1)).logAction(eq(approvalId), eq(userId), eq("DELEGATE"), any(), any(), any());

        // --------------------- VERIFY AUDIT LOG ORDER AND DETAIL VIA API ---------------------

        // Mock audit trail list (simulate DB ordered retrieval)
        List<com.webapp.fpmapp.entities.ApprovalAuditLog> auditLogs = Arrays.asList(
          new com.webapp.fpmapp.entities.ApprovalAuditLog(5551L, approvalId, userId, "APPROVE", LocalDateTime.now().minusMinutes(10), "Pending", "Approved", "Approving the deal sheet after review."),
          new com.webapp.fpmapp.entities.ApprovalAuditLog(5554L, approvalId, userId, "REJECT", LocalDateTime.now().minusMinutes(5), "Approved", "Rejected", "Rejecting due to incomplete info."),
          new com.webapp.fpmapp.entities.ApprovalAuditLog(5553L, approvalId, userId, "DELEGATE", LocalDateTime.now(), "Rejected", "Delegated", "Delegating to junior approver.")
        );

        when(auditService.getAuditTrail(approvalId)).thenReturn(auditLogs);

        MvcResult auditTrailResult = mockMvc.perform(MockMvcRequestBuilders.get("/approvals/audit/" + approvalId)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(200, auditTrailResult.getResponse().getStatus(), "Audit trail API should return 200 OK");

        String auditJson = auditTrailResult.getResponse().getContentAsString();
        assertTrue(auditJson.contains("APPROVE"), "Audit trail response should contain APPROVE action");
        assertTrue(auditJson.contains("REJECT"), "Audit trail response should contain REJECT action");
        assertTrue(auditJson.contains("DELEGATE"), "Audit trail response should contain DELEGATE action");

        // --------------------- SELENIUM UI VALIDATIONS ---------------------

        // Navigate to approval detail page
        driver.get(BASE_URL + "/dealsheets/approval/view/" + approvalId);

        // Wait for page load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatus")));

        // Check current approval status is 'Delegated' after last action
        WebElement statusElement = driver.findElement(By.id("approvalStatus"));
        String statusText = statusElement.getText();
        assertTrue(statusText.equalsIgnoreCase("Delegated"), "UI should show approval status as Delegated after delegation.");

        // Check audit trail logs displayed on UI
        WebElement auditLogContainer = driver.findElement(By.id("auditTrailContainer"));
        List<WebElement> auditEntries = auditLogContainer.findElements(By.className("audit-log-entry"));

        assertTrue(auditEntries.size() >= 3, "Audit trail UI should show at least 3 audit entries");

        boolean approveFound = false;
        boolean rejectFound = false;
        boolean delegateFound = false;

        for (WebElement entry : auditEntries) {
            String entryText = entry.getText();
            if (entryText.contains("APPROVE") && entryText.contains("Approving the deal sheet")) {
                approveFound = true;
            } else if (entryText.contains("REJECT") && entryText.contains("Rejecting due to incomplete info")) {
                rejectFound = true;
            } else if (entryText.contains("DELEGATE") && entryText.contains("Delegating to junior approver")) {
                delegateFound = true;
            }
        }

        assertTrue(approveFound, "Audit trail UI should display approve action details.");
        assertTrue(rejectFound, "Audit trail UI should display reject action details.");
        assertTrue(delegateFound, "Audit trail UI should display delegate action details.");
    }
}