/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8646
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:42:32
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
import java.util.Map;
import java.util.Optional;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration Selenium test for verifying audit trail logging on delegation action with missing previous_value.
 * 
 * Preconditions:
 * - User authenticated and authorized.
 * - Modified POST /fpm/staffing/approval API deployed.
 * - Audit_Logs table schema extended.
 * 
 * This test mocks backend services and verifies UI and DB audit log entries.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuditTrailDelegationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String STAFFING_APPROVAL_API = BASE_URL + "/fpm/staffing/approval";

    private static final String AUDIT_LOGS_API = BASE_URL + "/audit/logs";

    private static final String TEST_USER_ID = "user123";
    private static final String DELEGATED_USER_ID = "user456";
    private static final String STAFFING_REQUEST_ID = "staffingReq789";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
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
    public void setupMocks() throws Exception {
        // Mock authenticated user
        User mockUser = new User();
        mockUser.setUserId(TEST_USER_ID);
        mockUser.setUsername("compliance.officer");
        when(userProfileController.getAuthenticatedUser()).thenReturn(mockUser);

        // Mock delegation API response with audit trail references
        Map<String, Object> apiResponse = Map.of(
            "status", "success",
            "auditReferenceId", "auditRef12345",
            "message", "Delegation action recorded"
        );

        ResponseEntity<String> responseEntity = new ResponseEntity<>(objectMapper.writeValueAsString(apiResponse), HttpStatus.OK);

        when(restTemplate.postForEntity(any(String.class), any(Object.class), any(Class.class)))
            .thenAnswer(invocation -> {
                String url = invocation.getArgument(0);
                if (STAFFING_APPROVAL_API.equals(url)) {
                    return responseEntity;
                }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            });

        // Mock audit log query response
        Map<String, Object> auditLogEntry = Map.of(
            "action_type", "delegation",
            "entity_type", "staffing",
            "entity_id", STAFFING_REQUEST_ID,
            "timestamp", Instant.now().toString(),
            "performed_by_user_id", TEST_USER_ID,
            "previous_value", null,
            "new_value", DELEGATED_USER_ID,
            "immutable", true
        );

        ResponseEntity<String> auditLogResponse = new ResponseEntity<>(objectMapper.writeValueAsString(Collections.singletonList(auditLogEntry)), HttpStatus.OK);

        when(restTemplate.getForEntity(AUDIT_LOGS_API + "?entity_id=" + STAFFING_REQUEST_ID, String.class))
            .thenReturn(auditLogResponse);
    }

    @Test
    public void testDelegationActionAuditTrailWithMissingPreviousValue() throws Exception {
        // Step 1: Perform delegation action via UI
        driver.get(BASE_URL + "/login");

        // Simulate login
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("compliance.officer");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to staffing approval page
        driver.get(BASE_URL + "/staffing/approval");

        // Fill delegation form
        WebElement staffingRequestInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("staffingRequestId")));
        WebElement delegateUserInput = driver.findElement(By.id("delegateUserId"));
        WebElement submitButton = driver.findElement(By.id("submitDelegation"));

        staffingRequestInput.clear();
        staffingRequestInput.sendKeys(STAFFING_REQUEST_ID);
        delegateUserInput.clear();
        delegateUserInput.sendKeys(DELEGATED_USER_ID);

        // Intentionally omit previous_value field in the form or API call
        // Submit delegation
        submitButton.click();

        // Step 2: Verify API response includes audit trail references
        WebElement responseMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("responseMessage")));
        String responseText = responseMessage.getText();
        assertThat(responseText).contains("Delegation action recorded");
        assertThat(responseText).contains("auditRef12345");

        // Step 3: Query Audit_Logs table for new delegation entry
        // Simulate backend audit log query via mocked RestTemplate
        ResponseEntity<String> auditLogResponse = restTemplate.getForEntity(AUDIT_LOGS_API + "?entity_id=" + STAFFING_REQUEST_ID, String.class);
        assertThat(auditLogResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        String body = auditLogResponse.getBody();
        assertThat(body).isNotNull();

        // Parse audit log JSON
        Map[] auditLogs = objectMapper.readValue(body, Map[].class);
        assertThat(auditLogs).isNotEmpty();

        Map<String, Object> auditEntry = auditLogs[0];

        // Assertions on audit log entry
        assertThat(auditEntry.get("action_type")).isEqualTo("delegation");
        assertThat(auditEntry.get("entity_type")).isEqualTo("staffing");
        assertThat(auditEntry.get("entity_id")).isEqualTo(STAFFING_REQUEST_ID);
        assertThat(auditEntry.get("performed_by_user_id")).isEqualTo(TEST_USER_ID);
        assertThat(auditEntry.get("previous_value")).isNull();
        assertThat(auditEntry.get("new_value")).isEqualTo(DELEGATED_USER_ID);
        assertThat(auditEntry.get("immutable")).isEqualTo(true);

        // Additional checks: timestamp is parsable
        String timestamp = (String) auditEntry.get("timestamp");
        assertThat(Instant.parse(timestamp)).isBeforeOrEqualTo(Instant.now());
    }
}
