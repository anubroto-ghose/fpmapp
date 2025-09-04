/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5267
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:15:08
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.controllers.CurrencyConvertionController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Integration test for Historical Currency Data request using invalid parameters.
 * Preconditions: User is logged in and the app is connected to the currency exchange API.
 * 
 * Mocks CurrencyConvertionController to simulate API behavior.
 * 
 * Tests that submitting invalid currency code and an invalid date range displays an error message
 * and no data.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(HistoricalCurrencyDataInvalidParamsTest.TestConfig.class)
public class HistoricalCurrencyDataInvalidParamsTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private TestConfig testConfig;

    /**
     * Test configuration to set base URL and related settings.
     */
    public static class TestConfig {
        public String baseUrl = "http://localhost:8080";

        // Add other config properties if needed
    }

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver"); // Adjust path as necessary
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run headless for CI environments
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupTest() {
        // Mock the currency conversion controller to reject invalid parameters
        Mockito.when(currencyConvertionController.getHistoricalData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String currencyCode = invocation.getArgument(0);
                    String startDate = invocation.getArgument(1);
                    String endDate = invocation.getArgument(2);

                    // Validate currency code - example: only accept 3 uppercase letters
                    boolean invalidCurrency = currencyCode == null || !currencyCode.matches("[A-Z]{3}");

                    // Check date range - startDate after endDate is invalid
                    boolean invalidDateRange = false;
                    try {
                        java.time.LocalDate start = java.time.LocalDate.parse(startDate);
                        java.time.LocalDate end = java.time.LocalDate.parse(endDate);
                        if (start.isAfter(end)) {
                            invalidDateRange = true;
                        }
                    } catch (Exception e) {
                        invalidDateRange = true; // parsing error
                    }

                    if (invalidCurrency || invalidDateRange) {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Invalid parameters: currency code must be a 3-letter code and start date must be before end date.");
                        return errorResponse;
                    }

                    Map<String, Object> successResponse = new HashMap<>();
                    successResponse.put("data", "Some historical data here");
                    return successResponse;
                });

        // Additionally, simulate that the user is logged in by navigating to login page and logging in if required.
        // For this test simplification, we assume the test system auto-logs in or no login UX is needed.
    }

    @Test
    public void testHistoricalCurrencyDataInvalidParams() {
        String baseUrl = testConfig.baseUrl;
        driver.get(baseUrl + "/currency/historical");

        // Wait for the page/specific element to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("historicalRequestForm")));

        // Enter invalid currency code
        WebElement currencyInput = driver.findElement(By.id("currencyCode"));
        currencyInput.clear();
        currencyInput.sendKeys("XX1"); // Invalid currency code

        // Enter start date after end date
        WebElement startDateInput = driver.findElement(By.id("startDate"));
        startDateInput.clear();
        startDateInput.sendKeys("2024-06-15");

        WebElement endDateInput = driver.findElement(By.id("endDate"));
        endDateInput.clear();
        endDateInput.sendKeys("2024-06-10");

        // Submit the form
        WebElement submitBtn = driver.findElement(By.id("submitHistoricalRequest"));
        // Use JavaScript click to avoid issues with overlay or hidden states
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        // Wait for response error message or validation
        By errorMsgLocator = By.id("historicalDataErrorMessage");
        try {
            WebElement errorMsgElement = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsgLocator));
            String errorMessage = errorMsgElement.getText();
            Assertions.assertTrue(errorMessage.contains("Invalid parameters"), "Error message does not indicate invalid parameters");

            // Also ensure no historical data is displayed
            By dataTableLocator = By.id("historicalDataTable");
            boolean isDataTablePresent = driver.findElements(dataTableLocator).size() > 0;
            if (isDataTablePresent) {
                WebElement dataTable = driver.findElement(dataTableLocator);
                Assertions.assertEquals(0, dataTable.findElements(By.tagName("tr")).size(), "Historical data table should be empty for invalid params");
            }

        } catch (Exception e) {
            Assertions.fail("Error message or validation did not appear as expected.", e);
        }
    }
}
