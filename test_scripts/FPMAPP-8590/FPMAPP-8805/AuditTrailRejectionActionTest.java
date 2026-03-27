/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8805
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:58:48
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
import java.util.Map;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.AuditLogDTO;
import com.webapp.fpmapp.service.AuditTrailService;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

/**
 * Integration test for verifying audit trail retrieval API returns complete and immutable logs for rejection actions.
 * 
 * Preconditions:
 * - User with rejection permissions is logged in.
 * - AuditTrailService and DB tables operational.
 * - At least one rejection action logged.
 * 
 * This test uses Selenium WebDriver to simulate UI interaction and mocks AuditTrailService for API responses.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class AuditTrailRejectionActionTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private AuditTrailService auditTrailService;

    private static final String BASE_URL = "http://localhost:8080";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
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
    public void setup() {
        // Mock audit trail service to return a rejection action log
        AuditLogDTO rejectionLog = new AuditLogDTO();
        rejectionLog.setId(1001L);
        rejectionLog.setActionType("REJECTION");
        rejectionLog.setTimestamp(Instant.now().toString());
        rejectionLog.setUserId("user_rejector");
        rejectionLog.setDetails("Request #1234 rejected due to insufficient funds.");

        when(auditTrailService.getAuditLogsByActionType("REJECTION"))
            .thenReturn(Collections.singletonList(rejectionLog));
    }

    @Test
    public void testAuditTrailRejectionActionIsCompleteAndImmutable() throws Exception {
        // Step 1: Perform a rejection action on a request via UI
        driver.get(BASE_URL + "/login");

        // Login as user with rejection permissions
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("user_rejector");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to requests page
        driver.get(BASE_URL + "/requests");

        // Find a request to reject (simulate)
        WebElement requestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tr[data-request-id='1234']")));
        WebElement rejectButton = requestRow.findElement(By.cssSelector("button.reject-action"));
        rejectButton.click();

        // Confirm rejection modal
        WebElement confirmRejectBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmRejectBtn")));
        confirmRejectBtn.click();

        // Wait for success notification
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notificationSuccess")));
        assertThat(successMsg.getText()).containsIgnoringCase("rejected successfully");

        // Step 2: Retrieve the audit trail via the API
        // Simulate API call to /api/audit-trail?actionType=REJECTION
        String apiUrl = BASE_URL + "/api/audit-trail?actionType=REJECTION";

        RestTemplate restTemplate = new RestTemplate();
        String responseJson = restTemplate.getForObject(apiUrl, String.class);

        List<AuditLogDTO> auditLogs = objectMapper.readValue(responseJson, new TypeReference<List<AuditLogDTO>>() {});

        // Assert that the audit trail contains the rejection action with correct details
        assertThat(auditLogs).isNotEmpty();
        AuditLogDTO log = auditLogs.get(0);
        assertThat(log.getActionType()).isEqualTo("REJECTION");
        assertThat(log.getUserId()).isEqualTo("user_rejector");
        assertThat(log.getDetails()).contains("Request #1234 rejected");
        assertThat(log.getTimestamp()).isNotNull();

        // Step 3: Attempt to modify the retrieved audit log data
        // Simulate an attempt to modify the log via API (PUT request)
        // The system should reject or ignore this modification

        // Prepare modified log
        AuditLogDTO modifiedLog = new AuditLogDTO();
        modifiedLog.setId(log.getId());
        modifiedLog.setActionType("APPROVAL"); // changed action type
        modifiedLog.setUserId("user_rejector");
        modifiedLog.setTimestamp(log.getTimestamp());
        modifiedLog.setDetails("Modified details");

        try {
            restTemplate.put(BASE_URL + "/api/audit-trail/" + log.getId(), modifiedLog);
        } catch (Exception e) {
            // Expected: API should reject modification
            assertThat(e.getMessage()).containsIgnoringCase("immutable");
        }

        // Retrieve again to verify no changes
        String responseJsonAfter = restTemplate.getForObject(apiUrl, String.class);
        List<AuditLogDTO> auditLogsAfter = objectMapper.readValue(responseJsonAfter, new TypeReference<List<AuditLogDTO>>() {});
        AuditLogDTO logAfter = auditLogsAfter.get(0);

        assertThat(logAfter.getActionType()).isEqualTo("REJECTION");
        assertThat(logAfter.getDetails()).contains("Request #1234 rejected");

        // Step 4: Verify audit trail data is displayed correctly in UI
        driver.get(BASE_URL + "/audit-trail");

        // Filter by rejection actions
        WebElement filterSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("actionTypeFilter")));
        filterSelect.click();
        WebElement rejectionOption = driver.findElement(By.cssSelector("option[value='REJECTION']"));
        rejectionOption.click();

        WebElement filterButton = driver.findElement(By.id("filterBtn"));
        filterButton.click();

        // Wait for table to load
        WebElement auditTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailTable")));

        // Verify the rejection log is present
        List<WebElement> rows = auditTable.findElements(By.tagName("tr"));
        boolean found = false;
        for (WebElement row : rows) {
            if (row.getText().contains("REJECTION") && row.getText().contains("user_rejector") && row.getText().contains("Request #1234 rejected")) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();

        // Step 5: Performance check (basic) - ensure page loads within acceptable time
        long startTime = System.currentTimeMillis();
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailTable")));
        long duration = System.currentTimeMillis() - startTime;

        // Assert page loads within 3 seconds
        assertThat(duration).isLessThan(3000);
    }
}
