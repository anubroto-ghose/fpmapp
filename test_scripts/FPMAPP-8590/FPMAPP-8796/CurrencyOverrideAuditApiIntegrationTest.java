/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8796
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:04:45
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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Integration test for verifying the Currency Override Audit API returns correct override logs with filtering.
 * 
 * Preconditions:
 * - At least one override has been submitted and logged.
 * - API access credentials with audit permissions are available.
 * 
 * This test uses Selenium WebDriver to simulate a user accessing the audit API endpoint via a simple UI page
 * that displays the override logs. The API service is mocked to return predefined data.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyOverrideAuditApiIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private String baseUrl;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private FpmUserProfileController userProfileController;

    private static final String API_ENDPOINT = "/api/currency/override-logs";

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
        baseUrl = "http://localhost:" + port;
    }

    /**
     * Test verifies that the override logs API returns filtered logs correctly and the UI displays them properly.
     */
    @Test
    public void testOverrideLogsApiReturnsCorrectFilteredLogs() throws InterruptedException {
        // Prepare mock data for override logs
        OverrideLogEntry log1 = new OverrideLogEntry(
                "adminUser1",
                Instant.parse("2024-05-01T10:15:30Z"),
                "USD/EUR",
                0.85,
                0.87,
                "Quarterly adjustment due to market volatility"
        );

        OverrideLogEntry log2 = new OverrideLogEntry(
                "adminUser2",
                Instant.parse("2024-05-03T14:20:00Z"),
                "USD/GBP",
                0.75,
                0.76,
                "Special event override"
        );

        List<OverrideLogEntry> mockLogs = Arrays.asList(log1, log2);

        // Mock the service method to return filtered logs based on parameters
        when(currencyConvertionController.getOverrideLogs(
                eq(LocalDate.of(2024, 5, 1)),
                eq(LocalDate.of(2024, 5, 5)),
                eq("adminUser1"),
                eq("USD/EUR")
        )).thenReturn(Arrays.asList(log1));

        // Simulate user navigating to a simple test page that calls the API and displays results
        // For demonstration, we create a minimal HTML page served by the test context or embedded server
        // Here, we simulate the API call directly via Selenium by navigating to a test page

        // Construct URL with query parameters for filtering
        String url = baseUrl + "/test/currency-override-logs.html?startDate=2024-05-01&endDate=2024-05-05&adminUser=adminUser1&currencyPair=USD/EUR";

        driver.get(url);

        // Wait for the page to load and display results (simple wait for element presence)
        Thread.sleep(2000); // In production, use WebDriverWait instead

        // Verify page title
        assertThat(driver.getTitle()).isEqualTo("Currency Override Logs");

        // Verify that the table with logs is displayed
        WebElement table = driver.findElement(By.id("overrideLogsTable"));
        assertThat(table).isNotNull();

        // Verify that only one row is present (excluding header)
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        // 1 header + 1 data row expected
        assertThat(rows.size()).isEqualTo(2);

        WebElement dataRow = rows.get(1);
        List<WebElement> cells = dataRow.findElements(By.tagName("td"));

        // Validate each cell content
        assertThat(cells.get(0).getText()).isEqualTo("adminUser1");
        assertThat(cells.get(1).getText()).isEqualTo("2024-05-01T10:15:30Z");
        assertThat(cells.get(2).getText()).isEqualTo("USD/EUR");
        assertThat(cells.get(3).getText()).isEqualTo("0.85");
        assertThat(cells.get(4).getText()).isEqualTo("0.87");
        assertThat(cells.get(5).getText()).isEqualTo("Quarterly adjustment due to market volatility");
    }

    /**
     * DTO representing a currency override log entry.
     */
    public static class OverrideLogEntry {
        private String adminUser;
        private Instant timestamp;
        private String currencyPair;
        private double oldValue;
        private double newValue;
        private String reason;

        public OverrideLogEntry(String adminUser, Instant timestamp, String currencyPair, double oldValue, double newValue, String reason) {
            this.adminUser = adminUser;
            this.timestamp = timestamp;
            this.currencyPair = currencyPair;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.reason = reason;
        }

        public String getAdminUser() {
            return adminUser;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public String getCurrencyPair() {
            return currencyPair;
        }

        public double getOldValue() {
            return oldValue;
        }

        public double getNewValue() {
            return newValue;
        }

        public String getReason() {
            return reason;
        }
    }
}
