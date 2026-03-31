/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-9053
 * Epic: FPMAPP-9032
 * Generated on: 2026-03-31 16:47:28
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration test for currency exchange rate synchronization.
 * 
 * Preconditions:
 * - Third-party currency exchange rate provider API is mocked and responsive.
 * - Synchronization job is triggered manually via REST API.
 * 
 * This test uses MockMvc to simulate HTTP requests and mocks the CurrencyConvertionController service.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyRateSyncIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private FpmCommonController fpmCommonController; // Assuming this has DB access methods

    private AutoCloseable mocks;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
    }

    /**
     * Test case: Successful synchronization of currency exchange rates.
     * 
     * Steps:
     * 1. Mock the third-party API response with realistic exchange rates.
     * 2. Trigger synchronization via POST /api/fpm/currency/rates/sync.
     * 3. Verify API response success.
     * 4. Verify database updated with latest rates.
     * 5. Verify historical rates stored with correct timestamps.
     * 6. Assert no errors/exceptions.
     * 
     * @throws Exception
     */
    @Test
    public void testSuccessfulCurrencyRateSynchronization() throws Exception {
        // Mock third-party API response
        Map<String, Double> mockRates = new HashMap<>();
        mockRates.put("USD", 1.0);
        mockRates.put("EUR", 0.85);
        mockRates.put("GBP", 0.75);
        mockRates.put("INR", 74.5);

        // Mock the service method that fetches rates from third-party
        when(currencyConvertionController.fetchLatestRates()).thenReturn(mockRates);

        // Trigger synchronization via REST API
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/fpm/currency/rates/sync")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = result.getResponse().getStatus();
        String content = result.getResponse().getContentAsString();

        // Assert HTTP 200 OK
        assertThat(status).isEqualTo(200);

        // Assert response contains success message
        assertThat(content).containsIgnoringCase("synchronization completed");

        // Verify database updated with latest rates
        // Assuming fpmCommonController has method getCurrentExchangeRate(String currencyCode)
        Double usdRate = fpmCommonController.getCurrentExchangeRate("USD");
        Double eurRate = fpmCommonController.getCurrentExchangeRate("EUR");
        Double gbpRate = fpmCommonController.getCurrentExchangeRate("GBP");
        Double inrRate = fpmCommonController.getCurrentExchangeRate("INR");

        assertThat(usdRate).isEqualTo(1.0);
        assertThat(eurRate).isEqualTo(0.85);
        assertThat(gbpRate).isEqualTo(0.75);
        assertThat(inrRate).isEqualTo(74.5);

        // Verify historical rates stored with correct timestamps
        // Assuming fpmCommonController has method getHistoricalRates(String currencyCode) returning Map<Instant, Double>
        Map<Instant, Double> eurHistory = fpmCommonController.getHistoricalRates("EUR");
        assertThat(eurHistory).isNotEmpty();

        // Check that the latest timestamp is recent (within last 5 minutes)
        Instant latestTimestamp = eurHistory.keySet().stream().max(Instant::compareTo).orElse(null);
        assertThat(latestTimestamp).isNotNull();
        Instant now = Instant.now();
        long secondsDiff = now.getEpochSecond() - latestTimestamp.getEpochSecond();
        assertThat(secondsDiff).isLessThanOrEqualTo(300); // 5 minutes

        // Check the rate at latest timestamp matches mocked rate
        Double latestRate = eurHistory.get(latestTimestamp);
        assertThat(latestRate).isEqualTo(0.85);
    }
}
