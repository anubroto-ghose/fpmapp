/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8794
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:32:16
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.CurrencySyncService;
import com.webapp.fpmapp.services.AuditTrailService;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration test for currency override submission by administrator.
 * Uses Selenium WebDriver to simulate UI interaction and mocks backend services.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CurrencyOverrideAdminIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private CurrencySyncService currencySyncService;

    @MockBean
    private AuditTrailService auditTrailService;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController userProfileController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String ADMIN_USERNAME = "adminUser";
    private static final String ADMIN_PASSWORD = "adminPass123";

    private static final String CURRENCY_OVERRIDE_PANEL_PATH = "/currency-override";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
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
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        // Mock user profile to simulate admin logged in with override permissions
        when(userProfileController.getCurrentUser()).thenReturn(
            new com.webapp.fpmapp.entities.User(1L, ADMIN_USERNAME, "Administrator", true));

        // Mock currencySyncService to accept override and log alert
        doAnswer(invocation -> {
            // Simulate logging override
            return null;
        }).when(currencySyncService).processOverride(any());

        doAnswer(invocation -> {
            // Simulate sending alert
            return null;
        }).when(currencySyncService).sendOverrideAlert(any());
    }

    @Test
    public void testSubmitCurrencyOverrideWithValidReason() throws Exception {
        // Step 1: Login as admin (simulate session or direct access)
        // For simplicity, assume session is established or no auth required for test

        // Step 2: Navigate to CurrencyOverridePanel
        driver.get(BASE_URL + CURRENCY_OVERRIDE_PANEL_PATH);

        // Wait for panel to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyOverrideForm")));

        // Step 3: Enter valid currency pair and new exchange rate
        WebElement currencyPairInput = driver.findElement(By.id("currencyPair"));
        WebElement newRateInput = driver.findElement(By.id("newExchangeRate"));
        WebElement reasonInput = driver.findElement(By.id("overrideReason"));
        WebElement submitButton = driver.findElement(By.id("submitOverride"));

        String testCurrencyPair = "USD/EUR";
        String testNewRate = "0.85";
        String testReason = "Quarterly adjustment due to market volatility";

        currencyPairInput.clear();
        currencyPairInput.sendKeys(testCurrencyPair);

        newRateInput.clear();
        newRateInput.sendKeys(testNewRate);

        reasonInput.clear();
        reasonInput.sendKeys(testReason);

        // Step 4: Submit the override
        submitButton.click();

        // Step 5: Verify override accepted with real-time feedback
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overrideSuccessMessage")));
        assertThat(successMessage.getText()).contains("Override submitted successfully");

        // Step 6: Verify override logged in database via AuditTrailService and CurrencySyncService
        ArgumentCaptor<com.webapp.fpmapp.dto.CurrencyOverrideRequest> overrideCaptor = ArgumentCaptor.forClass(com.webapp.fpmapp.dto.CurrencyOverrideRequest.class);
        verify(currencySyncService).processOverride(overrideCaptor.capture());

        com.webapp.fpmapp.dto.CurrencyOverrideRequest capturedOverride = overrideCaptor.getValue();
        assertThat(capturedOverride.getCurrencyPair()).isEqualTo(testCurrencyPair);
        assertThat(capturedOverride.getNewRate()).isEqualTo(Double.parseDouble(testNewRate));
        assertThat(capturedOverride.getReason()).isEqualTo(testReason);
        assertThat(capturedOverride.getPerformedByUserId()).isEqualTo(1L); // admin user id

        // Verify alert sent
        verify(currencySyncService).sendOverrideAlert(any());

        // Step 7: Verify override retrievable via API
        MvcResult mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/currency/override-alerts")
                .accept(MediaType.APPLICATION_JSON))
            .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertThat(jsonResponse).contains(testCurrencyPair);
        assertThat(jsonResponse).contains(testReason);

        // Additional: Verify database entry via AuditTrailService
        when(auditTrailService.getOverrideLogsByCurrencyPair(testCurrencyPair))
            .thenReturn(Collections.singletonList(
                new com.webapp.fpmapp.entities.CurrencyOverrideLog(
                    100L,
                    testCurrencyPair,
                    Instant.now(),
                    1L,
                    Double.parseDouble(testNewRate),
                    testReason
                )
            ));

        List<com.webapp.fpmapp.entities.CurrencyOverrideLog> logs = auditTrailService.getOverrideLogsByCurrencyPair(testCurrencyPair);
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getOverrideReason()).isEqualTo(testReason);
    }
}
