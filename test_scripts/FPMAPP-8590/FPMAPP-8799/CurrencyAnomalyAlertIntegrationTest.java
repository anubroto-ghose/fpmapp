/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8799
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:35:36
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration test verifying alert triggers on currency rate data anomalies.
 * 
 * Preconditions:
 * - Alerting system configured and active.
 * - Currency anomaly detection rules defined.
 * - System connected to third-party API.
 * 
 * This test mocks the currency conversion service to inject anomalous data,
 * then verifies that the alerting system triggers alerts and notifications properly.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyAnomalyAlertIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private FpmUserProfileController userProfileController;

    @Autowired
    private FpmDealsheetController dealsheetController;

    @Autowired
    private FpmTravelController travelController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String TEST_CURRENCY_PAIR = "USD/EUR";

    private static final double NORMAL_RATE = 0.85;
    private static final double ANOMALOUS_RATE = 1.50; // Sudden spike

    private static final String ALERT_MESSAGE = "Currency rate anomaly detected for USD/EUR";

    private static AtomicBoolean alertTriggered = new AtomicBoolean(false);

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        alertTriggered.set(false);

        // Mock normal currency rate initially
        when(currencyConvertionController.getCurrentRate(TEST_CURRENCY_PAIR))
            .thenReturn(NORMAL_RATE);

        // Mock alerting system behavior: when anomaly detected, alertTriggered is set
        doAnswer(invocation -> {
            String currencyPair = invocation.getArgument(0);
            String message = invocation.getArgument(1);
            if (currencyPair.equals(TEST_CURRENCY_PAIR) && message.contains("anomaly")) {
                alertTriggered.set(true);
            }
            return null;
        }).when(fpmCommonController).triggerCurrencyAlert(any(String.class), any(String.class));
    }

    @Test
    public void testCurrencyRateAnomalyTriggersAlert() throws InterruptedException {
        // Step 1: Inject anomalous currency rate data
        when(currencyConvertionController.getCurrentRate(TEST_CURRENCY_PAIR))
            .thenReturn(ANOMALOUS_RATE);

        // Step 2: Simulate system processing incoming data
        // For this test, we simulate by calling the service method that processes rates
        // Assuming FpmForecastController has a method processCurrencyRates that triggers alerts
        fpmForecastController.processCurrencyRates();

        // Step 3: Monitor alerting system for triggered alerts
        // Wait up to 10 seconds for alert to be triggered
        int waitSeconds = 10;
        int waited = 0;
        while (!alertTriggered.get() && waited < waitSeconds) {
            Thread.sleep(1000);
            waited++;
        }

        // Step 4: Verify alert notifications sent
        assertTrue(alertTriggered.get(), "Alert was not triggered for currency anomaly.");

        // Additional UI verification: check alert notification in UI
        driver.get(BASE_URL + "/alerts");

        // Wait for alerts list to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("alertsTable")));

        List<WebElement> alerts = driver.findElements(By.cssSelector("#alertsTable tbody tr"));
        boolean foundAlert = alerts.stream().anyMatch(row -> {
            String text = row.getText();
            return text.contains(TEST_CURRENCY_PAIR) && text.contains("anomaly");
        });

        assertTrue(foundAlert, "Alert notification not found in UI alerts list.");
    }

    @Test
    public void testNoFalsePositiveOnNormalRate() throws InterruptedException {
        // Step 1: Provide normal currency rate
        when(currencyConvertionController.getCurrentRate(TEST_CURRENCY_PAIR))
            .thenReturn(NORMAL_RATE);

        // Step 2: Process rates
        fpmForecastController.processCurrencyRates();

        // Step 3: Wait to ensure no alert is triggered
        Thread.sleep(5000);

        // Step 4: Verify no alert triggered
        assertFalse(alertTriggered.get(), "False positive alert triggered on normal currency rate.");
    }

    @Test
    public void testAlertNotificationContainsRelevantDetails() throws InterruptedException {
        // Step 1: Inject anomalous currency rate
        when(currencyConvertionController.getCurrentRate(TEST_CURRENCY_PAIR))
            .thenReturn(ANOMALOUS_RATE);

        // Step 2: Process rates
        fpmForecastController.processCurrencyRates();

        // Step 3: Wait for alert
        int waitSeconds = 10;
        int waited = 0;
        while (!alertTriggered.get() && waited < waitSeconds) {
            Thread.sleep(1000);
            waited++;
        }

        assertTrue(alertTriggered.get(), "Alert was not triggered for currency anomaly.");

        // Step 4: Verify alert details via UI
        driver.get(BASE_URL + "/alerts/details?currencyPair=" + TEST_CURRENCY_PAIR);

        WebElement alertDetails = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("alertDetails")));

        String detailsText = alertDetails.getText();

        assertAll("Alert details content",
            () -> assertTrue(detailsText.contains(TEST_CURRENCY_PAIR), "Alert details missing currency pair."),
            () -> assertTrue(detailsText.toLowerCase().contains("anomaly"), "Alert details missing anomaly description."),
            () -> assertTrue(detailsText.matches(".*\d+\.\d+.*"), "Alert details missing numeric rate info.")
        );
    }
}