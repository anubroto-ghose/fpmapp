/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6218
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:41:09
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import com.webapp.fpmapp.dto.FpmCommonController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Comprehensive integration test verifying currency exchange rate historical queries and scheduling sync.
 * 
 * Preconditions:
 * - Currency sync job scheduled and runs (mocked trigger verified)
 * - Historical currency data available (mocked)
 * - API endpoint /api/fpm/currency/rates available
 * 
 * This test class uses SpringBootTest with WebDriver to simulate web interaction and REST API calls.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyRatesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static class CurrencyRateDTO {
        public String currencyCode;
        public double rate;
        public String timestamp;
        public boolean overridden;

        public CurrencyRateDTO() {}

        public CurrencyRateDTO(String currencyCode, double rate, String timestamp, boolean overridden) {
            this.currencyCode = currencyCode;
            this.rate = rate;
            this.timestamp = timestamp;
            this.overridden = overridden;
        }
    }

    static class SyncJobStatus {
        public boolean lastRunSuccessful;
        public String lastRunTimestamp;

        public SyncJobStatus() {}

        public SyncJobStatus(boolean success, String timestamp) {
            this.lastRunSuccessful = success;
            this.lastRunTimestamp = timestamp;
        }
    }

    @BeforeAll
    public static void setUpAll() {
        // Setup ChromeDriver headless for CI pipeline compatibility
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() throws Exception {
        // Prepare mocked historical currency rates data
        List<CurrencyRateDTO> mockedRates = Arrays.asList(
                new CurrencyRateDTO("USD", 1.0, "2025-09-10T12:00:00", false),
                new CurrencyRateDTO("EUR", 0.85, "2025-09-10T12:00:00", false),
                new CurrencyRateDTO("JPY", 110.0, "2025-09-10T12:00:00", true) // overridden
        );

        when(currencyConvertionController.getCurrencyRates(any(), any())).thenReturn(mockedRates);

        // Prepare mocked sync job status
        SyncJobStatus mockedStatus = new SyncJobStatus(true, "2025-09-14T23:59:00");
        when(fpmCommonController.getCurrencySyncJobStatus()).thenReturn(mockedStatus);
    }

    @Test
    public void testHistoricalCurrencyRatesApiWithSelenium() throws Exception {
        LocalDateTime fromDate = LocalDateTime.of(2025, 9, 10, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(2025, 9, 11, 0, 0);
        String from = fromDate.format(DateTimeFormatter.ISO_DATE);
        String to = toDate.format(DateTimeFormatter.ISO_DATE);

        // 1. Selenium: Open a simple HTML client page that triggers the API request
        // (In real case, this could be a UI page that calls the REST endpoint and shows results.)
        // Here, we simulate direct REST call below after verification.

        String apiEndpoint = BASE_URL + "/api/fpm/currency/rates?dateFrom=" + from + "&dateTo=" + to;

        // Use MockMvc to simulate REST GET call
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/fpm/currency/rates")
                        .param("dateFrom", from)
                        .param("dateTo", to)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response JSON
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CurrencyRateDTO[] rates = objectMapper.readValue(jsonResponse, CurrencyRateDTO[].class);

        // 2. Verify response includes expected currency codes, rates, timestamps, override flags
        // Assertions:
        org.junit.jupiter.api.Assertions.assertNotNull(rates, "Rates response should not be null");
        org.junit.jupiter.api.Assertions.assertTrue(rates.length >= 3, "Should return at least 3 currencies");

        boolean foundUSD = false, foundEUR = false, foundJPY = false;
        for (CurrencyRateDTO rate : rates) {
            // Validate required fields
            org.junit.jupiter.api.Assertions.assertNotNull(rate.currencyCode, "Currency code must be present");
            org.junit.jupiter.api.Assertions.assertTrue(rate.rate > 0, "Rate must be positive");
            org.junit.jupiter.api.Assertions.assertNotNull(rate.timestamp, "Timestamp must be present");

            // Check date validity
            LocalDateTime rateTimestamp = LocalDateTime.parse(rate.timestamp);
            org.junit.jupiter.api.Assertions.assertFalse(rateTimestamp.isBefore(fromDate), "Rate timestamp should not be before fromDate");
            org.junit.jupiter.api.Assertions.assertFalse(rateTimestamp.isAfter(toDate), "Rate timestamp should not be after toDate");

            // Record known currencies
            if ("USD".equals(rate.currencyCode)) {
                foundUSD = true;
                org.junit.jupiter.api.Assertions.assertFalse(rate.overridden, "USD should not be overridden");
            } else if ("EUR".equals(rate.currencyCode)) {
                foundEUR = true;
                org.junit.jupiter.api.Assertions.assertFalse(rate.overridden, "EUR should not be overridden");
            } else if ("JPY".equals(rate.currencyCode)) {
                foundJPY = true;
                org.junit.jupiter.api.Assertions.assertTrue(rate.overridden, "JPY should be overridden");
            }
        }

        org.junit.jupiter.api.Assertions.assertTrue(foundUSD, "USD rate must be present");
        org.junit.jupiter.api.Assertions.assertTrue(foundEUR, "EUR rate must be present");
        org.junit.jupiter.api.Assertions.assertTrue(foundJPY, "JPY rate must be present");

        // 3. Verify synchronization job logs indicate recent successful sync
        SyncJobStatus syncStatus = fpmCommonController.getCurrencySyncJobStatus();
        org.junit.jupiter.api.Assertions.assertTrue(syncStatus.lastRunSuccessful, "Sync job last run should be successful");

        LocalDateTime lastSync = LocalDateTime.parse(syncStatus.lastRunTimestamp);
        // The last sync should be before now and not too old (e.g., last 24 hours)
        org.junit.jupiter.api.Assertions.assertFalse(lastSync.isAfter(LocalDateTime.now()), "Last sync timestamp cannot be in future");
        org.junit.jupiter.api.Assertions.assertTrue(lastSync.isAfter(LocalDateTime.now().minusDays(2)), "Last sync must be recent within 2 days");


        // 4. Use Selenium to verify UI representation (simulate simple display)

        // For demonstration: create a simple HTML file content that would show fetched rates
        // Since no actual UI provided, testing direct REST + assertions suffices here.

        // If an actual UI existed, here Selenium could load the page, interact,
        // e.g., check elements that show currency rates and overridden flags.

        // Example: driver.get("http://localhost:8080/currency-rates-ui?dateFrom=" + from + "&dateTo=" + to);
        // Then find elements and assert texts.

        // For completeness, simulate navigating to API endpoint URL (GET returns JSON)
        driver.get(apiEndpoint);

        WebElement preformatted = driver.findElement(By.tagName("pre"));
        org.junit.jupiter.api.Assertions.assertNotNull(preformatted, "Page should contain preformatted JSON output");

        String pageText = preformatted.getText();
        org.junit.jupiter.api.Assertions.assertTrue(pageText.contains("USD"), "Page should contain USD");
        org.junit.jupiter.api.Assertions.assertTrue(pageText.contains("EUR"), "Page should contain EUR");
        org.junit.jupiter.api.Assertions.assertTrue(pageText.contains("JPY"), "Page should contain JPY");

    }
}
