/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6228
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:34:12
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Integration test with real HTTP call using WebDriver to invoke /currency/rates/historical
 * endpoint. Mocks service responses and verifies UI-related feedback.
 * <p>
 * Because the requirement is a Spring Boot integration test with WebDriver,
 * here we assume the API is accessible via localhost.
 * </p>
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HistoricalCurrencyRatesIntegrationTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private OkHttpClient httpClient;
    private ObjectMapper objectMapper;

    @BeforeAll
    public void setUp() {
        // ChromeDriver path must be set via system property or use WebDriverManager (not shown here)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);

        httpClient = new OkHttpClient();
        objectMapper = new ObjectMapper();
    }

    @AfterAll
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Verify Historical Currency Rates API returns correct data and UI reflects it")
    public void testHistoricalCurrencyRatesAPIAndUI() throws Exception {

        // Preconditions: Historical data exists in DB (assumed for integration testing)
        // Rest API URL
        String url = String.format("http://localhost:%d/currency/rates/historical?currencyCode=USD&startDate=2025-01-01&endDate=2025-01-10", port);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            assertThat(response).isNotNull();
            assertThat(response.code()).isEqualTo(200);

            String body = response.body().string();
            JsonNode rootNode = objectMapper.readTree(body);
            assertThat(rootNode.has("rates")).isTrue();
            JsonNode ratesNode = rootNode.get("rates");
            assertThat(ratesNode.isArray()).isTrue();

            LocalDate startDate = LocalDate.parse("2025-01-01");
            LocalDate endDate = LocalDate.parse("2025-01-10");

            // Collect all rate entries
            Map<LocalDate, Double> ratesMap =
                    objectMapper.readerForListOf(RateEntry.class)
                    .readValue(ratesNode.traverse())
                    .stream()
                    .collect(Collectors.toMap(Re -> Re.getDate(), Re -> Re.getRate()));

            // Verify each date from startDate to endDate exists with positive rate
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                assertThat(ratesMap.containsKey(date))
                        .withFailMessage("Missing rate entry for date: %s", date)
                        .isTrue();

                double rate = ratesMap.get(date);
                assertThat(rate)
                        .withFailMessage("Rate for %s should be positive, but was %f", date, rate)
                        .isGreaterThan(0.0);
            }

            // Confirm no date outside requested range
            for (LocalDate dateKey : ratesMap.keySet()) {
                assertThat(!dateKey.isBefore(startDate) && !dateKey.isAfter(endDate))
                        .withFailMessage("Found date outside requested range: %s", dateKey)
                        .isTrue();
            }

            // Confirm no override_flag field present
            ratesNode.forEach(node -> {
                assertThat(node.has("override_flag")).isFalse();
            });
        }

        // Perform a simple UI validation of the data via WebDriver
        driver.get(String.format("http://localhost:%d/ui/currency-rates/historical?currencyCode=USD&startDate=2025-01-01&endDate=2025-01-10", port));

        // Wait for rates table or element to appear
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        wait.until((ExpectedCondition<Boolean>) d -> d.findElements(By.id("historicalRatesTable")).size() > 0);

        WebElement table = driver.findElement(By.id("historicalRatesTable"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        // Expect one header + number of date rows
        assertThat(rows.size()).isEqualTo(1 + 10);

        // Validate each row data
        for (int i = 1; i < rows.size(); i++) {
            WebElement row = rows.get(i);
            List<WebElement> cols = row.findElements(By.tagName("td"));
            assertThat(cols.size()).isGreaterThanOrEqualTo(2);

            String dateStr = cols.get(0).getText();
            String rateStr = cols.get(1).getText();

            LocalDate rowDate = LocalDate.parse(dateStr);
            assertThat(!rowDate.isBefore(LocalDate.of(2025, 1, 1)) && !rowDate.isAfter(LocalDate.of(2025, 1, 10)))
                    .withFailMessage("Date in UI outside expected range: %s", rowDate)
                    .isTrue();

            double rateValue;
            try {
                rateValue = Double.parseDouble(rateStr);
            } catch (NumberFormatException e) {
                throw new AssertionError("Invalid rate value in UI: " + rateStr);
            }
            assertThat(rateValue).isGreaterThan(0.0);
        }
    }

    /**
     * Inner static class representing a rate entry from the JSON response.
     */
    static class RateEntry {
        private String date;
        private double rate;

        public RateEntry() {
        }

        public LocalDate getDate() {
            return LocalDate.parse(date);
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }
    }
}