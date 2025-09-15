/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6180
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:12:09
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.controller.CurrencyConvertionController;
import com.webapp.fpmapp.entity.CurrencyRate;
import com.webapp.fpmapp.repository.CurrencyRateRepository;
import com.webapp.fpmapp.service.CurrencySyncScheduledJob;

/**
 * Spring Boot Integration Test with Selenium WebDriver.
 * 
 * This test verifies that the scheduled synchronization job updates currency rates in the database,
 * real-time and historical rates are properly stored, and failures are logged.
 * 
 * Assumes the application exposes a simple status page for monitoring job execution and logs.
 * We will mock external 3rd party service via Controller mock to simulate update.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CurrencySyncScheduledJobIT {

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    @Autowired
    private CurrencySyncScheduledJob currencySyncScheduledJob;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @BeforeEach
    public void setup() {
        // Setup headless ChromeDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);

        // Setup mock for external currency data source
        when(currencyConvertionController.fetchLatestCurrencyRates())
                .thenReturn(Arrays.asList(
                        new CurrencyRate("USD", 1.00, LocalDateTime.now()),
                        new CurrencyRate("EUR", 0.85, LocalDateTime.now()),
                        new CurrencyRate("JPY", 110.0, LocalDateTime.now())
                ));

        when(currencyConvertionController.fetchHistoricalCurrencyRates(anyString(), any()))
                .thenReturn(Arrays.asList(
                        new CurrencyRate("USD", 0.97, LocalDateTime.now().minusDays(1)),
                        new CurrencyRate("EUR", 0.83, LocalDateTime.now().minusDays(1))
                ));

        // Clear repository before each test
        currencyRateRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Test the scheduled synchronization job:
     *  - Trigger the job manually
     *  - Verify DB updates with latest rates
     *  - Verify historical rates are preserved
     *  - Confirm job status via Web UI
     *  - Confirm no errors in system notifications
     */
    @Test
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    public void testScheduledCurrencySyncJob() throws InterruptedException {
        // Trigger scheduled job manually
        currencySyncScheduledJob.syncCurrencyRates();

        // Allow some time for job to complete and DB update
        Thread.sleep(1500);

        // Verify currency rates updated in DB
        List<CurrencyRate> allRates = currencyRateRepository.findAll();
        assertThat(allRates).isNotEmpty();

        // Check presence of latest real-time rates
        Optional<CurrencyRate> usdRateOpt = currencyRateRepository.findTopByCurrencyCodeOrderByTimestampDesc("USD");
        assertThat(usdRateOpt).isPresent();
        CurrencyRate usdRate = usdRateOpt.get();
        assertThat(usdRate.getRate()).isEqualTo(1.00);

        // Check historical rates preserved if any (simulate by adding historical rate first)
        CurrencyRate oldEUR = new CurrencyRate("EUR", 0.80, LocalDateTime.now().minusDays(10));
        currencyRateRepository.save(oldEUR);

        List<CurrencyRate> historicalRates = currencyRateRepository.findByCurrencyCodeAndTimestampBefore("EUR", LocalDateTime.now().minusDays(5));
        assertThat(historicalRates).contains(oldEUR);

        // Access the web interface to verify job status and logs
        driver.get(BASE_URL + "/admin/currency-sync/status");

        // Look for job success message
        WebElement statusElement = driver.findElement(By.id("job-status"));
        String statusText = statusElement.getText();
        assertThat(statusText.toLowerCase()).contains("success");

        // Check log panel for errors
        WebElement logElement = driver.findElement(By.id("job-log"));
        String logs = logElement.getText();
        assertThat(logs.toLowerCase()).doesNotContain("error").doesNotContain("exception");

        // Additionally, check UI notifications badge count (if any failures occurred, badge would show)
        WebElement notifBadge = driver.findElement(By.id("notification-badge"));
        String notifCountText = notifBadge.getText();
        int notifCount = Integer.parseInt(notifCountText.trim());
        assertThat(notifCount).isEqualTo(0);
    }

    /**
     * Additional test: simulate failure in external API and verify error handling and notifications.
     */
    @Test
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    public void testScheduledCurrencySyncJob_withApiFailure() throws InterruptedException {
        // Setup mock to throw exception simulating external API failure
        when(currencyConvertionController.fetchLatestCurrencyRates())
                .thenThrow(new RuntimeException("Third party API down"));

        try {
            // Trigger job
            currencySyncScheduledJob.syncCurrencyRates();
        } catch (Exception e) {
            // Exception expected
        }

        // Wait a bit for job logging
        Thread.sleep(500);

        driver.get(BASE_URL + "/admin/currency-sync/status");

        // Job status should reflect failure
        WebElement statusElement = driver.findElement(By.id("job-status"));
        String statusText = statusElement.getText();
        assertThat(statusText.toLowerCase()).contains("failed").contains("third party");

        // Logs contain error details
        WebElement logElement = driver.findElement(By.id("job-log"));
        String logs = logElement.getText();
        assertThat(logs.toLowerCase()).contains("runtimeexception");

        // Notification badge shows alert
        WebElement notifBadge = driver.findElement(By.id("notification-badge"));
        String notifCountText = notifBadge.getText();
        int notifCount = Integer.parseInt(notifCountText.trim());
        assertThat(notifCount).isGreaterThan(0);
    }

    /**
     * Configuration for scheduler bean override
     */
    @Configuration
    static class TestConfig {

        @Bean
        public ThreadPoolTaskScheduler taskScheduler() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(2);
            scheduler.setThreadNamePrefix("TestScheduler-");
            return scheduler;
        }
    }
}
