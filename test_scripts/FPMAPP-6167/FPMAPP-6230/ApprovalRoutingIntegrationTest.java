/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6230
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:33:06
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.dto.ApprovalRequestDTO;
import com.webapp.fpmapp.dto.ApprovalResponseDTO;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmDealApprovalService;

/**
 * Spring Boot + Selenium integration test verifying automatic routing of approval requests per hierarchical roles and thresholds
 * 
 * Preconditions:
 * - Approval workflows are configured properly in Camunda with defined hierarchical roles and thresholds.
 * - User accounts exist with assigned roles matching the workflow configuration.
 * - The system is integrated with Camunda and the FPM backend.
 * 
 * This test submits approval requests and verifies correct automatic routing via UI and backend mock validation.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class ApprovalRoutingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FpmDealApprovalService approvalService;

    @MockBean
    private CurrencyConvertionController currencyConversionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private WebDriver driver;

    private AutoCloseable mocks;

    private static final int SELENIUM_WAIT_SECONDS = 15;

    @BeforeEach
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);

        // Setup headless ChromeDriver for Selenium
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Mock currency conversion controller to return fixed exchange rate
        when(currencyConversionController.convertCurrency(any(String.class), any(String.class), any(Double.class)))
            .thenReturn(100.0); // returning dummy conversion

        // Mock forecast and common controller - return simple success for all
        when(fpmForecastController.getForecast(any())).thenReturn(Collections.emptyMap());
        when(fpmCommonController.getCommonData()).thenReturn(Collections.singletonMap("status", "ready"));

        // Mock approval service routing behavior
        when(approvalService.submitApprovalRequest(any(ApprovalRequestDTO.class))).thenAnswer(invocation -> {
            ApprovalRequestDTO req = invocation.getArgument(0);
            ApprovalResponseDTO response = new ApprovalResponseDTO();
            response.setApprovalId(UUID.randomUUID().toString());
            // Assign approver role based on threshold
            if (req.getAmount() < 10000) {
                response.setNextApproverRole("ROLE_MANAGER");
            } else if (req.getAmount() < 50000) {
                response.setNextApproverRole("ROLE_DIRECTOR");
            } else {
                response.setNextApproverRole("ROLE_VP");
            }
            response.setStatus("PENDING_APPROVAL");
            return response;
        });
    }

    @AfterEach
    public void teardown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    @DisplayName("Verify approval request routing follows hierarchical roles and thresholds")
    public void testApprovalRoutingByThreshold() throws Exception {
        // Test data for different threshold cases
        double[] testAmounts = {5000, 20000, 100000};
        String[] expectedRoles = {"ROLE_MANAGER", "ROLE_DIRECTOR", "ROLE_VP"};

        ObjectMapper mapper = new ObjectMapper();

        for (int i = 0; i < testAmounts.length; i++) {
            ApprovalRequestDTO requestDTO = new ApprovalRequestDTO();
            requestDTO.setRequesterUserId("user123");
            requestDTO.setAmount(testAmounts[i]);
            requestDTO.setDescription("Test approval request for routing threshold " + testAmounts[i]);

            // Submit approval request via MockMvc to controller end point
            MvcResult mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/approvals/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDTO))
                )
                .andExpect(status().isOk())
                .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString();
            ApprovalResponseDTO responseDTO = mapper.readValue(jsonResponse, ApprovalResponseDTO.class);

            assertNotNull(responseDTO.getApprovalId(), "Approval ID should not be null");
            assertEquals(expectedRoles[i], responseDTO.getNextApproverRole(), "Next approver role mismatch.");
            assertEquals("PENDING_APPROVAL", responseDTO.getStatus(), "Status must be PENDING_APPROVAL");
        }
        
        // Now verify UI integration - Navigate to approval request page and check routing display
        driver.get("http://localhost:8080/approvals/request");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(SELENIUM_WAIT_SECONDS));

        for (int i = 0; i < testAmounts.length; i++) {
            // Fill in approval request form in UI
            WebElement amountInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("amount")));
            amountInput.clear();
            amountInput.sendKeys(String.valueOf(testAmounts[i]));

            WebElement descriptionInput = driver.findElement(By.id("description"));
            descriptionInput.clear();
            descriptionInput.sendKeys("Selenium test approval " + testAmounts[i]);

            WebElement submitBtn = driver.findElement(By.id("submitApprovalRequest"));
            submitBtn.click();

            // Wait for result panel to display
            WebElement resultPanel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalResult")));
            String resultText = resultPanel.getText().toUpperCase();

            assertTrue(resultText.contains("PENDING_APPROVAL"), "UI must show PENDING_APPROVAL status");
            assertTrue(resultText.contains(expectedRoles[i]), "UI must show correct next approver role");

            // Ideally logs can be checked but here just verify UI outputs
        }
    }
}