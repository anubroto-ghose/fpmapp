/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8820
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:49:07
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.AuditTrailEntryDTO;
import com.webapp.fpmapp.service.AuditTrailService;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Integration test for Audit Trail Retrieval API with Selenium WebDriver.
 * 
 * This test mocks the AuditTrailService to simulate backend responses and
 * verifies the API behavior through Selenium-driven HTTP calls and UI validations.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuditTrailApiIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @MockBean
    private AuditTrailService auditTrailService;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
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
        baseUrl = "http://localhost:" + port + "/api/approvals/audit-trail";
    }

    private AuditTrailEntryDTO createAuditEntry(String actionType, String userId, String userName, String delegationInfo, String comments, Instant timestamp) {
        AuditTrailEntryDTO entry = new AuditTrailEntryDTO();
        entry.setActionType(actionType);
        entry.setTimestamp(timestamp);
        entry.setUserId(userId);
        entry.setUserName(userName);
        entry.setDelegationInfo(delegationInfo);
        entry.setComments(comments);
        return entry;
    }

    /**
     * Test retrieving all audit trail entries for a valid request ID without filters.
     */
    @Test
    public void testGetAuditTrailEntriesWithoutFilter() throws Exception {
        String requestId = "REQ12345";

        List<AuditTrailEntryDTO> mockEntries = Arrays.asList(
                createAuditEntry("approval", "user1", "Alice Johnson", null, "Approved successfully", Instant.parse("2024-06-01T10:15:30Z")),
                createAuditEntry("rejection", "user2", "Bob Smith", null, "Rejected due to missing docs", Instant.parse("2024-06-02T11:20:00Z")),
                createAuditEntry("delegation", "user3", "Carol White", "Delegated to Bob Smith", "Delegated for review", Instant.parse("2024-06-03T09:00:00Z"))
        );

        when(auditTrailService.getAuditTrailEntries(requestId, null)).thenReturn(mockEntries);

        // Use Selenium to perform a GET request via browser to the API endpoint
        driver.get(baseUrl + "/" + requestId);

        // The API returns JSON, so we get the page source and parse it
        String pageSource = driver.findElement(By.tagName("pre")).getText();

        AuditTrailEntryDTO[] responseEntries = objectMapper.readValue(pageSource, AuditTrailEntryDTO[].class);

        assertThat(responseEntries).isNotNull();
        assertThat(responseEntries.length).isEqualTo(3);

        // Verify fields of first entry
        AuditTrailEntryDTO firstEntry = responseEntries[0];
        assertThat(firstEntry.getActionType()).isEqualTo("approval");
        assertThat(firstEntry.getUserName()).isEqualTo("Alice Johnson");
        assertThat(firstEntry.getComments()).isEqualTo("Approved successfully");
        assertThat(firstEntry.getTimestamp()).isEqualTo(Instant.parse("2024-06-01T10:15:30Z"));
    }

    /**
     * Test retrieving audit trail entries filtered by action_type = 'approval'.
     */
    @Test
    public void testGetAuditTrailEntriesWithActionTypeFilter() throws Exception {
        String requestId = "REQ12345";
        String actionTypeFilter = "approval";

        List<AuditTrailEntryDTO> mockEntries = Collections.singletonList(
                createAuditEntry("approval", "user1", "Alice Johnson", null, "Approved successfully", Instant.parse("2024-06-01T10:15:30Z"))
        );

        when(auditTrailService.getAuditTrailEntries(requestId, actionTypeFilter)).thenReturn(mockEntries);

        driver.get(baseUrl + "/" + requestId + "?action_type=" + actionTypeFilter);

        String pageSource = driver.findElement(By.tagName("pre")).getText();

        AuditTrailEntryDTO[] responseEntries = objectMapper.readValue(pageSource, AuditTrailEntryDTO[].class);

        assertThat(responseEntries).isNotNull();
        assertThat(responseEntries.length).isEqualTo(1);
        assertThat(responseEntries[0].getActionType()).isEqualTo("approval");
    }

    /**
     * Test retrieving audit trail entries with an invalid/non-existent request ID.
     */
    @Test
    public void testGetAuditTrailEntriesWithInvalidRequestId() throws Exception {
        String invalidRequestId = "INVALID_REQ";

        when(auditTrailService.getAuditTrailEntries(invalidRequestId, null)).thenReturn(Collections.emptyList());

        driver.get(baseUrl + "/" + invalidRequestId);

        String pageSource = driver.findElement(By.tagName("pre")).getText();

        AuditTrailEntryDTO[] responseEntries = objectMapper.readValue(pageSource, AuditTrailEntryDTO[].class);

        assertThat(responseEntries).isNotNull();
        assertThat(responseEntries.length).isEqualTo(0);
    }

}
