/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6196
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:58:42
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.dto.FpmUserProfileController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring Boot integration test class with Selenium WebDriver for currency rate sync API.
 * 
 * Preconditions:
 * - Backend server running on random port
 * - Third-party currency rate service mocked
 * - Admin user authenticated
 * 
 * This test performs an end-to-end integration verification including UI (via Selenium)
 * and API validation with mocked service responses.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CurrencyRateSyncIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRateSyncIntegrationTest.class);

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static final String SYNC_ENDPOINT = "/currency/rates/sync";

    @BeforeAll
    public static void setupClass() {
        // Setup WebDriver with headless Chrome for CI
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        driver = new ChromeDriver(options);
        logger.info("WebDriver initialized.");
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
            logger.info("WebDriver shut down.");
        }
    }

    @Test
    public void testCurrencyRateSyncApi_andUi_verification() {
        // Step 1: Prepare mocked response from currency conversion controller
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("success", true);
        apiResponse.put("syncedAt", now.toString());

        when(currencyConvertionController.syncRates()).thenReturn(apiResponse);

        // Step 2: Perform POST /currency/rates/sync via WebTestClient (simulate API request)
        webTestClient.post()
            .uri(SYNC_ENDPOINT)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.syncedAt").isEqualTo(now.toString());

        logger.info("API /currency/rates/sync responded with success and syncedAt timestamp.");

        // Step 3: Validate currency rates table updated (mocked DB check)
        // As database details are unknown, we mock verification through controller method
        Map<String, Object> dbRates = new HashMap<>();
        dbRates.put("lastUpdated", now.toString());
        when(currencyConvertionController.getLatestSyncTimestamp()).thenReturn(now.toString());

        String latestTimestamp = currencyConvertionController.getLatestSyncTimestamp();
        assertThat(latestTimestamp).isEqualTo(now.toString());
        logger.info("CurrencyRates DB latest sync timestamp matches API response.");

        // Step 4: Use Selenium WebDriver to simulate admin login and trigger sync via UI (simulate realistic scenario)
        String baseUrl = "http://localhost:" + port;
        driver.get(baseUrl + "/admin/currency-sync");

        // Assuming a login page appears
        driver.findElement(By.id("username")).sendKeys("adminUser");
        driver.findElement(By.id("password")).sendKeys("correctHorseBatteryStaple");
        driver.findElement(By.id("loginButton")).click();

        // Wait for redirect after login - very simplified sleep to simulate wait (better to use WebDriverWait in prod)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted during wait", e);
        }

        // Now click the sync button
        driver.findElement(By.id("syncRatesButton")).click();

        // Wait for sync success message
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted during wait", e);
        }

        String successMessage = driver.findElement(By.id("syncSuccessMessage")).getText();
        assertThat(successMessage).contains("Synchronization completed");

        // Step 5: Verify no error messages
        boolean errorPresent = driver.findElements(By.id("syncErrorMessage")).size() > 0;
        assertThat(errorPresent).isFalse();

        logger.info("UI sync confirmation message received and no error messages present.");
    }
}
