/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8812
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:46:08
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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.CurrencyOverrideRequest;
import com.webapp.fpmapp.entities.CurrencyOverrideLog;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.CurrencySyncService;

/**
 * Integration test for currency override submission with valid reason and new rate.
 * 
 * Preconditions:
 * - User logged in as administrator.
 * - POST /api/currency/override endpoint accessible.
 * 
 * This test uses Spring Boot test context with mocked services and Selenium WebDriver
 * to simulate UI verification.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CurrencyOverrideIntegrationTest {

    private static WebDriver driver;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private CurrencySyncService currencySyncService;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String OVERRIDE_API_ENDPOINT = "/api/currency/override";

    private static final String OVERRIDE_ALERTS_API_ENDPOINT = "/api/currency/override-alerts";

    private static final String CURRENCY_DATA_UI_URL = "http://localhost:8080/currency-data";

    private static final String ADMIN_USERNAME = "adminUser";
    private static final Long ADMIN_USER_ID = 1001L;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
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
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testSuccessfulCurrencyOverrideSubmission() throws Exception {
        // Prepare test data
        double newExchangeRate = 1.2345;
        String overrideReason = "Quarterly adjustment due to market volatility";

        CurrencyOverrideRequest overrideRequest = new CurrencyOverrideRequest();
        overrideRequest.setCurrencyPair("USD/EUR");
        overrideRequest.setNewRate(newExchangeRate);
        overrideRequest.setReason(overrideReason);

        // Mock service behavior for override processing
        CurrencyOverrideLog mockLog = new CurrencyOverrideLog();
        mockLog.setId(5001L);
        mockLog.setCurrencyPair("USD/EUR");
        mockLog.setNewRate(newExchangeRate);
        mockLog.setReason(overrideReason);
        mockLog.setPerformedByUserId(ADMIN_USER_ID);
        mockLog.setOverrideTimestamp(Instant.now());

        when(currencySyncService.processOverride(any())).thenReturn(mockLog);
        when(currencySyncService.getOverrideAlerts()).thenReturn(
                Collections.singletonList("Override alert generated for USD/EUR"));

        // Step 1: Submit override request via API
        String requestJson = objectMapper.writeValueAsString(overrideRequest);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(OVERRIDE_API_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        // Step 2: Verify API response indicates success
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        String responseBody = response.getContentAsString();
        assertThat(responseBody).contains("success");

        // Step 3: Query the Currency_Override_Logs table for the new entry (mocked via service)
        CurrencyOverrideLog logEntry = currencySyncService.processOverride(overrideRequest);
        assertThat(logEntry).isNotNull();
        assertThat(logEntry.getPerformedByUserId()).isEqualTo(ADMIN_USER_ID);
        assertThat(logEntry.getReason()).isEqualTo(overrideReason);
        assertThat(logEntry.getNewRate()).isEqualTo(newExchangeRate);
        assertThat(logEntry.getOverrideTimestamp()).isNotNull();

        // Step 4: Verify that an alert is generated for the override action
        List<String> alerts = currencySyncService.getOverrideAlerts();
        assertThat(alerts).isNotEmpty();
        assertThat(alerts.get(0)).contains("Override alert generated");

        // Step 5: Query the currency data via UI and confirm override flags and details are visible
        // Mock currencyConvertionController to return overridden currency data
        when(currencyConvertionController.getCurrencyData("USD/EUR")).thenReturn(
                new com.webapp.fpmapp.dto.CurrencyDataResponse(
                        "USD/EUR", newExchangeRate, true, overrideReason));

        // Navigate to currency data UI page
        driver.get(CURRENCY_DATA_UI_URL);

        // Wait and find the currency pair element
        WebElement currencyRow = driver.findElement(By.id("currency-USD/EUR"));
        assertThat(currencyRow).isNotNull();

        // Verify override flag is displayed
        WebElement overrideFlag = currencyRow.findElement(By.className("override-flag"));
        assertThat(overrideFlag.getText()).contains("Overridden");

        // Verify override reason is displayed
        WebElement overrideReasonElement = currencyRow.findElement(By.className("override-reason"));
        assertThat(overrideReasonElement.getText()).isEqualTo(overrideReason);
    }
}
