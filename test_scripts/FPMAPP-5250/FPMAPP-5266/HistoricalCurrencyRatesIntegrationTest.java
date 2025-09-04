/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5266
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:41:59
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

import com.webapp.fpmapp.controllers.CurrencyConvertionController;

/**
 * Spring Boot + Selenium Integration test for testing historical currency exchange rate retrieval
 * Test covers user interaction to fetch historical data via UI and verifying mocked backend response
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // assumes default port 8080
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class HistoricalCurrencyRatesIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private CurrencyConvertionController currencyConversionController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver with headless mode for CI/CD environments
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        // Assumes chromedriver executable in path
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupTest() {
        // Mock the service call that fetches historical currency data
        // Sample mock response
        List<HistoricalCurrencyRatesResponse> mockResponse = Arrays.asList(
                new HistoricalCurrencyRatesResponse("2025-08-01", "USD", 1.0),
                new HistoricalCurrencyRatesResponse("2025-08-02", "USD", 0.99),
                new HistoricalCurrencyRatesResponse("2025-08-03", "USD", 1.01)
        );

        when(currencyConversionController.getHistoricalRates(eq("USD"), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(mockResponse);
    }

    /**
     * Sanity check to ensure that login and required elements exist
     */
    @Test
    public void testPreconditions_UserLoggedInAndApiConnected() {
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait until redirected to dashboard or homepage
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertThat(driver.getCurrentUrl()).contains("/dashboard");
    }

    /**
     * Full E2E test for historical currency data retrieval
     */
    @Test
    public void testFetchHistoricalCurrencyData_DisplaysCorrectRates() {
        // Step 1 & 2: User logged in
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 3: Navigate to historical currency data request section
        // Assuming there is a menu item or direct URL
        driver.get(BASE_URL + "/currency/historical");

        // Wait for form elements
        WebElement currencyCodeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyCode")));
        WebElement startDateInput = driver.findElement(By.id("startDate"));
        WebElement endDateInput = driver.findElement(By.id("endDate"));
        WebElement submitButton = driver.findElement(By.id("submitRequest"));

        // Step 4: Enter valid currency code and date range
        currencyCodeInput.clear();
        currencyCodeInput.sendKeys("USD");
        startDateInput.sendKeys("2025-08-01");
        endDateInput.sendKeys("2025-08-03");

        // Submit the request
        submitButton.click();

        // Wait for results to display
        WebElement resultsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("historicalRatesTable")));

        // Verify table rows and content
        List<WebElement> rows = resultsTable.findElements(By.tagName("tr"));
        // first row might be header, so check at least 4 rows (header + 3 data)
        assertThat(rows.size()).isGreaterThanOrEqualTo(4);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Check content of table rows (skip header row)
        boolean usdFound = false;
        for (int i = 1; i < rows.size(); i++) {
            WebElement row = rows.get(i);
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() < 3) {
                throw new AssertionError("Table row does not have enough columns");
            }

            String dateStr = cells.get(0).getText();
            String currency = cells.get(1).getText();
            String rateStr = cells.get(2).getText();

            LocalDate date = LocalDate.parse(dateStr, formatter);
            assertThat(date).isBetween(LocalDate.parse("2025-08-01"), LocalDate.parse("2025-08-03"));

            assertThat(currency).isEqualTo("USD");

            double rate = Double.parseDouble(rateStr);
            // Verified mocked data rates
            assertThat(rate).isIn(1.0, 0.99, 1.01);
            usdFound = true;
        }

        assertThat(usdFound).isTrue();
    }

    /**
     * Mock DTO representing currency rate response used for mocking service layer return
     */
    private static class HistoricalCurrencyRatesResponse {
        private String date;
        private String currencyCode;
        private double rate;

        public HistoricalCurrencyRatesResponse(String date, String currencyCode, double rate) {
            this.date = date;
            this.currencyCode = currencyCode;
            this.rate = rate;
        }

        public String getDate() {
            return date;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public double getRate() {
            return rate;
        }
    }
}
