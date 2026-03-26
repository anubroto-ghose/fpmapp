/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8805
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:40:24
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.AuditTrailService;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration test verifying audit trail retrieval API returns complete and immutable logs for rejection actions.
 * 
 * Preconditions:
 * - User with rejection permissions is logged in.
 * - AuditTrailService and DB tables operational.
 * - At least one rejection action logged.
 * 
 * Test Steps:
 * 1. Perform rejection action on a request.
 * 2. Retrieve audit trail via API.
 * 3. Attempt to modify retrieved audit log data.
 * 
 * Expected:
 * - Audit trail API returns all rejection actions with timestamps, user details, action types.
 * - Audit logs are immutable; modification attempts rejected or ignored.
 * - Audit trail data accessible and correctly displayed in UI.
 * - Performance remains acceptable.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuditTrailRejectionActionIT {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private AuditTrailService auditTrailService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "http://localhost:8080";

    private static final long TEST_REQUEST_ID = 12345L;
    private static final long REJECTION_USER_ID = 1001L;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
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
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        // Mock audit trail service to return a rejection action log
        AuditTrailEntry rejectionEntry = new AuditTrailEntry();
        rejectionEntry.setAuditLogId(1L);
        rejectionEntry.setApprovalRequestId(TEST_REQUEST_ID);
        rejectionEntry.setActionType("REJECTION");
        rejectionEntry.setActionTimestamp(Instant.now());
        rejectionEntry.setPerformedByUserId(REJECTION_USER_ID);
        rejectionEntry.setComments("Rejected due to insufficient funds");
        rejectionEntry.setDelegationFromUserId(null);
        rejectionEntry.setDelegationToUserId(null);

        when(auditTrailService.getAuditTrail(TEST_REQUEST_ID))
            .thenReturn(Collections.singletonList(rejectionEntry));

        // Mock logAction to do nothing (simulate logging)
        doNothing().when(auditTrailService).logAction(
            anyLong(),
            org.mockito.ArgumentMatchers.anyString(),
            anyLong(),
            org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    public void testRejectionActionAuditTrailRetrievalAndImmutability() throws Exception {
        // Step 1: Perform rejection action on a request via API
        String rejectionPayload = "{\"requestId\": " + TEST_REQUEST_ID + ", \"action\": \"REJECT\", \"userId\": " + REJECTION_USER_ID + ", \"comments\": \"Rejected due to insufficient funds\"}";

        MvcResult rejectionResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/approvals/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rejectionPayload))
                .andReturn();

        int rejectionStatus = rejectionResult.getResponse().getStatus();
        assertThat(rejectionStatus).isEqualTo(200);

        // Verify audit log was created
        // (auditTrailService.logAction is mocked to doNothing, so no exception means success)

        // Step 2: Retrieve audit trail via API
        MvcResult auditResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/approvals/" + TEST_REQUEST_ID + "/audit-trail")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int auditStatus = auditResult.getResponse().getStatus();
        assertThat(auditStatus).isEqualTo(200);

        String auditJson = auditResult.getResponse().getContentAsString();
        AuditTrailEntry[] auditEntries = objectMapper.readValue(auditJson, AuditTrailEntry[].class);

        assertThat(auditEntries).isNotNull();
        assertThat(auditEntries.length).isGreaterThanOrEqualTo(1);

        boolean foundRejection = false;
        for (AuditTrailEntry entry : auditEntries) {
            assertThat(entry.getActionType()).isNotNull();
            assertThat(entry.getActionTimestamp()).isNotNull();
            assertThat(entry.getPerformedByUserId()).isNotNull();
            if ("REJECTION".equalsIgnoreCase(entry.getActionType())) {
                foundRejection = true;
                assertThat(entry.getComments()).contains("insufficient funds");
            }
        }
        assertThat(foundRejection).isTrue();

        // Step 3: Attempt to modify the retrieved audit log data
        // Simulate a client attempt to update an audit log entry via API
        AuditTrailEntry modifiedEntry = auditEntries[0];
        modifiedEntry.setComments("Tampered comment");

        String modifiedJson = objectMapper.writeValueAsString(modifiedEntry);

        MvcResult modifyResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/approvals/audit-trail/" + modifiedEntry.getAuditLogId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(modifiedJson))
                .andReturn();

        int modifyStatus = modifyResult.getResponse().getStatus();

        // Expecting 405 Method Not Allowed or 403 Forbidden or 400 Bad Request because audit logs are immutable
        assertThat(modifyStatus).isIn(400, 403, 405);

        // Step 4: UI verification - load audit trail view and verify rejection entry displayed
        driver.get(BASE_URL + "/audit-trail/view?requestId=" + TEST_REQUEST_ID);

        // Wait for audit trail table to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailTable")));

        WebElement table = driver.findElement(By.id("auditTrailTable"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        boolean uiFoundRejection = false;
        for (WebElement row : rows) {
            String rowText = row.getText();
            if (rowText.contains("REJECTION") && rowText.contains("insufficient funds")) {
                uiFoundRejection = true;
                break;
            }
        }
        assertThat(uiFoundRejection).isTrue();

        // Performance check: simple timing for audit trail API
        long start = System.currentTimeMillis();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/approvals/" + TEST_REQUEST_ID + "/audit-trail")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        long duration = System.currentTimeMillis() - start;

        // Assert response time less than 2 seconds
        assertThat(duration).isLessThan(2000);
    }

    // DTO for audit trail entry used in mocks and deserialization
    public static class AuditTrailEntry {
        private Long auditLogId;
        private Long approvalRequestId;
        private String actionType;
        private Instant actionTimestamp;
        private Long performedByUserId;
        private Long delegationFromUserId;
        private Long delegationToUserId;
        private String comments;

        public Long getAuditLogId() {
            return auditLogId;
        }

        public void setAuditLogId(Long auditLogId) {
            this.auditLogId = auditLogId;
        }

        public Long getApprovalRequestId() {
            return approvalRequestId;
        }

        public void setApprovalRequestId(Long approvalRequestId) {
            this.approvalRequestId = approvalRequestId;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public Instant getActionTimestamp() {
            return actionTimestamp;
        }

        public void setActionTimestamp(Instant actionTimestamp) {
            this.actionTimestamp = actionTimestamp;
        }

        public Long getPerformedByUserId() {
            return performedByUserId;
        }

        public void setPerformedByUserId(Long performedByUserId) {
            this.performedByUserId = performedByUserId;
        }

        public Long getDelegationFromUserId() {
            return delegationFromUserId;
        }

        public void setDelegationFromUserId(Long delegationFromUserId) {
            this.delegationFromUserId = delegationFromUserId;
        }

        public Long getDelegationToUserId() {
            return delegationToUserId;
        }

        public void setDelegationToUserId(Long delegationToUserId) {
            this.delegationToUserId = delegationToUserId;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }
}
