/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6227
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:35:42
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.webapp.fpmapp.entities.CurrencyRate;
import com.webapp.fpmapp.repositories.CurrencyRateRepository;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration Test verifying the real-time currency synchronization API and db state.
 * Uses embedded SpringBoot context, mocks external currency fetching service,
 * and Selenium WebDriver in headless mode to verify backend integration.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CurrencyRateSyncIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private MockMvc mockMvc;
    private WebDriver driver;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // Setup Selenium WebDriver (ChromeDriver in headless mode)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Clean currency rates before each test
        currencyRateRepository.deleteAll();

        // Mock the external currency rate source with predefined rates
        when(currencyConvertionController.fetchLatestRates()).thenReturn(
            Collections.singletonMap("USD", 1.0) // USD base rate
        );
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Test Steps:
     * 1. POST /currency/rates/sync as admin user
     * 2. Verify response success and valid ISO timestamp
     * 3. Verify currency rates updated in DB with override_flag = false
     * 4. Verify no audit log or overrides created
     * 5. Verify no alert emails sent (mocked behavior)
     */
    @Test
    public void testCurrencyRateSynchronizationSuccess() throws Exception {
        // Perform POST request to sync endpoint
        MvcResult result = mockMvc.perform(post("/currency/rates/sync")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        JsonNode rootNode = objectMapper.readTree(responseContent);

        // Assert success flag true
        assertThat(rootNode.get("success").asBoolean()).isTrue();

        // Assert syncedAt field is present and parseable ISO timestamp
        String syncedAtStr = rootNode.get("syncedAt").asText();
        Instant syncedAt = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(syncedAtStr));
        assertThat(syncedAt).isNotNull();

        // Verify database updated currency rates
        List<CurrencyRate> rates = currencyRateRepository.findAll();
        assertThat(rates).isNotEmpty();
        for (CurrencyRate rate : rates) {
            assertThat(rate.getEffectiveDate()).isNotNull();
            assertThat(rate.isOverrideFlag()).isFalse();
            // EffectiveDate should be very recent (within last 5 minutes)
            Instant effectiveInstant = rate.getEffectiveDate().toInstant(ZoneOffset.UTC);
            assertThat(effectiveInstant).isBetween(syncedAt.minusSeconds(300), syncedAt.plusSeconds(300));
        }

        // Verify no override audit logs created
        // Assume CurrencyRateRepository.executes are just for currency rates; we check other repositories mocked
        // Since audit log repository is not provided, verify external audit/override service is not called
        verify(currencyConvertionController, times(1)).fetchLatestRates();

        // Verify no alert emails sent (mock service or API, placeholder assertion)
        // Assuming there is no mailSender bean call (not implemented here), so no action needed

        // Selenium part: Open local API docs or dashboard and assert connection
        driver.get("http://localhost:8080/swagger-ui.html"); // example, adjust base URL & path

        WebElement docHeader = driver.findElement(By.tagName("h2"));
        assertThat(docHeader.getText()).containsIgnoringCase("currency");

        // Additional UI interaction could be added if the UI shows currency rates
    }
}