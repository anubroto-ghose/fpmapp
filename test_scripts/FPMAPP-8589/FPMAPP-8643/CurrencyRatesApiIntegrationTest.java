/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8643
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:44:22
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.CurrencyRateDTO;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Integration test for Currency Rates API with Selenium WebDriver and Spring Boot context.
 * 
 * Validates current and historical currency rates fetching with admin override info.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class CurrencyRatesApiIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private String baseUrl;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
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
        baseUrl = "http://localhost:" + port + "/fpm/currency/rates";
    }

    /**
     * Test fetching current currency rates without timestamp parameter.
     * Verifies response includes current rates with override flags and metadata.
     */
    @Test
    public void testFetchCurrentRates() throws Exception {
        // Prepare mock data
        CurrencyRateDTO usdRate = new CurrencyRateDTO();
        usdRate.setCurrencyCode("USD");
        usdRate.setRate(1.0);
        usdRate.setIsAdminOverride(false);
        usdRate.setOverrideReason(null);
        usdRate.setOverrideTimestamp(null);
        usdRate.setOverrideUserId(null);

        CurrencyRateDTO eurRate = new CurrencyRateDTO();
        eurRate.setCurrencyCode("EUR");
        eurRate.setRate(0.85);
        eurRate.setIsAdminOverride(true);
        eurRate.setOverrideReason("Manual correction due to market volatility");
        eurRate.setOverrideTimestamp(LocalDateTime.of(2024, 6, 1, 10, 0));
        eurRate.setOverrideUserId("admin123");

        List<CurrencyRateDTO> currentRates = Arrays.asList(usdRate, eurRate);

        when(currencyConvertionController.getCurrencyRates(null)).thenReturn(currentRates);

        // Navigate to API endpoint
        driver.get(baseUrl);

        // Wait and parse JSON response
        String pageSource = driver.findElement(By.tagName("pre")).getText();

        CurrencyRateDTO[] responseRates = objectMapper.readValue(pageSource, CurrencyRateDTO[].class);

        assertThat(responseRates).isNotNull();
        assertThat(responseRates.length).isEqualTo(2);

        // Validate USD rate
        CurrencyRateDTO usdResponse = Arrays.stream(responseRates)
                .filter(r -> "USD".equals(r.getCurrencyCode()))
                .findFirst()
                .orElse(null);
        assertThat(usdResponse).isNotNull();
        assertThat(usdResponse.getRate()).isEqualTo(1.0);
        assertThat(usdResponse.getIsAdminOverride()).isFalse();
        assertThat(usdResponse.getOverrideReason()).isNull();

        // Validate EUR rate with override
        CurrencyRateDTO eurResponse = Arrays.stream(responseRates)
                .filter(r -> "EUR".equals(r.getCurrencyCode()))
                .findFirst()
                .orElse(null);
        assertThat(eurResponse).isNotNull();
        assertThat(eurResponse.getRate()).isEqualTo(0.85);
        assertThat(eurResponse.getIsAdminOverride()).isTrue();
        assertThat(eurResponse.getOverrideReason()).isEqualTo("Manual correction due to market volatility");
        assertThat(eurResponse.getOverrideTimestamp()).isEqualTo(LocalDateTime.of(2024, 6, 1, 10, 0));
        assertThat(eurResponse.getOverrideUserId()).isEqualTo("admin123");
    }

    /**
     * Test fetching historical currency rates with a valid timestamp parameter.
     * Verifies response returns rates as of that timestamp including override info.
     */
    @Test
    public void testFetchHistoricalRates() throws Exception {
        String historicalTimestamp = "2024-05-01T00:00:00Z";

        CurrencyRateDTO usdRateHist = new CurrencyRateDTO();
        usdRateHist.setCurrencyCode("USD");
        usdRateHist.setRate(1.0);
        usdRateHist.setIsAdminOverride(false);
        usdRateHist.setOverrideReason(null);
        usdRateHist.setOverrideTimestamp(null);
        usdRateHist.setOverrideUserId(null);

        CurrencyRateDTO eurRateHist = new CurrencyRateDTO();
        eurRateHist.setCurrencyCode("EUR");
        eurRateHist.setRate(0.83);
        eurRateHist.setIsAdminOverride(true);
        eurRateHist.setOverrideReason("Historical manual override");
        eurRateHist.setOverrideTimestamp(LocalDateTime.of(2024, 4, 30, 15, 30));
        eurRateHist.setOverrideUserId("admin456");

        List<CurrencyRateDTO> historicalRates = Arrays.asList(usdRateHist, eurRateHist);

        when(currencyConvertionController.getCurrencyRates(eq(historicalTimestamp))).thenReturn(historicalRates);

        // Navigate to API endpoint with timestamp param
        driver.get(baseUrl + "?timestamp=" + historicalTimestamp);

        String pageSource = driver.findElement(By.tagName("pre")).getText();

        CurrencyRateDTO[] responseRates = objectMapper.readValue(pageSource, CurrencyRateDTO[].class);

        assertThat(responseRates).isNotNull();
        assertThat(responseRates.length).isEqualTo(2);

        // Validate USD historical rate
        CurrencyRateDTO usdResponse = Arrays.stream(responseRates)
                .filter(r -> "USD".equals(r.getCurrencyCode()))
                .findFirst()
                .orElse(null);
        assertThat(usdResponse).isNotNull();
        assertThat(usdResponse.getRate()).isEqualTo(1.0);
        assertThat(usdResponse.getIsAdminOverride()).isFalse();

        // Validate EUR historical rate with override
        CurrencyRateDTO eurResponse = Arrays.stream(responseRates)
                .filter(r -> "EUR".equals(r.getCurrencyCode()))
                .findFirst()
                .orElse(null);
        assertThat(eurResponse).isNotNull();
        assertThat(eurResponse.getRate()).isEqualTo(0.83);
        assertThat(eurResponse.getIsAdminOverride()).isTrue();
        assertThat(eurResponse.getOverrideReason()).isEqualTo("Historical manual override");
        assertThat(eurResponse.getOverrideTimestamp()).isEqualTo(LocalDateTime.of(2024, 4, 30, 15, 30));
        assertThat(eurResponse.getOverrideUserId()).isEqualTo("admin456");
    }

    /**
     * Test fetching currency rates with an invalid or future timestamp.
     * Verifies API returns appropriate error or empty response.
     */
    @Test
    public void testFetchRatesWithInvalidTimestamp() throws Exception {
        String invalidTimestamp = "2999-01-01T00:00:00Z"; // Future date

        when(currencyConvertionController.getCurrencyRates(eq(invalidTimestamp)))
                .thenThrow(new IllegalArgumentException("Timestamp is in the future or invalid"));

        // Navigate to API endpoint with invalid timestamp param
        driver.get(baseUrl + "?timestamp=" + invalidTimestamp);

        // The API is expected to return an error JSON or empty response
        WebElement preElement = driver.findElement(By.tagName("pre"));
        String responseText = preElement.getText();

        // Try parse error message
        Map<String, Object> errorResponse = null;
        try {
            errorResponse = objectMapper.readValue(responseText, Map.class);
        } catch (Exception e) {
            // Not JSON, treat as empty or invalid
        }

        if (errorResponse != null) {
            assertThat(errorResponse).containsKey("error");
            assertThat(errorResponse.get("error").toString().toLowerCase()).contains("timestamp");
        } else {
            // If empty response, assert empty
            assertThat(responseText.trim()).isEmpty();
        }
    }
}
