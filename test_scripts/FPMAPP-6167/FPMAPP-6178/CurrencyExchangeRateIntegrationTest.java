/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6178
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:13:24
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.dto.CurrencyRateResponse;

import org.springframework.http.ResponseEntity;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CurrencyExchangeRateIntegrationTest {

    private static WebDriver driver;

    @Autowired
    private CurrencyConvertionController currencyConvertionController;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setUp() {
        // Setup Chrome WebDriver headless for integration test
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1200");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Integration test fetching current currency exchange rates via API and verifying through Selenium WebDriver
     * Simulates UI call that fetches rates and displays.
     */
    @Test
    public void testFetchCurrentCurrencyExchangeRates() {
        // Arrange: We expect the API to be up and serve rates
        // Since CurrencyConvertionController is a controller, we test actual endpoint via WebDriver + HTTP call

        String apiEndpoint = BASE_URL + "/currency/current-rates";

        // Navigate driver to a test HTML page that calls the currency API and displays results
        // For demonstration, this file is assumed to be served by test server or mocked UI page
        // Since no UI page provided, test direct JSON API via HTTP (using controller mock below),
        // but Selenium is mainly for UI testing - so emulate through executing JavaScript fetch inside Selenium.

        // Load a blank data URI page for script execution
        driver.get("data:text/html;charset=utf-8,<html><head><title>Currency Rates Test</title></head><body><pre id='output'></pre><script>
" +
            "fetch('" + apiEndpoint + "').then(res => { if (!res.ok) throw new Error('HTTP error ' + res.status);
" +
            "return res.json()}).then(data => { document.getElementById('output').textContent = JSON.stringify(data); })
" +
            ".catch(e => { document.getElementById('output').textContent = 'Error: ' + e.message; });
" +
            "</script></body></html>");

        // Wait for the output to load with a simple polling
        // (In real test use WebDriverWait but here use simple sleep for brevity)
        try {
            Thread.sleep(2000); // Wait to allow fetch and display
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement outputPre = driver.findElement(By.id("output"));
        String content = outputPre.getText();

        // Assertions
        assertThat(content).doesNotContain("Error:");
        assertThat(content).isNotEmpty();

        // Parse the JSON response string via org.json
        org.json.JSONObject jsonResponse;
        try {
            jsonResponse = new org.json.JSONObject(content);
        } catch (org.json.JSONException e) {
            throw new AssertionError("Response is not valid JSON", e);
        }

        // Validate expected keys present
        // Example API schema assumed:
        // {
        //    "timestamp": "2025-09-15T05:00:00Z",
        //    "rates": {
        //       "USD_EUR": 0.85,
        //       "USD_GBP": 0.75,
        //       ...
        //    }
        // }

        assertThat(jsonResponse.has("timestamp")).isTrue();
        assertThat(jsonResponse.has("rates")).isTrue();

        String timestamp = jsonResponse.getString("timestamp");
        org.json.JSONObject ratesObj = jsonResponse.getJSONObject("rates");

        // Validate timestamp format roughly
        assertThat(timestamp).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z");

        // Validate that rates contain expected currency pairs
        assertThat(ratesObj.length()).isGreaterThan(0);
        // Check common currency pairs
        assertThat(ratesObj.has("USD_EUR")).isTrue();
        assertThat(ratesObj.has("USD_GBP")).isTrue();

        // Validate that rates are positive numbers
        assertThat(ratesObj.getDouble("USD_EUR")).isGreaterThan(0);
        assertThat(ratesObj.getDouble("USD_GBP")).isGreaterThan(0);

        // Additional consistency check: timestamp should be recent (within last 10 mins)
        Instant rateTimestamp = Instant.parse(timestamp);
        Instant now = Instant.now();
        long diffSeconds = Math.abs(now.getEpochSecond() - rateTimestamp.getEpochSecond());
        assertThat(diffSeconds).isLessThanOrEqualTo(600L); // 10 minutes
    }
}
