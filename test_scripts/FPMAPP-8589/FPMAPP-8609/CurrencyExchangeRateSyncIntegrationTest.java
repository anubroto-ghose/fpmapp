/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8609
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 08:07:57
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.entities.CurrencyRate;
import com.webapp.fpmapp.repositories.CurrencyRateRepository;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test for currency exchange rate synchronization.
 * 
 * Preconditions:
 * - External currency exchange rate provider is mocked and responsive.
 * - Scheduled synchronization job can be triggered manually via UI.
 * - Database is accessible.
 * 
 * This test uses Selenium WebDriver to simulate user interaction triggering the sync job,
 * mocks external service responses, and verifies DB updates.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyExchangeRateSyncIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        // Clear DB before each test
        currencyRateRepository.deleteAll();

        // Mock external currency exchange rate provider response
        Map<String, Double> mockRates = new HashMap<>();
        mockRates.put("USD", 1.0);
        mockRates.put("EUR", 0.85);
        mockRates.put("JPY", 110.0);

        when(currencyConvertionController.getLatestRates()).thenReturn(mockRates);
    }

    @Test
    public void testSuccessfulCurrencyExchangeRateSynchronization() {
        // Navigate to the admin page where sync job can be triggered
        driver.get(BASE_URL + "/admin/currency-sync");

        // Wait for the sync button to be visible
        WebElement syncButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("syncCurrencyRatesBtn")));

        // Click the sync button to trigger the scheduled synchronization job manually
        syncButton.click();

        // Wait for success message or job completion indicator
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("syncSuccessMessage")));

        assertThat(successMsg.getText()).containsIgnoringCase("Synchronization completed successfully");

        // Verify logs or API call indicators on UI (if any)
        WebElement logArea = driver.findElement(By.id("syncJobLogs"));
        assertThat(logArea.getText()).contains("Fetched latest currency exchange rates");

        // Verify database entries
        Iterable<CurrencyRate> rates = currencyRateRepository.findAll();
        Map<String, CurrencyRate> rateMap = new HashMap<>();
        rates.forEach(rate -> rateMap.put(rate.getCurrencyCode(), rate));

        // Assert all mocked currencies are present
        assertThat(rateMap).containsKeys("USD", "EUR", "JPY");

        // Assert values and timestamps
        CurrencyRate usdRate = rateMap.get("USD");
        assertThat(usdRate.getRate()).isEqualTo(1.0);
        assertThat(usdRate.getTimestamp()).isNotNull();
        assertThat(usdRate.getVersion()).isGreaterThanOrEqualTo(1);

        CurrencyRate eurRate = rateMap.get("EUR");
        assertThat(eurRate.getRate()).isEqualTo(0.85);
        assertThat(eurRate.getTimestamp()).isNotNull();
        assertThat(eurRate.getVersion()).isGreaterThanOrEqualTo(1);

        CurrencyRate jpyRate = rateMap.get("JPY");
        assertThat(jpyRate.getRate()).isEqualTo(110.0);
        assertThat(jpyRate.getTimestamp()).isNotNull();
        assertThat(jpyRate.getVersion()).isGreaterThanOrEqualTo(1);

        // Verify no data loss or corruption by checking DB count
        long count = currencyRateRepository.count();
        assertThat(count).isEqualTo(3);
    }
}
