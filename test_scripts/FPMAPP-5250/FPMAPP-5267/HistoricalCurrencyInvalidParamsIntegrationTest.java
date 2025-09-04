/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5267
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:41:04
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

import com.webapp.fpmapp.services.CurrencyConvertionController;

import static org.mockito.Mockito.when;

/**
 * Integration test with Selenium WebDriver for testing the historical currency data section.
 *
 * Preconditions: User logged in, connected to currency exchange API (mocked).
 * Test case verifies invalid input handling scenario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HistoricalCurrencyInvalidParamsIntegrationTest {

    private WebDriver driver;

    private WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public void setUp() {
        // Setup ChromeDriver with headless option for CI environments
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock the service layer to simulate connected currency exchange API,
        // but here we want to verify rejection of invalid parameters before calling service,
        // so the service shouldn't be called with invalid input.
        when(currencyConvertionController.getHistoricalRates("INVALID", "2025-12-31", "2025-01-01"))
            .thenThrow(new IllegalArgumentException("Invalid query parameters"));
    }

    @AfterAll
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("FPMAPP-5252-TC03: Requesting historical data with invalid parameters should show error message")
    public void testInvalidHistoricalCurrencyRequest() {
        // Step 1: Navigate to the historical currency data request section
        driver.get(BASE_URL + "/currency/historical");

        // Assume page title or header confirms this is the correct section
        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        assertThat(header.getText()).containsIgnoringCase("Historical Currency Data");

        // Step 2: Enter invalid currency code and invalid date range (start date after end date)
        WebElement currencyInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("currencyCodeInput")));
        WebElement startDateInput = driver.findElement(By.id("startDateInput"));
        WebElement endDateInput = driver.findElement(By.id("endDateInput"));

        currencyInput.clear();
        currencyInput.sendKeys("INVALID");
        startDateInput.clear();
        startDateInput.sendKeys("2025-12-31");
        endDateInput.clear();
        endDateInput.sendKeys("2025-01-01");

        // Step 3: Submit the request for historical data
        WebElement submitButton = driver.findElement(By.id("submitHistoricalRequestButton"));
        submitButton.click();

        // Wait for error message element to appear
        By errorMessageLocator = By.id("historicalErrorMessage");

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessageLocator));

        // Expected Results: System returns error message indicating invalid parameters and no data displayed
        assertThat(errorMessage.getText())
            .as("Check error message content")
            .containsIgnoringCase("invalid parameters")
            .containsIgnoringCase("currency code")
            .containsIgnoringCase("date range");

        // Verify no historical data table is displayed
        boolean isTableDisplayed = driver.findElements(By.id("historicalDataTable")).stream()
            .anyMatch(WebElement::isDisplayed);

        assertThat(isTableDisplayed).isFalse();
    }
}
