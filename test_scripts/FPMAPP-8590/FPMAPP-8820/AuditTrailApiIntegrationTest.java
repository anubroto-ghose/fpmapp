/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8820
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:51:46
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.AuditTrailEntryDTO;
import com.webapp.fpmapp.service.AuditTrailService;

/**
 * Integration test for Audit Trail retrieval API with Selenium WebDriver and Spring Boot context.
 * 
 * This test mocks AuditTrailService responses and verifies API behavior and UI rendering.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class AuditTrailApiIntegrationTest {

    private static WebDriver driver;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private AuditTrailService auditTrailService;

    private static final String BASE_URL = "http://localhost:8080";

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    /**
     * Test case: Verify audit trail retrieval API returns all entries for a valid request ID without filter.
     * Then verify filtering by action_type = 'approval'.
     * Finally verify behavior for invalid request ID.
     */
    @Test
    public void testAuditTrailRetrievalApi() throws Exception {
        String validRequestId = "REQ12345";
        String invalidRequestId = "INVALID_REQ";

        // Prepare mock audit trail entries
        AuditTrailEntryDTO entry1 = new AuditTrailEntryDTO();
        entry1.setAuditLogId(1L);
        entry1.setApprovalRequestId(validRequestId);
        entry1.setActionType("approval");
        entry1.setActionTimestamp(Instant.parse("2026-03-25T10:15:30Z"));
        entry1.setPerformedByUserId("user1");
        entry1.setPerformedByUserName("Alice Manager");
        entry1.setDelegationFromUserId(null);
        entry1.setDelegationToUserId(null);
        entry1.setComments("Approved by manager");

        AuditTrailEntryDTO entry2 = new AuditTrailEntryDTO();
        entry2.setAuditLogId(2L);
        entry2.setApprovalRequestId(validRequestId);
        entry2.setActionType("delegation");
        entry2.setActionTimestamp(Instant.parse("2026-03-25T11:00:00Z"));
        entry2.setPerformedByUserId("user2");
        entry2.setPerformedByUserName("Bob Director");
        entry2.setDelegationFromUserId("user2");
        entry2.setDelegationToUserId("user3");
        entry2.setComments("Delegated to user3");

        AuditTrailEntryDTO entry3 = new AuditTrailEntryDTO();
        entry3.setAuditLogId(3L);
        entry3.setApprovalRequestId(validRequestId);
        entry3.setActionType("override");
        entry3.setActionTimestamp(Instant.parse("2026-03-25T12:30:00Z"));
        entry3.setPerformedByUserId("admin1");
        entry3.setPerformedByUserName("Admin User");
        entry3.setDelegationFromUserId(null);
        entry3.setDelegationToUserId(null);
        entry3.setComments("Override due to urgent compliance");

        List<AuditTrailEntryDTO> allEntries = Arrays.asList(entry1, entry2, entry3);
        List<AuditTrailEntryDTO> approvalEntries = Collections.singletonList(entry1);

        // Mock service behavior
        when(auditTrailService.getAuditTrail(eq(validRequestId), eq(null))).thenReturn(allEntries);
        when(auditTrailService.getAuditTrail(eq(validRequestId), eq("approval"))).thenReturn(approvalEntries);
        when(auditTrailService.getAuditTrail(eq(invalidRequestId), any())).thenReturn(Collections.emptyList());

        // Step 1: Call API with valid request ID, no filter
        MvcResult resultAll = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/approvals/audit-trail/" + validRequestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int statusAll = resultAll.getResponse().getStatus();
        assertThat(statusAll).isEqualTo(200);

        String jsonResponseAll = resultAll.getResponse().getContentAsString();
        AuditTrailEntryDTO[] responseEntriesAll = objectMapper.readValue(jsonResponseAll, AuditTrailEntryDTO[].class);

        assertThat(responseEntriesAll).hasSize(3);
        assertThat(responseEntriesAll).extracting("actionType").containsExactlyInAnyOrder("approval", "delegation", "override");

        // Step 2: Call API with valid request ID and action_type=approval
        MvcResult resultApproval = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/approvals/audit-trail/" + validRequestId)
                        .param("action_type", "approval")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int statusApproval = resultApproval.getResponse().getStatus();
        assertThat(statusApproval).isEqualTo(200);

        String jsonResponseApproval = resultApproval.getResponse().getContentAsString();
        AuditTrailEntryDTO[] responseEntriesApproval = objectMapper.readValue(jsonResponseApproval, AuditTrailEntryDTO[].class);

        assertThat(responseEntriesApproval).hasSize(1);
        assertThat(responseEntriesApproval[0].getActionType()).isEqualTo("approval");

        // Step 3: Call API with invalid request ID
        MvcResult resultInvalid = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/approvals/audit-trail/" + invalidRequestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int statusInvalid = resultInvalid.getResponse().getStatus();
        assertThat(statusInvalid).isEqualTo(200);

        String jsonResponseInvalid = resultInvalid.getResponse().getContentAsString();
        AuditTrailEntryDTO[] responseEntriesInvalid = objectMapper.readValue(jsonResponseInvalid, AuditTrailEntryDTO[].class);

        assertThat(responseEntriesInvalid).isEmpty();

        // --- Selenium UI verification ---
        // Assuming a simple UI page exists at /audit-trail-viewer?requestId=REQ12345
        // that displays audit trail entries in a table with id 'auditTrailTable'

        // Navigate to audit trail viewer page for validRequestId
        driver.get(BASE_URL + "/audit-trail-viewer?requestId=" + validRequestId);

        // Wait for table to load (simple implicit wait)
        Thread.sleep(1000);

        WebElement table = driver.findElement(By.id("auditTrailTable"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        // Header + 3 data rows expected
        assertThat(rows.size()).isEqualTo(4);

        // Verify first data row contains expected action type and comments
        WebElement firstDataRow = rows.get(1);
        List<WebElement> cells = firstDataRow.findElements(By.tagName("td"));

        assertThat(cells.get(2).getText()).isEqualTo("approval");
        assertThat(cells.get(6).getText()).isEqualTo("Approved by manager");

        // Now test filtering by action type 'approval' via UI filter
        WebElement filterInput = driver.findElement(By.id("actionTypeFilter"));
        filterInput.clear();
        filterInput.sendKeys("approval");

        WebElement filterButton = driver.findElement(By.id("filterButton"));
        filterButton.click();

        Thread.sleep(1000); // wait for filter to apply

        rows = table.findElements(By.tagName("tr"));
        // Header + 1 data row expected
        assertThat(rows.size()).isEqualTo(2);

        WebElement filteredRow = rows.get(1);
        cells = filteredRow.findElements(By.tagName("td"));
        assertThat(cells.get(2).getText()).isEqualTo("approval");

        // Test UI behavior for invalid request ID
        driver.get(BASE_URL + "/audit-trail-viewer?requestId=" + invalidRequestId);
        Thread.sleep(1000);

        WebElement noDataMessage = driver.findElement(By.id("noDataMessage"));
        assertThat(noDataMessage.isDisplayed()).isTrue();
        assertThat(noDataMessage.getText()).contains("No audit trail entries found");
    }
}