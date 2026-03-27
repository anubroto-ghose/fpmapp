/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8642
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:44:54
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmCommonController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for verifying automatic scheduled synchronization of currency exchange rates.
 * 
 * Preconditions:
 * - Scheduled synchronization job is configured and enabled.
 * - External currency exchange rate provider is mocked to return valid data.
 * - Database Currency_Exchange_Rates table is accessible via FpmCommonController.
 * 
 * This test triggers the synchronization job, monitors logs via UI, and verifies DB updates.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyExchangeRateSyncTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private FpmCommonController fpmCommonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 20);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        // Mock external currency exchange rate provider response
        when(currencyConvertionController.fetchLatestRates()).thenReturn(
                Arrays.asList(
                        new CurrencyConvertionController.CurrencyRate("USD", "EUR", 0.85, LocalDateTime.now()),
                        new CurrencyConvertionController.CurrencyRate("USD", "GBP", 0.75, LocalDateTime.now())
                )
        );

        // Clear or reset DB state before test
        fpmCommonController.clearCurrencyExchangeRates();
    }

    @Test
    public void testScheduledCurrencyExchangeRateSynchronization() throws InterruptedException {
        // Step 1: Query DB before synchronization
        List<FpmCommonController.CurrencyRateRecord> beforeSyncRates = fpmCommonController.getCurrencyExchangeRates();
        assertThat(beforeSyncRates).isEmpty();

        // Step 2: Trigger the scheduled synchronization job via UI or API
        // For demonstration, we simulate triggering via UI
        driver.get("http://localhost:8080/admin/currency-sync");

        // Wait for the sync button and click
        WebElement syncButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("triggerSyncBtn")));
        syncButton.click();

        // Step 3: Monitor job execution logs on UI
        WebElement logArea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("syncJobLogs")));

        // Wait up to 30 seconds for job completion message
        boolean jobCompleted = false;
        for (int i = 0; i < 30; i++) {
            String logs = logArea.getText();
            if (logs.contains("Synchronization completed successfully")) {
                jobCompleted = true;
                break;
            } else if (logs.toLowerCase().contains("error") || logs.toLowerCase().contains("exception")) {
                throw new AssertionError("Synchronization job failed with error logs: " + logs);
            }
            TimeUnit.SECONDS.sleep(1);
        }
        assertThat(jobCompleted).as("Synchronization job should complete successfully").isTrue();

        // Step 4: Query DB after synchronization
        List<FpmCommonController.CurrencyRateRecord> afterSyncRates = fpmCommonController.getCurrencyExchangeRates();

        // Verify that new or updated currency rates are present
        assertThat(afterSyncRates).isNotEmpty();

        // Verify that rates match mocked data
        boolean containsUsdEur = afterSyncRates.stream()
                .anyMatch(r -> "USD".equals(r.getFromCurrency()) && "EUR".equals(r.getToCurrency()) && r.getRate() == 0.85);
        boolean containsUsdGbp = afterSyncRates.stream()
                .anyMatch(r -> "USD".equals(r.getFromCurrency()) && "GBP".equals(r.getToCurrency()) && r.getRate() == 0.75);

        assertThat(containsUsdEur).isTrue();
        assertThat(containsUsdGbp).isTrue();

        // Step 5: Verify no data inconsistencies or missing rates
        for (FpmCommonController.CurrencyRateRecord rate : afterSyncRates) {
            assertThat(rate.getRate()).isGreaterThan(0);
            assertThat(rate.getFromCurrency()).isNotBlank();
            assertThat(rate.getToCurrency()).isNotBlank();
            assertThat(rate.getTimestamp()).isNotNull();
        }

        // Step 6: Verify synchronization respects DB constraints and indexes
        // This is implicitly verified by absence of exceptions and successful DB queries
    }
}
