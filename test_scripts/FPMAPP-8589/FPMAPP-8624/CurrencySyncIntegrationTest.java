/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8624
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:57:20
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
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.entities.User;

/**
 * Integration test for verifying scheduled synchronization of currency exchange rates.
 * 
 * Preconditions:
 * - External currency exchange rate provider is mocked and available.
 * - Scheduled synchronization job is enabled.
 * 
 * This test uses Selenium WebDriver to simulate UI interaction and verify synchronization logs.
 * It also mocks the external currency provider and verifies database updates.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencySyncIntegrationTest {

    private static WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Test verifies that the scheduled synchronization job fetches latest currency rates,
     * updates the database correctly with timestamps, and logs the process without errors.
     */
    @Test
    public void testScheduledCurrencySync() throws InterruptedException {
        // Mock response from external currency provider
        Map<String, Double> mockRates = new HashMap<>();
        mockRates.put("USD", 1.0);
        mockRates.put("EUR", 0.85);
        mockRates.put("JPY", 110.0);

        Instant syncTime = Instant.now();

        when(currencyConvertionController.fetchLatestRates()).thenReturn(mockRates);

        // Trigger the scheduled synchronization job via UI or API
        // For this test, we simulate triggering via UI button click
        driver.get("http://localhost:8080/currency-sync");

        WebElement syncButton = driver.findElement(By.id("syncButton"));
        syncButton.click();

        // Wait for synchronization to complete (poll logs or wait fixed time)
        // Here, wait max 15 seconds for log update
        boolean syncCompleted = false;
        for (int i = 0; i < 15; i++) {
            WebElement logElement = driver.findElement(By.id("syncLog"));
            String logText = logElement.getText();
            if (logText.contains("Synchronization completed successfully")) {
                syncCompleted = true;
                break;
            }
            Thread.sleep(1000);
        }

        assertThat(syncCompleted).as("Synchronization job should complete successfully").isTrue();

        // Verify database updated with latest rates and correct timestamps
        mockRates.forEach((currency, rate) -> {
            CurrencyRateEntity entity = currencyRateRepository.findTopByCurrencyOrderByTimestampDesc(currency);
            assertThat(entity).as("Currency rate entity for " + currency + " should exist").isNotNull();
            assertThat(entity.getRate()).as("Rate for " + currency + " should match").isEqualTo(rate);
            assertThat(entity.getTimestamp()).as("Timestamp for " + currency + " should be recent").isAfter(syncTime.minusSeconds(60));
        });

        // Verify no data loss or corruption
        long count = currencyRateRepository.count();
        assertThat(count).as("Currency rates count should be at least the mocked rates size").isGreaterThanOrEqualTo(mockRates.size());
    }

}

// --- Supporting repository and entity classes ---

package com.webapp.fpmapp.entities;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "currency_rates")
public class CurrencyRateEntity {

    @Id
    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "rate", nullable = false)
    private Double rate;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    public CurrencyRateEntity() {}

    public CurrencyRateEntity(String currency, Double rate, Instant timestamp) {
        this.currency = currency;
        this.rate = rate;
        this.timestamp = timestamp;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

package com.webapp.fpmapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp.fpmapp.entities.CurrencyRateEntity;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, String> {

    CurrencyRateEntity findTopByCurrencyOrderByTimestampDesc(String currency);
}
