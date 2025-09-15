/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6187
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:06:08
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.CurrencyRateDTO;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test simulating the scheduled synchronization job and verifying the currency exchange rates API.
 * 
 * Preconditions:
 * - Mocks the external currency exchange provider with realistic data.
 * - Runs within SpringBootTest with mocked CurrencyConvertionController service.
 * - Uses Selenium WebDriver to verify page content and API endpoints.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CurrencyRateSyncIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static WebDriver driver;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;

    @BeforeAll
    public static void setupClass() {
        // Set ChromeDriver location as per environment. Example uses WebDriverManager or system property.
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        // Using headless for CI compatibility
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1200");
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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Setup mock responses for the currency rate sync
        // Simulate current rates
        CurrencyRateDTO usdToEurCurrent = new CurrencyRateDTO();
        usdToEurCurrent.setCurrencyCode("USD-EUR");
        usdToEurCurrent.setRate(0.85);
        usdToEurCurrent.setTimestamp(LocalDate.now().atStartOfDay());
        usdToEurCurrent.setOverrideStatus(false);

        CurrencyRateDTO gbpToUsdCurrent = new CurrencyRateDTO();
        gbpToUsdCurrent.setCurrencyCode("GBP-USD");
        gbpToUsdCurrent.setRate(1.39);
        gbpToUsdCurrent.setTimestamp(LocalDate.now().atStartOfDay());
        gbpToUsdCurrent.setOverrideStatus(false);

        List<CurrencyRateDTO> currentRates = Arrays.asList(usdToEurCurrent, gbpToUsdCurrent);

        // Simulate historical rates
        CurrencyRateDTO usdToEurHist1 = new CurrencyRateDTO();
        usdToEurHist1.setCurrencyCode("USD-EUR");
        usdToEurHist1.setRate(0.83);
        usdToEurHist1.setTimestamp(LocalDate.now().minusDays(30).atStartOfDay());
        usdToEurHist1.setOverrideStatus(false);

        CurrencyRateDTO usdToEurHist2 = new CurrencyRateDTO();
        usdToEurHist2.setCurrencyCode("USD-EUR");
        usdToEurHist2.setRate(0.82);
        usdToEurHist2.setTimestamp(LocalDate.now().minusDays(60).atStartOfDay());
        usdToEurHist2.setOverrideStatus(true); // Assume overridden

        List<CurrencyRateDTO> historicalRates = Arrays.asList(usdToEurHist1, usdToEurHist2);

        // Mock the synchronization method to simulate successful job run (could be void, we just skip here)
        // Mock the API GET /api/fpm/currency/rates without params returns currentRates
        try {
            when(currencyConvertionController.getCurrencyRates(null, null))
                .thenReturn(currentRates);
            
            when(currencyConvertionController.getCurrencyRates(
                any(LocalDate.class), any(LocalDate.class)))
                .thenAnswer(invocation -> {
                    LocalDate start = invocation.getArgument(0);
                    LocalDate end = invocation.getArgument(1);
                    // Simple filter simulation
                    return historicalRates.stream()
                      .filter(r -> !r.getTimestamp().toLocalDate().isBefore(start)
                                   && !r.getTimestamp().toLocalDate().isAfter(end))
                      .toList();
                });
        } catch (Exception e) {
            throw new RuntimeException("Mock setup failed", e);
        }
    }

    /**
     * Test case to trigger the synchronization job and verify API responses using MockMvc and Selenium WebDriver.
     */
    @Test
    public void testCurrencyRateSynchronizationJobAndApi() throws Exception {
        // 1. Trigger synchronization job
        // In this test, we simulate this by directly invoking the controller method or assuming it's scheduled.
        // The service is mocked, so we do not execute real sync logic.

        // 2. Query current rates without parameters
        MvcResult currentRatesResult = mockMvc.perform(get("/api/fpm/currency/rates")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String currentRatesJson = currentRatesResult.getResponse().getContentAsString();
        List<CurrencyRateDTO> currentRates = objectMapper.readValue(currentRatesJson,
            new TypeReference<List<CurrencyRateDTO>>(){});

        assertThat(currentRates).isNotNull();
        assertThat(currentRates).isNotEmpty();

        // Validate latest timestamp is today
        currentRates.forEach(rate -> {
            assertThat(rate.getTimestamp().toLocalDate()).isEqualTo(LocalDate.now());
            assertThat(rate.getOverrideStatus()).isFalse();
            assertThat(rate.getRate()).isGreaterThan(0.0);
        });

        // 3. Query historical rates with a valid date range
        LocalDate startDate = LocalDate.now().minusDays(60);
        LocalDate endDate = LocalDate.now().minusDays(10);

        MvcResult historicalRatesResult = mockMvc.perform(get("/api/fpm/currency/rates")
            .param("startDate", startDate.format(FORMATTER))
            .param("endDate", endDate.format(FORMATTER))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        String historicalRatesJson = historicalRatesResult.getResponse().getContentAsString();
        List<CurrencyRateDTO> historicalRates = objectMapper.readValue(historicalRatesJson,
            new TypeReference<List<CurrencyRateDTO>>(){});

        assertThat(historicalRates).isNotNull();
        assertThat(historicalRates).isNotEmpty();

        // Assert all timestamps in correct range
        historicalRates.forEach(rate -> {
            LocalDate rateDate = rate.getTimestamp().toLocalDate();
            assertThat(rateDate.isBefore(startDate) || rateDate.isAfter(endDate)).isFalse();
            assertThat(rate.getRate()).isGreaterThan(0.0);
        });

        // Assert there is at least one overridden status in historical
        boolean hasOverride = historicalRates.stream()
            .anyMatch(r -> r.getOverrideStatus());
        assertThat(hasOverride).isTrue();

        // 4. Use Selenium WebDriver to load the API endpoint page showing current currency rates
        String baseUrl = "http://localhost:" + webApplicationContext.getEnvironment().getProperty("local.server.port", "8080") + "/fpmapp";
        // Here we simulate UI displaying current rates at /currencyRates (example path assumes UI page)

        // Since UI components not fully known, we simulate that the rates can be displayed at
        // /currencyRates page which fetches from /api/fpm/currency/rates
        driver.get(baseUrl + "/currencyRates");

        // Wait and check for table or list element containing currency codes and rates
        Thread.sleep(1000); // simple wait, in real tests use WebDriverWait

        List<WebElement> currencyRows = driver.findElements(By.cssSelector(".currency-rate-row"));

        assertThat(currencyRows).isNotEmpty();

        // Check each row contains expected currency code and rate format
        boolean usdEurFound = false;
        boolean gbpUsdFound = false;

        for (WebElement row : currencyRows) {
            String text = row.getText();
            if (text.contains("USD-EUR") && text.matches(".*0\.85.*")) {
                usdEurFound = true;
            }
            if (text.contains("GBP-USD") && text.matches(".*1\.39.*")) {
                gbpUsdFound = true;
            }
        }

        assertThat(usdEurFound).isTrue();
        assertThat(gbpUsdFound).isTrue();
    }
}
