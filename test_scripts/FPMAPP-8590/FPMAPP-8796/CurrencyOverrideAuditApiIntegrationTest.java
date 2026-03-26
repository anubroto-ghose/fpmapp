/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8796
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:33:34
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.CurrencyOverrideLogDTO;
import com.webapp.fpmapp.services.CurrencySyncService;

/**
 * Integration test for verifying the currency override audit API returns correct override logs with filtering.
 * 
 * This test uses Selenium WebDriver to simulate API calls and verify the response.
 * It mocks the CurrencySyncService to provide controlled test data.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyOverrideAuditApiIntegrationTest {

    private static WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private CurrencySyncService currencySyncService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
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
        // Prepare mock data for override logs
        CurrencyOverrideLogDTO log1 = new CurrencyOverrideLogDTO();
        log1.setAlertId(1001L);
        log1.setCurrencyPair("USD/EUR");
        log1.setOverrideTimestamp(Instant.parse("2026-03-20T10:15:30Z"));
        log1.setOverrideUserId(501L);
        log1.setOverrideUserName("adminUser1");
        log1.setOverrideValue(0.85);
        log1.setOverrideReason("Quarterly adjustment");

        CurrencyOverrideLogDTO log2 = new CurrencyOverrideLogDTO();
        log2.setAlertId(1002L);
        log2.setCurrencyPair("USD/GBP");
        log2.setOverrideTimestamp(Instant.parse("2026-03-22T14:00:00Z"));
        log2.setOverrideUserId(502L);
        log2.setOverrideUserName("adminUser2");
        log2.setOverrideValue(0.75);
        log2.setOverrideReason("Market correction");

        List<CurrencyOverrideLogDTO> mockLogs = Arrays.asList(log1, log2);

        // Mock the service method to return filtered logs based on input parameters
        when(currencySyncService.getOverrideLogs(
                any(LocalDate.class),
                any(LocalDate.class),
                any(String.class),
                any(String.class)))
            .thenAnswer(invocation -> {
                LocalDate startDate = invocation.getArgument(0);
                LocalDate endDate = invocation.getArgument(1);
                String adminUser = invocation.getArgument(2);
                String currencyPair = invocation.getArgument(3);

                // Filter mockLogs according to parameters
                return mockLogs.stream()
                        .filter(log -> {
                            LocalDate logDate = log.getOverrideTimestamp().atZone(ZoneOffset.UTC).toLocalDate();
                            boolean dateMatch = (startDate == null || !logDate.isBefore(startDate)) &&
                                                (endDate == null || !logDate.isAfter(endDate));
                            boolean userMatch = (adminUser == null || adminUser.isEmpty()) ||
                                                log.getOverrideUserName().equalsIgnoreCase(adminUser);
                            boolean currencyMatch = (currencyPair == null || currencyPair.isEmpty()) ||
                                                    log.getCurrencyPair().equalsIgnoreCase(currencyPair);
                            return dateMatch && userMatch && currencyMatch;
                        })
                        .toList();
            });
    }

    @Test
    public void testOverrideLogsApiWithFilters() throws Exception {
        // Prepare filter parameters
        String startDate = "2026-03-19";
        String endDate = "2026-03-21";
        String adminUser = "adminUser1";
        String currencyPair = "USD/EUR";

        // Construct the API URL with query parameters
        String apiUrl = String.format(
                "%s/api/currency/override-logs?startDate=%s&endDate=%s&adminUser=%s&currencyPair=%s",
                BASE_URL, startDate, endDate, adminUser, currencyPair);

        // Use Selenium WebDriver to open the URL and get the JSON response
        driver.get(apiUrl);

        // The API returns JSON, so get the page source (raw JSON)
        String jsonResponse = driver.findElement(By.tagName("pre")).getText();

        // Parse JSON response
        CurrencyOverrideLogDTO[] logs = objectMapper.readValue(jsonResponse, CurrencyOverrideLogDTO[].class);

        // Assertions
        assertThat(logs).isNotNull();
        assertThat(logs.length).isEqualTo(1);

        CurrencyOverrideLogDTO log = logs[0];
        assertThat(log.getCurrencyPair()).isEqualToIgnoringCase(currencyPair);
        assertThat(log.getOverrideUserName()).isEqualToIgnoringCase(adminUser);
        assertThat(log.getOverrideReason()).isNotEmpty();
        assertThat(log.getOverrideTimestamp()).isNotNull();
        assertThat(log.getOverrideValue()).isGreaterThan(0);

        // Additional check: timestamp within filter range
        LocalDate logDate = log.getOverrideTimestamp().atZone(ZoneOffset.UTC).toLocalDate();
        assertThat(logDate).isAfterOrEqualTo(LocalDate.parse(startDate));
        assertThat(logDate).isBeforeOrEqualTo(LocalDate.parse(endDate));
    }

    @Test
    public void testOverrideLogsApiReturnsAllWhenNoFilters() throws Exception {
        // API URL without filters
        String apiUrl = String.format("%s/api/currency/override-logs", BASE_URL);

        driver.get(apiUrl);

        String jsonResponse = driver.findElement(By.tagName("pre")).getText();

        CurrencyOverrideLogDTO[] logs = objectMapper.readValue(jsonResponse, CurrencyOverrideLogDTO[].class);

        assertThat(logs).isNotNull();
        assertThat(logs.length).isEqualTo(2);
    }

    @Test
    public void testOverrideLogsApiWithInvalidFilterReturnsEmpty() throws Exception {
        // Filters that do not match any logs
        String startDate = "2025-01-01";
        String endDate = "2025-01-02";
        String adminUser = "nonexistentUser";
        String currencyPair = "XYZ/ABC";

        String apiUrl = String.format(
                "%s/api/currency/override-logs?startDate=%s&endDate=%s&adminUser=%s&currencyPair=%s",
                BASE_URL, startDate, endDate, adminUser, currencyPair);

        driver.get(apiUrl);

        String jsonResponse = driver.findElement(By.tagName("pre")).getText();

        CurrencyOverrideLogDTO[] logs = objectMapper.readValue(jsonResponse, CurrencyOverrideLogDTO[].class);

        assertThat(logs).isNotNull();
        assertThat(logs.length).isEqualTo(0);
    }
}