/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5266
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:15:56
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;

/**
 * Selenium integration test for Historical Currency Data Retrieval
 * Ticket: FPMAPP-5266
 * User story: FPMAPP-5252 (US-02)
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HistoricalCurrencyDataIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeAll
    public static void setupClass() {
        // Please set the path to your chromedriver executable if not on PATH
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run headless for CI environments
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
        // Mock the service to return fixed historical currency rates for a given request
        Mockito.reset(currencyConvertionController);

        // Mocked historical response: Map<Date, Rate>
        Map<String, Double> mockedRates = new LinkedHashMap<>();
        mockedRates.put("2024-05-01", 1.10);
        mockedRates.put("2024-05-02", 1.12);
        mockedRates.put("2024-05-03", 1.11);

        Mockito.when(currencyConvertionController.getHistoricalRates(
                anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenAnswer(invocation -> {
                    String currencyCode = invocation.getArgument(0);
                    LocalDate startDate = invocation.getArgument(1);
                    LocalDate endDate = invocation.getArgument(2);

                    if (!currencyCode.matches("[A-Z]{3}")) {
                        throw new IllegalArgumentException("Invalid currency code");
                    }

                    // Filter mocked data by date range
                    Map<String, Double> filtered = new LinkedHashMap<>();
                    for (Map.Entry<String, Double> entry : mockedRates.entrySet()) {
                        LocalDate date = LocalDate.parse(entry.getKey());
                        if ((date.isEqual(startDate) || date.isAfter(startDate)) &&
                                (date.isEqual(endDate) || date.isBefore(endDate))) {
                            filtered.put(entry.getKey(), entry.getValue());
                        }
                    }
                    return filtered;
                });
    }

    @Test
    public void testHistoricalCurrencyDataRetrieval() {
        // Assume we have an embedded server, build the base URL
        String baseUrl = "http://localhost:" + port + "/";

        // Step 1: Navigate to login page and perform login (precondition)
        driver.get(baseUrl + "login");
        WebElement userInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        userInput.sendKeys("testuser");
        passwordInput.sendKeys("securepassword");
        loginButton.click();

        // Wait for redirect to main page
        waitForPageLoad("dashboard");

        // Check login success by presence of logout button
        assertTrue(driver.findElements(By.id("logoutButton")).size() > 0,
                "User should be logged in and see logout button");

        // Step 2: Navigate to historical currency data request section
        driver.get(baseUrl + "currency/historical");

        waitForPageLoad("historicalCurrencyData");

        // Step 3: Enter a valid currency code and start/end dates
        WebElement currencyInput = driver.findElement(By.id("currencyCode"));
        WebElement startDateInput = driver.findElement(By.id("startDate"));
        WebElement endDateInput = driver.findElement(By.id("endDate"));
        WebElement submitButton = driver.findElement(By.id("submitRequest"));

        String testCurrency = "USD";
        LocalDate start = LocalDate.of(2024, 5, 1);
        LocalDate end = LocalDate.of(2024, 5, 3);

        currencyInput.clear();
        currencyInput.sendKeys(testCurrency);
        startDateInput.clear();
        startDateInput.sendKeys(start.format(formatter));
        endDateInput.clear();
        endDateInput.sendKeys(end.format(formatter));

        // Step 4: Submit the request
        submitButton.click();

        // Wait for results table to appear
        waitForElement(By.id("historicalRatesTable"));

        WebElement ratesTable = driver.findElement(By.id("historicalRatesTable"));
        assertNotNull(ratesTable, "The historical rates table should be displayed.");

        // Check rows match mocked data count
        int expectedRowCount = 3; // from mocked data
        int actualRowCount = ratesTable.findElements(By.tagName("tr")).size() - 1; // excluding header row

        assertEquals(expectedRowCount, actualRowCount, "Table rows count should match mocked data.");

        // Validate displayed data correctness
        for (Map.Entry<String, Double> entry : Map.of(
                "2024-05-01", 1.10,
                "2024-05-02", 1.12,
                "2024-05-03", 1.11)
                .entrySet()) {
            String date = entry.getKey();
            String rateString = String.format("%.2f", entry.getValue());

            // Each row presumably has date and rate columns
            assertTrue(driver.getPageSource().contains(date), "Page should contain date: " + date);
            assertTrue(driver.getPageSource().contains(rateString), "Page should contain rate: " + rateString);
        }
    }

    private void waitForPageLoad(String expectedPathFragment) {
        int attempts = 0;
        while (attempts < 10) {
            if (driver.getCurrentUrl().contains(expectedPathFragment)) {
                return;
            }
            sleep(500);
            attempts++;
        }
        fail("Timeout waiting for page load containing path fragment: " + expectedPathFragment);
    }

    private void waitForElement(By locator) {
        int attempts = 0;
        while (attempts < 10) {
            if (driver.findElements(locator).size() > 0) {
                return;
            }
            sleep(500);
            attempts++;
        }
        fail("Timeout waiting for element located by: " + locator.toString());
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Interrupted while waiting");
        }
    }
}