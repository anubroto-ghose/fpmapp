/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5265
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:16:34
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CurrencyExchangeRatesIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @Autowired
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private CurrencyConvertionController mockedCurrencyConvertionController;

    private WebDriverWait wait;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is available in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
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
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Mock the currency conversion API call with sample realistic data
        Map<String, Double> mockRates = Map.of(
                "USD", 1.0,
                "EUR", 0.91,
                "GBP", 0.79,
                "JPY", 134.2
        );

        // Mock controller method getLatestRates or equivalent
        Mockito.when(mockedCurrencyConvertionController.getLatestRates())
                .thenReturn(mockRates);
    }

    private void loginUser() {
        driver.get("http://localhost:" + port + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("testuser");
        passwordInput.sendKeys("TestPassword123!");
        loginButton.click();

        // Verify login success - e.g., presence of logout button
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutButton")));
    }

    @Test
    public void testSuccessfulRetrievalOfRealTimeCurrencyExchangeRates() {
        try {
            loginUser();

            // Navigate to currency exchange rates page
            driver.get("http://localhost:" + port + "/currency-exchange");

            // Wait for currency rates container
            WebElement ratesContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyRatesContainer")));

            // Wait for the system to fetch and update rates - simulate defined interval (e.g., 5 seconds)
            // Here we wait for the currency rates elements to be updated - assume each currency is displayed in element with id rate-<curr_code>
            boolean ratesRefreshed = wait.until(driver -> {
                try {
                    WebElement usdRate = driver.findElement(By.id("rate-USD"));
                    WebElement eurRate = driver.findElement(By.id("rate-EUR"));
                    WebElement gbpRate = driver.findElement(By.id("rate-GBP"));
                    WebElement jpyRate = driver.findElement(By.id("rate-JPY"));

                    // Check text is not empty and parsable as a double
                    return !usdRate.getText().isBlank() &&
                            Double.parseDouble(usdRate.getText()) > 0 &&
                            !eurRate.getText().isBlank() &&
                            Double.parseDouble(eurRate.getText()) > 0 &&
                            !gbpRate.getText().isBlank() &&
                            Double.parseDouble(gbpRate.getText()) > 0 &&
                            !jpyRate.getText().isBlank() &&
                            Double.parseDouble(jpyRate.getText()) > 0;
                } catch (Exception e) {
                    return false;
                }
            });

            assertTrue(ratesRefreshed, "Currency exchange rates should be displayed and greater than zero");

            // Verify that displayed values match mock data
            WebElement usdRate = driver.findElement(By.id("rate-USD"));
            WebElement eurRate = driver.findElement(By.id("rate-EUR"));
            WebElement gbpRate = driver.findElement(By.id("rate-GBP"));
            WebElement jpyRate = driver.findElement(By.id("rate-JPY"));

            double usd = Double.parseDouble(usdRate.getText());
            double eur = Double.parseDouble(eurRate.getText());
            double gbp = Double.parseDouble(gbpRate.getText());
            double jpy = Double.parseDouble(jpyRate.getText());

            assertEquals(1.0, usd, 0.0001, "USD rate should be 1.0");
            assertEquals(0.91, eur, 0.0001, "EUR rate should be 0.91");
            assertEquals(0.79, gbp, 0.0001, "GBP rate should be 0.79");
            assertEquals(134.2, jpy, 0.0001, "JPY rate should be 134.2");

        } catch (Exception ex) {
            fail("Test failed due to exception: " + ex.getMessage());
        }
    }
}
