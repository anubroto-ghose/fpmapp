/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5270
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:39:04
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.services.AuditTrailService;
import com.webapp.fpmapp.services.DelegationService;
import com.webapp.fpmapp.services.FpmcamundaService;

/**
 * Full integration Selenium test for verifying auditor role access to approval history.
 * 
 * Preconditions:
 * - Auditor user is logged in
 * 
 * This test mocks service layers to provide audit trail data and tests that the UI 
 * correctly displays the approval history with timestamps and user details.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuditorApprovalHistoryIntegrationTest {

    private static WebDriver driver;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private AuditTrailService auditTrailService;

    @MockBean
    private FpmcamundaService fpmcamundaService;

    @MockBean
    private DelegationService delegationService;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver in headless mode
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
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
        // Setup MockMvc for mocking service interactions
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * Test that an auditor can view the approval audit history filtered by timeframe and see deal sheet approvals.
     */
    @Test
    public void testAuditorViewsCompleteApprovalHistory() throws Exception {
        // Arrange: Prepare mock audit trail data for the auditor

        // Simulated audit records
        var record1 = new com.webapp.fpmapp.entities.ApprovalAuditRecord(
            101L, // auditId
            501L, // approvalRequestId
            "APPROVAL",
            10L, // userId (approver user)
            null, // no delegate
            LocalDateTime.of(2025, 8, 25, 14, 30),
            "Approved deal sheet 501"
        );

        var record2 = new com.webapp.fpmapp.entities.ApprovalAuditRecord(
            102L,
            502L,
            "REJECTION",
            11L,
            12L, // delegated
            LocalDateTime.of(2025, 8, 26, 9, 15),
            "Rejected deal sheet 502 due to missing docs"
        );

        List<com.webapp.fpmapp.entities.ApprovalAuditRecord> mockAuditRecords = Arrays.asList(record1, record2);

        // Mock service method to return the audit records
        when(auditTrailService.getAuditRecords(any(), any(), any())).thenReturn(mockAuditRecords);

        // Act: simulate auditor login and navigate to audit history section through Selenium
        driver.get(BASE_URL + "/login");

        // Login as auditor
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("auditor_user");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for redirect - simple wait (could be improved with explicit waits)
        Thread.sleep(1500);

        // Navigate to Audit History page
        driver.get(BASE_URL + "/audithistory");

        // Select time frame filter: from 2025-08-20 to 2025-08-30
        WebElement fromDateInput = driver.findElement(By.id("fromDate"));
        WebElement toDateInput = driver.findElement(By.id("toDate"));
        WebElement filterBtn = driver.findElement(By.id("filterBtn"));

        fromDateInput.clear();
        toDateInput.clear();

        fromDateInput.sendKeys("2025-08-20");
        toDateInput.sendKeys("2025-08-30");

        filterBtn.click();

        // Wait for results to load
        Thread.sleep(1500);

        // Assert: Auditor can view complete approval history entries with timestamps and user details
        WebElement auditTable = driver.findElement(By.id("approvalAuditTable"));
        List<WebElement> rows = auditTable.findElements(By.tagName("tr"));

        // We expect at least 2 records based on mock data
        assertTrue(rows.size() >= 2, "Expected at least 2 audit records in table");

        boolean foundRecord1 = false;
        boolean foundRecord2 = false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (WebElement row : rows) {
            String rowText = row.getText();

            if (rowText.contains("Approved deal sheet 501") && rowText.contains("2025-08-25 14:30")) {
                foundRecord1 = true;
                assertTrue(rowText.contains("User ID: 10"), "Approver user ID should be displayed");
            }

            if (rowText.contains("Rejected deal sheet 502") && rowText.contains("2025-08-26 09:15")) {
                foundRecord2 = true;
                assertTrue(rowText.contains("User ID: 11"), "Approver user ID should be displayed");
                assertTrue(rowText.contains("Delegate ID: 12"), "Delegate user ID should be displayed");
            }
        }

        assertTrue(foundRecord1, "Approval record 501 was not found in the audit history");
        assertTrue(foundRecord2, "Rejection record 502 was not found in the audit history");
    }
}