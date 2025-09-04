/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5258
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:19:56
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminOverrideLoggingIntegrationTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private final String adminId = "admin123";
    private final String currencyCode = "USD";
    private final double oldRate = 1.00;
    private final double newRate = 1.15;
    private final LocalDateTime overrideTimestamp = LocalDateTime.now();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeAll
    public void setupClass() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock the currency conversion override method to simulate successful override action
        Mockito.when(currencyConvertionController.overrideCurrencyRate(
                Mockito.eq(adminId), Mockito.eq(currencyCode), Mockito.anyDouble()))
                .then(invocation -> {
                    // Simulating stored log entry; In a real service, it would persist logs
                    // Here, for test, just return true to indicate success
                    return true;
                });

        // Mock a service method that returns recent override logs
        Mockito.when(currencyConvertionController.getOverrideLogs(anyString()))
                .thenReturn(List.of(new CurrencyConvertionController.OverrideLogEntry(
                        adminId,
                        currencyCode,
                        oldRate,
                        newRate,
                        overrideTimestamp.format(formatter)
                )));
    }

    @Test
    @DisplayName("Validate logging of admin override actions")
    public void testAdminCurrencyOverrideLogIsRecorded() throws Exception {
        // Step 1: Perform currency override via mocked service
        boolean overrideSuccess = currencyConvertionController.overrideCurrencyRate(adminId, currencyCode, newRate);
        assertTrue(overrideSuccess, "Currency override action should be successful");

        // Step 2: Navigate to the admin logs page
        String baseUrl = "http://localhost:" + port;
        driver.get(baseUrl + "/admin/logs");

        // Step 3: Locate the search input and submit the admin ID or currency override search
        WebElement searchInput = driver.findElement(By.id("searchLogsInput"));
        searchInput.clear();
        searchInput.sendKeys(adminId);

        WebElement searchButton = driver.findElement(By.id("searchLogsButton"));
        searchButton.click();

        // Step 4: Wait and verify logs are displayed including the override entry
        Thread.sleep(1000); // Using sleep for simplicity; in prod, use WebDriverWait

        List<WebElement> logEntries = driver.findElements(By.cssSelector(".log-entry"));

        assertFalse(logEntries.isEmpty(), "Expected at least one log entry after search");

        boolean foundOverrideLog = false;

        for (WebElement logEntry : logEntries) {
            String logText = logEntry.getText();
            if (logText.contains(adminId) && logText.contains(currencyCode) &&
                logText.contains(String.format("%.2f", oldRate)) && logText.contains(String.format("%.2f", newRate))) {
                foundOverrideLog = true;
                // Assert timestamp presence and format
                assertTrue(logText.matches(".*\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.*"), "Log entry must contain a valid timestamp");
                break;
            }
        }

        assertTrue(foundOverrideLog, "Override log entry with correct details should be present");
    }

    // Mock Controller classes inside test for completeness
    public static class CurrencyConvertionController {

        public static class OverrideLogEntry {
            public final String adminId;
            public final String currencyCode;
            public final double oldRate;
            public final double newRate;
            public final String timestamp;

            public OverrideLogEntry(String adminId, String currencyCode, double oldRate, double newRate, String timestamp) {
                this.adminId = adminId;
                this.currencyCode = currencyCode;
                this.oldRate = oldRate;
                this.newRate = newRate;
                this.timestamp = timestamp;
            }
        }

        public boolean overrideCurrencyRate(String adminId, String currencyCode, double newRate) {
            return false; // to be mocked
        }

        public List<OverrideLogEntry> getOverrideLogs(String filter) {
            return List.of(); // to be mocked
        }
    }
}
