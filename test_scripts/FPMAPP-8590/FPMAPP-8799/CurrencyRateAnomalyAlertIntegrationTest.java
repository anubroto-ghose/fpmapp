/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8799
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:02:58
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

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

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration test verifying alert triggers on currency rate data anomalies.
 * 
 * Preconditions:
 * - Alerting system configured and active.
 * - Currency rate anomaly detection rules defined.
 * - System connected to third-party API.
 * 
 * Test Steps:
 * 1. Inject anomalous currency rate data.
 * 2. Allow system processing.
 * 3. Monitor alerting system UI for triggered alerts.
 * 4. Verify alert notifications.
 * 
 * This test uses Selenium WebDriver to simulate user interaction and verify alerts.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyRateAnomalyAlertIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

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
    public void setupMocks() {
        MockitoAnnotations.openMocks(this);

        // Mock user profile for authentication
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("financial_analyst");
        mockUser.setEmail("analyst@bank.com");
        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock alerting system status
        when(fpmCommonController.isAlertingSystemActive()).thenReturn(true);

        // Mock normal currency rate data initially
        when(currencyConvertionController.getLatestCurrencyRate("USD", "EUR"))
            .thenReturn(0.85);
    }

    /**
     * Test verifies that an alert is triggered when an anomalous currency rate is injected.
     */
    @Test
    public void testCurrencyRateAnomalyTriggersAlert() throws InterruptedException {
        // Step 1: Inject anomalous currency rate data (simulate sudden spike)
        double anomalousRate = 1.50; // Sudden spike from 0.85 to 1.50

        // Mock the service to return anomalous rate on next call
        when(currencyConvertionController.getLatestCurrencyRate("USD", "EUR"))
            .thenReturn(anomalousRate);

        // Simulate system processing delay
        TimeUnit.SECONDS.sleep(2);

        // Step 2: Trigger system to process incoming data
        // Assuming there is an endpoint or method to trigger processing, we simulate it here
        fpmCommonController.processCurrencyRateUpdate("USD", "EUR", anomalousRate, LocalDateTime.now());

        // Step 3: Open alerting system UI page to monitor alerts
        driver.get("http://localhost:8080/alerts");

        // Wait for alert list to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("alertTable")));

        // Step 4: Verify alert is present with correct details
        WebElement alertTable = driver.findElement(By.id("alertTable"));

        boolean alertFound = alertTable.findElements(By.xpath(".//tr")).stream()
            .anyMatch(row -> {
                String text = row.getText();
                return text.contains("Currency Rate Anomaly")
                    && text.contains("USD/EUR")
                    && text.contains(String.valueOf(anomalousRate));
            });

        assertThat(alertFound).as("Alert for currency rate anomaly should be present").isTrue();

        // Verify alert notification sent to configured recipients
        // Assuming fpmCommonController can provide last sent alert notifications
        var lastAlerts = fpmCommonController.getLastSentAlerts();
        assertThat(lastAlerts).isNotEmpty();

        boolean notificationCorrect = lastAlerts.stream()
            .anyMatch(alert -> alert.getType().equals("Currency Rate Anomaly")
                && alert.getDetails().contains("USD/EUR")
                && alert.getDetails().contains(String.valueOf(anomalousRate))
                && alert.getRecipients().contains("analyst@bank.com"));

        assertThat(notificationCorrect).as("Alert notification should contain relevant anomaly details and recipients").isTrue();

        // Verify no false positives: no alerts for normal currency pairs
        boolean falsePositive = alertTable.findElements(By.xpath(".//tr")).stream()
            .anyMatch(row -> {
                String text = row.getText();
                return text.contains("Currency Rate Anomaly")
                    && !text.contains("USD/EUR");
            });

        assertThat(falsePositive).as("No false positive alerts should be present").isFalse();
    }
}
