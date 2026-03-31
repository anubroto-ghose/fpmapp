/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8971
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:32:55
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.server.LocalManagementPort;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UnauthorizedApprovalTest {

    private static WebDriver driver;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "http://localhost:";

    private static final String APPROVAL_REQUEST_ID = "1001";

    private static final String UNAUTHORIZED_USERNAME = "unauthorizedUser";

    private static final String APPROVAL_API_ENDPOINT = "/api/approval-requests/" + APPROVAL_REQUEST_ID;

    private static final String APPROVE_ACTION_ENDPOINT = "/api/approval-requests/" + APPROVAL_REQUEST_ID + "/approve";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() throws Exception {
        // Mock the approval request data as pending approval
        ApprovalRequestDto pendingApproval = new ApprovalRequestDto();
        pendingApproval.setId(Long.parseLong(APPROVAL_REQUEST_ID));
        pendingApproval.setStatus("PENDING");
        pendingApproval.setCurrentApproverRole("ROLE_APPROVER");

        // Mock the service to return the pending approval request
        when(fpmCommonController.getApprovalRequestById(anyLong())).thenReturn(pendingApproval);

        // Mock the service to reject approval attempt by unauthorized user
        when(fpmCommonController.approveRequest(anyLong(), org.mockito.ArgumentMatchers.anyString()))
                .thenThrow(new UnauthorizedApprovalException("User does not have approval rights"));
    }

    @Test
    public void testUnauthorizedUserCannotApproveRequest() throws Exception {
        // Step 1: Attempt to approve the request using unauthorized user via UI
        driver.get(BASE_URL + port + "/login");

        // Login as unauthorized user
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(UNAUTHORIZED_USERNAME);
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for redirect to dashboard or approval page
        Thread.sleep(1000);

        // Navigate to approval request page
        driver.get(BASE_URL + port + "/approval-requests/" + APPROVAL_REQUEST_ID);

        // Attempt to click approve button
        WebElement approveButton = driver.findElement(By.id("approveBtn"));
        approveButton.click();

        // Wait for response
        Thread.sleep(1000);

        // Verify error message displayed
        WebElement errorMsg = driver.findElement(By.id("errorMessage"));
        assertThat(errorMsg.getText()).contains("You do not have permission to approve this request");

        // Step 2: Query the database (simulate via API) to verify approval status and current approver role remain unchanged
        ResponseEntity<String> response = restTemplate.getForEntity(APPROVAL_API_ENDPOINT, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ApprovalRequestDto approvalRequest = objectMapper.readValue(response.getBody(), ApprovalRequestDto.class);
        assertThat(approvalRequest.getStatus()).isEqualTo("PENDING");
        assertThat(approvalRequest.getCurrentApproverRole()).isEqualTo("ROLE_APPROVER");

        // Step 3: Use approval workflow API to retrieve approval request details and verify no change
        // (Already done above)

        // Additional assertion: ensure no approval action was accepted
        // Attempt approval via REST API directly
        ResponseEntity<String> approveResponse = restTemplate.postForEntity(APPROVE_ACTION_ENDPOINT + "?user=" + UNAUTHORIZED_USERNAME, null, String.class);

        assertThat(approveResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(approveResponse.getBody()).contains("User does not have approval rights");
    }

    // DTO and Exception classes for mocking
    public static class ApprovalRequestDto {
        private Long id;
        private String status;
        private String currentApproverRole;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCurrentApproverRole() {
            return currentApproverRole;
        }

        public void setCurrentApproverRole(String currentApproverRole) {
            this.currentApproverRole = currentApproverRole;
        }
    }

    public static class UnauthorizedApprovalException extends RuntimeException {
        public UnauthorizedApprovalException(String message) {
            super(message);
        }
    }
}
