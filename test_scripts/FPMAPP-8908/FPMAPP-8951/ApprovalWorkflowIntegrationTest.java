/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8951
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:39:46
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.repositories.ApprovalRequestRepository;
import com.webapp.fpmapp.repositories.UserRepository;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import org.springframework.boot.web.server.LocalServerPort;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.ApprovalRequestDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class ApprovalWorkflowIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private WebTestClient webTestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_URL = "http://localhost:";

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
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock external service responses
        when(currencyConvertionController.convertCurrency("USD", "EUR", 100.0)).thenReturn(85.0);
        when(fpmForecastController.getForecastData()).thenReturn("Sample Forecast Data");
        when(fpmCommonController.getCommonData()).thenReturn("Common Data");

        // Prepare test data in DB
        approvalRequestRepository.deleteAll();
        userRepository.deleteAll();

        // Create a user with approver role
        User approver = new User();
        approver.setId(1L);
        approver.setUsername("approverUser");
        approver.setRole("ROLE_APPROVER_LEVEL_1");
        userRepository.save(approver);

        // Create a pending approval request
        com.webapp.fpmapp.entities.ApprovalRequest approvalRequest = new com.webapp.fpmapp.entities.ApprovalRequest();
        approvalRequest.setId(100L);
        approvalRequest.setStatus("Pending");
        approvalRequest.setCurrentApproverRole("ROLE_APPROVER_LEVEL_1");
        approvalRequestRepository.save(approvalRequest);
    }

    @Test
    public void testApprovalStatusAndApproverRoleUpdate() throws Exception {
        // Step 1: Approve the request using authorized approver via UI
        String approvalUrl = BASE_URL + port + "/approval/requests/100";
        driver.get(approvalUrl);

        // Wait for page to load and button to be clickable
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("approveBtn")));

        // Simulate login as approverUser (assuming login is required)
        // For simplicity, assume session is already authenticated or no auth required for test

        approveButton.click();

        // Wait for success message or status update
        WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatus")));
        String updatedStatus = statusElement.getText();
        assertThat(updatedStatus).isEqualToIgnoringCase("Approved");

        WebElement currentApproverRoleElement = driver.findElement(By.id("currentApproverRole"));
        String updatedApproverRole = currentApproverRoleElement.getText();
        assertThat(updatedApproverRole).isNotEmpty();

        // Step 2: Query the database to verify approval status and current approver role
        Optional<com.webapp.fpmapp.entities.ApprovalRequest> updatedRequestOpt = approvalRequestRepository.findById(100L);
        assertThat(updatedRequestOpt).isPresent();
        com.webapp.fpmapp.entities.ApprovalRequest updatedRequest = updatedRequestOpt.get();
        assertThat(updatedRequest.getStatus()).isEqualTo("Approved");
        assertThat(updatedRequest.getCurrentApproverRole()).isEqualTo(updatedApproverRole);

        // Step 3: Use approval workflow API to retrieve approval request details
        webTestClient.get()
            .uri("/api/approval/requests/100")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(response -> {
                try {
                    ApprovalRequestDTO dto = objectMapper.readValue(response.getResponseBody(), ApprovalRequestDTO.class);
                    assertThat(dto.getStatus()).isEqualTo("Approved");
                    assertThat(dto.getCurrentApproverRole()).isEqualTo(updatedApproverRole);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse API response", e);
                }
            });
    }
}
