/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8816
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:48:51
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.CurrencyRateDTO;
import com.webapp.fpmapp.services.CurrencyConvertionController;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CurrencyHistoricalRatesIntegrationTest {

    private static WebDriver driver;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    /**
     * Helper method to generate mock historical currency rates for a date range
     */
    private List<CurrencyRateDTO> generateMockRates(LocalDate startDate, LocalDate endDate) {
        List<CurrencyRateDTO> rates = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            CurrencyRateDTO rate = new CurrencyRateDTO();
            rate.setExchangeRate(1.1 + current.getDayOfMonth() * 0.01); // realistic variation
            rate.setRateTimestamp(current.atStartOfDay());
            rate.setHistorical(true);
            rate.setOverrideFlag(false);
            rate.setCurrencyPair("USD/EUR");
            rates.add(rate);
            current = current.plusDays(1);
        }
        return rates;
    }

    @Test
    public void testGetHistoricalCurrencyRates_ValidDateRange() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31);

        List<CurrencyRateDTO> mockRates = generateMockRates(startDate, endDate);

        // Mock the service controller response
        when(currencyConvertionController.getHistoricalRates(eq(startDate), eq(endDate), any()))
                .thenReturn(mockRates);

        String url = String.format("/api/currency/historical?start_date=%s&end_date=%s", 
                startDate.format(DATE_FORMATTER), endDate.format(DATE_FORMATTER));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(HttpStatus.OK.value());

        String content = mvcResult.getResponse().getContentAsString();

        CurrencyRateDTO[] responseRates = objectMapper.readValue(content, CurrencyRateDTO[].class);

        assertThat(responseRates).isNotNull();
        assertThat(responseRates.length).isEqualTo(mockRates.size());

        // Validate each record
        for (CurrencyRateDTO rate : responseRates) {
            assertThat(rate.getExchangeRate()).isGreaterThan(0);
            assertThat(rate.getRateTimestamp()).isNotNull();
            assertThat(rate.isHistorical()).isTrue();
            assertThat(rate.isOverrideFlag()).isIn(true, false); // override flag can be true or false
            assertThat(rate.getCurrencyPair()).isEqualTo("USD/EUR");
        }

        // Selenium part: Navigate to a UI page that displays historical rates (assuming such page exists)
        // For demonstration, we simulate a UI page that calls the API and displays results
        driver.get(BASE_URL + "/currency/historical?start_date=2026-01-01&end_date=2026-01-31");

        // Wait and verify that the page loaded and contains expected data
        // This is a placeholder: actual selectors depend on UI implementation
        List<WebElement> rows = driver.findElements(By.cssSelector("table#historicalRates tbody tr"));
        assertThat(rows.size()).isEqualTo(mockRates.size());

        // Verify first row data
        WebElement firstRow = rows.get(0);
        String dateText = firstRow.findElement(By.cssSelector("td.date")).getText();
        String rateText = firstRow.findElement(By.cssSelector("td.rate")).getText();
        String historicalFlagText = firstRow.findElement(By.cssSelector("td.historical")).getText();

        assertThat(dateText).isEqualTo("2026-01-01");
        assertThat(Double.parseDouble(rateText)).isGreaterThan(0);
        assertThat(historicalFlagText.toLowerCase()).contains("true");
    }

    @Test
    public void testGetHistoricalCurrencyRates_InvalidDateRange() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 2, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 31); // invalid: start after end

        String url = String.format("/api/currency/historical?start_date=%s&end_date=%s", 
                startDate.format(DATE_FORMATTER), endDate.format(DATE_FORMATTER));

        // Mock controller to throw IllegalArgumentException or return 400
        when(currencyConvertionController.getHistoricalRates(eq(startDate), eq(endDate), any()))
                .thenThrow(new IllegalArgumentException("start_date must be before or equal to end_date"));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value());

        String content = mvcResult.getResponse().getContentAsString();
        assertThat(content).containsIgnoringCase("start_date must be before or equal to end_date");

        // Selenium part: Navigate to UI page with invalid date range and verify error message
        driver.get(BASE_URL + "/currency/historical?start_date=2026-02-01&end_date=2026-01-31");

        WebElement errorElement = driver.findElement(By.id("error-message"));
        assertThat(errorElement.getText()).containsIgnoringCase("Invalid date range");
    }
}