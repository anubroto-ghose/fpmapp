/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5262
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:49:05
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration Selenium test verifying audit trail logging for approval actions.
 * 
 * Preconditions:
 * - System configured with mock audit logging
 * - Compliance officer user exists
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class ApprovalAuditTrailIntegrationTest {

    private static WebDriver driver;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Mock user representing a compliance officer
    private static final String COMPLIANCE_OFFICER_USERNAME = "compliance.officer";
    private static final String COMPLIANCE_OFFICER_PASSWORD = "StrongPassword123!";

    // Sample transaction id
    private static final UUID SAMPLE_TRANSACTION_ID = UUID.randomUUID();

    @BeforeAll
    public static void setUp() {
        // Set path to chromedriver executable if required by environment
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run headless for CI
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    public void testComplianceOfficerLogin() {
        driver.get("http://localhost:8080/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(COMPLIANCE_OFFICER_USERNAME);
        passwordInput.sendKeys(COMPLIANCE_OFFICER_PASSWORD);
        loginButton.click();

        // Wait for possible redirect or dashboard load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();

        assertThat(currentUrl).contains("/dashboard").withFailMessage("Login failed or dashboard not loaded");

        WebElement welcomeElement = driver.findElement(By.id("welcomeUser"));
        assertThat(welcomeElement.getText()).contains(COMPLIANCE_OFFICER_USERNAME);
    }

    @Test
    @Order(2)
    public void testPerformApprovalActionLogsAudit() throws Exception {

        // Arrange: Mock user retrieval for approval action
        User mockApprover = new User();
        mockApprover.setId(1001L);
        mockApprover.setUsername(COMPLIANCE_OFFICER_USERNAME);
        mockApprover.setRoles(Collections.singletonList("ROLE_COMPLIANCE_OFFICER"));

        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockApprover);

        // Arrange: Mock audit trail service to verify logging
        // Here assuming audit trail access via FpmCommonController or similar
        // Instead, perform real HTTP approval request and verify stored logs by GET

        // Step 2: Perform approval action via REST API (simulate transaction approval)
        String approvalPayload = OBJECT_MAPPER.writeValueAsString(
                Collections.singletonMap("transactionId", SAMPLE_TRANSACTION_ID.toString())
        );

        MvcResult approvalResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/approvals/request")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(approvalPayload))
                .andReturn();

        int status = approvalResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        // Step 3: Query audit logs for the approval action
        MvcResult auditResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/audit/approvals")
                                      .param("user", COMPLIANCE_OFFICER_USERNAME)
                                      .param("actionType", "APPROVAL")
                                      .param("transactionId", SAMPLE_TRANSACTION_ID.toString())
                                      .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(auditResult.getResponse().getStatus()).isEqualTo(200);

        String jsonResponse = auditResult.getResponse().getContentAsString();

        // Parse response to verify audit entry presence
        
        // Simple check for user and action presence in logs JSON
        assertThat(jsonResponse).isNotNull();
        assertThat(jsonResponse).contains(COMPLIANCE_OFFICER_USERNAME);
        assertThat(jsonResponse).contains("APPROVAL");
        assertThat(jsonResponse).contains(SAMPLE_TRANSACTION_ID.toString());

        // Additional: verify timestamp present and valid
        // For brevity, expect timestamp field
        assertThat(jsonResponse).contains("actionTimestamp");
    }

    @Test
    @Order(3)
    public void testAuditTrailDisplayedInUI() {

        // Navigate to audit trail page
        driver.get("http://localhost:8080/audit/approvals");

        // Filter logs for compliance officer
        WebElement filterUserInput = driver.findElement(By.id("filter-user"));
        WebElement filterActionSelect = driver.findElement(By.id("filter-action"));
        WebElement filterTransactionInput = driver.findElement(By.id("filter-transaction"));
        WebElement filterButton = driver.findElement(By.id("filter-btn"));

        filterUserInput.clear();
        filterUserInput.sendKeys(COMPLIANCE_OFFICER_USERNAME);
        filterActionSelect.sendKeys("APPROVAL");
        filterTransactionInput.clear();
        filterTransactionInput.sendKeys(SAMPLE_TRANSACTION_ID.toString());

        filterButton.click();

        // Wait for results
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<WebElement> auditRows = driver.findElements(By.cssSelector("table#auditTrailTable tbody tr"));

        assertThat(auditRows.size()).isGreaterThan(0).withFailMessage("No audit records found for filters");

        // Verify presence of compliance officer username and approval action in UI rows
        boolean foundMatchingEntry = auditRows.stream().anyMatch(row ->
                row.getText().contains(COMPLIANCE_OFFICER_USERNAME) && row.getText().contains("APPROVAL") && row.getText().contains(SAMPLE_TRANSACTION_ID.toString())
        );

        assertThat(foundMatchingEntry).isTrue();
    }
}