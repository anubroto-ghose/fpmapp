/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6233
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:30:53
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.services.ApprovalAuditService;
import com.webapp.fpmapp.services.DelegationManagementService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class DealSheetApprovalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApprovalAuditService approvalAuditService;

    @MockBean
    private DelegationManagementService delegationManagementService;

    @Autowired
    private FpmDealsheetController fpmDealsheetController;

    private WebDriver driver;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        // Setup Selenium WebDriver for Chrome (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1200,800");
        driver = new ChromeDriver(options);

        // Mock audit log creation to store auditLogId and verify data
        doAnswer(invocation -> {
            Long approvalId = invocation.getArgument(0);
            Long userId = invocation.getArgument(1);
            String action = invocation.getArgument(2);
            Instant timestamp = invocation.getArgument(3);
            String prevStatus = invocation.getArgument(4);
            String newStatus = invocation.getArgument(5);

            // Return a dummy auditLogId (simulate DB-generated ID)
            return 9999L;
        }).when(approvalAuditService).logApprovalAction(any(Long.class), any(Long.class), 
                any(String.class), any(Instant.class), any(String.class), any(String.class));

        // Set up delegation management service mock for validation to always return true
        when(delegationManagementService.validateDelegation(any(Long.class), any(Long.class))).thenReturn(true);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Integration test for hierarchical role-based approval of a deal sheet.
     * Simulates a Manager user approving a deal sheet and verifies:
     * - Approval status update
     * - Current approver role moves to Director
     * - Audit entry creation
     * - Correct response contents
     */
    @Test
    @WithMockUser(username = "managerUser", roles = {"MANAGER"})
    public void testManagerApprovalUpdatesStatusAndAudit() throws Exception {
        // Given
        Long dealSheetId = 12345L;
        Long userId = 111L; // managerUser simulated id
        String roleId = "MANAGER";

        // Mock behavior: fpmDealsheetController.approveDealSheet handles approval and audit
        // We simulate the service call and return of approval status
        String initialStatus = "Pending Approval";
        String expectedStatusAfterApproval = "Approved by Manager";
        String nextApproverRole = "DIRECTOR";

        // Prepare request JSON
        Map<String, Object> requestBody = Map.of(
                "action", "approve",
                "dealSheetId", dealSheetId,
                "roleId", roleId
        );

        // Mock controller method call response structure
        // We assume controller method returns a Map<String, Object> with required fields
        when(approvalAuditService.logApprovalAction(
                eq(dealSheetId),
                eq(userId),
                eq("approve"),
                any(Instant.class),
                eq(initialStatus),
                eq(expectedStatusAfterApproval))).thenReturn(9999L);

        // Because detailed controller mocking can be complex, we use MockMvc here

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/deal-sheet/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseString, Map.class);

        // Assertions on response JSON
        assertThat(responseMap).containsKeys("approvalStatus", "currentApproverRole", "auditLogId");
        assertThat(responseMap.get("approvalStatus")).isEqualTo(expectedStatusAfterApproval);
        assertThat(responseMap.get("currentApproverRole")).isEqualTo(nextApproverRole);
        assertThat(responseMap.get("auditLogId")).isEqualTo(9999);

        // Using Selenium to simulate browser UI interaction for approval status verification
        // Navigate to UI page displaying deal sheet approval status
        String baseUrl = "http://localhost:8080/deal-sheet/" + dealSheetId + "/view";
        driver.get(baseUrl);

        // Wait and assert approval status element shows "Approved by Manager"
        WebElement approvalStatusElem = driver.findElement(By.id("approvalStatus"));
        assertThat(approvalStatusElem.getText()).isEqualToIgnoringCase(expectedStatusAfterApproval);

        // Assert current approver role displayed correctly
        WebElement currentApproverElem = driver.findElement(By.id("currentApproverRole"));
        assertThat(currentApproverElem.getText()).isEqualToIgnoringCase(nextApproverRole);

        // Assert audit trail entry presence in UI audit log table
        WebElement auditLogTable = driver.findElement(By.id("auditTrailTable"));
        assertThat(auditLogTable.getText()).contains("approve");
        assertThat(auditLogTable.getText()).contains("managerUser");
    }
}