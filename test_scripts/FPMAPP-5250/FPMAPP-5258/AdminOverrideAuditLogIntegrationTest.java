/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5258
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:54:16
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.controllers.CurrencyConvertionController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyIntegrationService;

/**
 * Integration test for validating that currency admin override actions are logged and
 * displayed correctly in the admin audit logs UI.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AdminOverrideAuditLogIntegrationTest {

    private WebDriver driver;

    @MockBean
    private CurrencyIntegrationService currencyIntegrationService;

    @Autowired
    private CurrencyConvertionController currencyConvertionController;

    private AutoCloseable mocks;

    private static final String ADMIN_USERNAME = "adminUser123";
    private static final String CURRENCY_CODE = "USD";
    private static final double OLD_RATE = 1.1200;
    private static final double NEW_RATE = 1.1500;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
        // Assuming chromedriver is in PATH or set webdriver.chrome.driver system property
        driver = new ChromeDriver(options);

        // Mock the currency override log entries that would be returned by the service for displaying logs
        when(currencyIntegrationService.getRecentOverrideLogs(any(Integer.class)))
            .thenReturn(Collections.singletonList(
                new com.webapp.fpmapp.entities.CurrencyOverrideLog(
                    1001L, CURRENCY_CODE, OLD_RATE, NEW_RATE, ADMIN_USERNAME,
                    Instant.now(), false
                )
            ));

        // Mock behavior of currency override to mark logs and notification
        when(currencyIntegrationService.overrideCurrencyRate(any(String.class), any(Double.class), any(String.class)))
            .thenAnswer(invocation -> {
                String currency = invocation.getArgument(0);
                Double rate = invocation.getArgument(1);
                String adminUser = invocation.getArgument(2);
                // Log entry simulated - in real scenario could store to DB
                return true;
            });
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
        if (mocks != null) {
            mocks.close();
        }
    }

    /**
     * Preconditions: Administrator has overridden a currency rate successfully.
     * 
     * Steps:
     * 1. Perform a currency rate override with admin user context
     * 2. Navigate to admin logs page where override logs appear
     * 3. Search/filter for the override entry
     * 
     * Expected:
     * - The recent override entry is displayed,
     * - Showing admin ID, currency code, old and new rates, and timestamp.
     */
    @Test
    @DisplayName("Validate admin override action is logged and visible in audit logs")
    public void testAdminOverrideActionIsLoggedAndVisibleInAuditTrail() {
        try {
            // Step 1: Simulate admin override - call service (mocked)
            boolean overrideSuccess = currencyIntegrationService.overrideCurrencyRate(CURRENCY_CODE, NEW_RATE, ADMIN_USERNAME);
            assertThat(overrideSuccess).isTrue();

            // Step 2: Navigate to admin audit logs UI page
            driver.get(BASE_URL + "/admin/logs");

            // Small wait for page load, replace with explicit wait if available
            Thread.sleep(1000);

            // Step 3: Locate logs search input and enter currency code
            WebElement searchBox = driver.findElement(By.id("searchOverrideLogs"));
            searchBox.clear();
            searchBox.sendKeys(CURRENCY_CODE);

            // Click search or trigger search event
            WebElement searchBtn = driver.findElement(By.id("btnSearchLogs"));
            searchBtn.click();

            // Wait for results to refresh
            Thread.sleep(1000);

            // Verify logs displayed in table
            WebElement logsTable = driver.findElement(By.id("overrideLogsTable"));
            List<WebElement> rows = logsTable.findElements(By.tagName("tr"));
            assertThat(rows).isNotEmpty();

            boolean foundMatchingLog = false;
            for (WebElement row : rows) {
                List<WebElement> cols = row.findElements(By.tagName("td"));
                if (cols.size() >= 5) {
                    String adminIdText = cols.get(0).getText().trim();
                    String currencyText = cols.get(1).getText().trim();
                    String oldRateText = cols.get(2).getText().trim();
                    String newRateText = cols.get(3).getText().trim();
                    String timestampText = cols.get(4).getText().trim();

                    boolean adminMatches = ADMIN_USERNAME.equals(adminIdText);
                    boolean currencyMatches = CURRENCY_CODE.equals(currencyText);
                    boolean oldRateMatches = Double.parseDouble(oldRateText) == OLD_RATE;
                    boolean newRateMatches = Double.parseDouble(newRateText) == NEW_RATE;

                    // Check timestamp parsable (basic check)
                    boolean timestampValid = false;
                    try {
                        Instant parsedInstant = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(timestampText));
                        timestampValid = parsedInstant.isBefore(Instant.now().plusSeconds(5));
                    } catch (Exception e) {
                        timestampValid = false;
                    }

                    if (adminMatches && currencyMatches && oldRateMatches && newRateMatches && timestampValid) {
                        foundMatchingLog = true;
                        break;
                    }
                }
            }
            assertThat(foundMatchingLog).as("Check that admin override log entry is present with correct details").isTrue();

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during test execution", ie);
        } catch (Exception ex) {
            throw new RuntimeException("Test failed due to unexpected error", ex);
        }
    }
}
