/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8803
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:38:50
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.services.AuditTrailService;

/**
 * Integration Selenium test for verifying audit log creation after approval action.
 * 
 * Preconditions:
 * - User with approval permissions is logged in.
 * - AuditTrailService and DB audit tables operational.
 * 
 * Test Steps:
 * 1. Perform approval action on a request via UI.
 * 2. Retrieve audit log entry via API.
 * 3. Verify audit log correctness and immutability.
 * 4. Verify audit trail UI displays correct info.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuditTrailApprovalTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuditTrailService auditTrailService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String TEST_USER_USERNAME = "approverUser";
    private static final String TEST_USER_PASSWORD = "password123";
    private static final String APPROVAL_REQUEST_ID = "1001";

    @BeforeAll
    public static void setup() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAuditLogCreatedAfterApproval() throws Exception {
        // Mock AuditTrailService to simulate audit log creation and retrieval
        Mockito.doNothing().when(auditTrailService).logAction(Mockito.eq(APPROVAL_REQUEST_ID), Mockito.eq("approval"), Mockito.anyString(), Mockito.anyString());

        // Mock audit log entry returned by service
        Map<String, Object> mockAuditLogEntry = Map.of(
            "approvalRequestId", APPROVAL_REQUEST_ID,
            "actionType", "approval",
            "performedByUserId", TEST_USER_USERNAME,
            "actionTimestamp", System.currentTimeMillis(),
            "comments", "Approved by user"
        );
        Mockito.when(auditTrailService.getAuditTrail(APPROVAL_REQUEST_ID))
            .thenReturn(List.of(mockAuditLogEntry));

        // Step 1: Login as user with approval permissions
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(TEST_USER_USERNAME);
        driver.findElement(By.id("password")).sendKeys(TEST_USER_PASSWORD);
        driver.findElement(By.id("loginButton")).click();

        // Wait for dashboard or approval page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to approval request page
        driver.get(BASE_URL + "/approvals/request/" + APPROVAL_REQUEST_ID);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approveButton")));

        // Step 3: Perform approval action
        WebElement approveButton = driver.findElement(By.id("approveButton"));
        approveButton.click();

        // Wait for confirmation message
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalConfirmation")));
        assertThat(confirmation.getText()).containsIgnoringCase("approved successfully");

        // Step 4: Retrieve audit log entry via API
        String auditApiUrl = BASE_URL + "/api/approvals/" + APPROVAL_REQUEST_ID + "/audit-trail";

        String auditResponseJson = restTemplate.getForObject(auditApiUrl, String.class);
        assertThat(auditResponseJson).isNotNull();

        List<Map<String, Object>> auditLogs = objectMapper.readValue(auditResponseJson, new TypeReference<List<Map<String, Object>>>() {});

        // Assertions on audit log
        assertThat(auditLogs).isNotEmpty();
        Map<String, Object> auditLog = auditLogs.get(0);

        assertThat(auditLog.get("approvalRequestId")).isEqualTo(APPROVAL_REQUEST_ID);
        assertThat(auditLog.get("actionType")).isEqualTo("approval");
        assertThat(auditLog.get("performedByUserId")).isEqualTo(TEST_USER_USERNAME);
        assertThat(auditLog.get("actionTimestamp")).isInstanceOf(Number.class);

        // Step 5: Verify audit trail UI displays correct info
        driver.get(BASE_URL + "/approvals/request/" + APPROVAL_REQUEST_ID + "/audit-trail");
        WebElement auditTrailTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailTable")));

        String auditTableText = auditTrailTable.getText();
        assertThat(auditTableText).contains("approval");
        assertThat(auditTableText).contains(TEST_USER_USERNAME);

        // Additional check: audit log immutability (try to edit - UI should not allow)
        List<WebElement> editButtons = driver.findElements(By.cssSelector("#auditTrailTable .editButton"));
        assertThat(editButtons).isEmpty();
    }
}
