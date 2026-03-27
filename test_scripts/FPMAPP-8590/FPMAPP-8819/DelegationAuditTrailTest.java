/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8819
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:49:48
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration Selenium test for verifying delegation audit trail logging.
 * 
 * Preconditions:
 * - Approval_Audit_Trail table is available.
 * - User with delegation rights is logged in.
 * - System supports delegation of approval requests.
 * 
 * This test mocks backend services and verifies UI delegation action,
 * audit trail persistence, and retrieval via API.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DelegationAuditTrailTest {

    private static WebDriver driver;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @InjectMocks
    private FpmUserProfileController fpmUserProfileController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DELEGATION_ACTION_TYPE = "delegation";

    private static final String APPROVAL_REQUEST_ID = "REQ123456";

    private static final User DELEGATED_USER = new User();

    private static final User DELEGATING_USER = new User();

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Setup test users
        DELEGATED_USER.setId(2001L);
        DELEGATED_USER.setUsername("delegateUser");

        DELEGATING_USER.setId(1001L);
        DELEGATING_USER.setUsername("delegatorUser");
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock logged in user with delegation rights
        when(userProfileController.getLoggedInUser()).thenReturn(DELEGATING_USER);

        // Mock delegation API call
        when(fpmCommonController.delegateApprovalRequest(any(String.class), any(Long.class)))
            .thenAnswer(invocation -> {
                String requestId = invocation.getArgument(0);
                Long delegatedUserId = invocation.getArgument(1);

                // Simulate audit trail entry creation
                Map<String, Object> auditEntry = Map.of(
                    "requestId", requestId,
                    "actionType", DELEGATION_ACTION_TYPE,
                    "delegatedUserId", delegatedUserId,
                    "delegatedUsername", DELEGATED_USER.getUsername(),
                    "timestamp", Instant.now().toString(),
                    "immutable", true
                );

                return auditEntry;
            });

        // Mock audit trail retrieval API
        when(fpmCommonController.getAuditTrailByRequestIdAndActionType(APPROVAL_REQUEST_ID, DELEGATION_ACTION_TYPE))
            .thenReturn(List.of(Map.of(
                "requestId", APPROVAL_REQUEST_ID,
                "actionType", DELEGATION_ACTION_TYPE,
                "delegatedUserId", DELEGATED_USER.getId(),
                "delegatedUsername", DELEGATED_USER.getUsername(),
                "timestamp", Instant.now().toString(),
                "immutable", true
            )));
    }

    @Test
    public void testDelegationAuditTrailLogging() throws Exception {
        // Step 1: Log in as delegating user
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(DELEGATING_USER.getUsername());
        passwordInput.sendKeys("password123"); // assuming test password
        loginButton.click();

        // Wait for redirect to dashboard
        Thread.sleep(2000);
        assertThat(driver.getCurrentUrl()).endsWith("/dashboard");

        // Step 2: Navigate to approval requests page
        driver.get(BASE_URL + "/approvals");
        Thread.sleep(1000);

        // Find the approval request to delegate
        WebElement approvalRequestRow = driver.findElement(By.xpath("//tr[td/text()='" + APPROVAL_REQUEST_ID + "']"));
        assertThat(approvalRequestRow).isNotNull();

        // Click delegate button
        WebElement delegateButton = approvalRequestRow.findElement(By.cssSelector("button.delegate-btn"));
        delegateButton.click();

        // Wait for delegation modal
        Thread.sleep(1000);

        // Select delegated user from dropdown
        WebElement userDropdown = driver.findElement(By.id("delegateUserSelect"));
        userDropdown.click();
        WebElement delegatedUserOption = driver.findElement(By.xpath("//option[@value='" + DELEGATED_USER.getId() + "']"));
        delegatedUserOption.click();

        // Confirm delegation
        WebElement confirmButton = driver.findElement(By.id("confirmDelegateBtn"));
        confirmButton.click();

        // Wait for delegation to process
        Thread.sleep(2000);

        // Step 3: Verify delegation action recorded in audit trail via API
        List<Map<String, Object>> auditTrailEntries = webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path("/api/audit-trail")
                .queryParam("requestId", APPROVAL_REQUEST_ID)
                .queryParam("actionType", DELEGATION_ACTION_TYPE)
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Map.class)
            .returnResult()
            .getResponseBody();

        assertThat(auditTrailEntries).isNotNull();
        assertThat(auditTrailEntries).isNotEmpty();

        Map<String, Object> delegationEntry = auditTrailEntries.get(0);

        // Assertions on audit trail entry
        assertThat(delegationEntry.get("actionType")).isEqualTo(DELEGATION_ACTION_TYPE);
        assertThat(delegationEntry.get("delegatedUserId")).isEqualTo(DELEGATED_USER.getId());
        assertThat(delegationEntry.get("delegatedUsername")).isEqualTo(DELEGATED_USER.getUsername());

        String timestampStr = (String) delegationEntry.get("timestamp");
        assertThat(timestampStr).isNotNull();
        Instant timestamp = Instant.parse(timestampStr);
        assertThat(timestamp).isBefore(Instant.now().plusSeconds(5));

        Boolean immutable = (Boolean) delegationEntry.get("immutable");
        assertThat(immutable).isTrue();

        // Step 4: Verify immutability by attempting to modify audit trail entry (simulate API call)
        webTestClient.put()
            .uri("/api/audit-trail/" + delegationEntry.get("requestId"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(Map.of("actionType", "modified")))
            .exchange()
            .expectStatus().is4xxClientError(); // Expect 4xx error as audit trail is immutable
    }
}
