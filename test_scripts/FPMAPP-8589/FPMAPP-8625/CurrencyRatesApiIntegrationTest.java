/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8625
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:56:48
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.services.CurrencyConvertionController;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
public class CurrencyRatesApiIntegrationTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private String baseUrl;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        baseUrl = "http://localhost:" + port + "/fpm/currency/rates";
    }

    @Test
    public void testFetchCurrentCurrencyRate() {
        // Arrange
        String currencyCode = "USD";
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("rate", 1.12);
        mockResponse.put("timestamp", now.toString());
        mockResponse.put("override_flag", false);

        when(currencyConvertionController.getCurrencyRate(eq(currencyCode), eq(null)))
                .thenReturn(mockResponse);

        // Act
        driver.get(baseUrl + "?currencyCode=" + currencyCode);

        // Assert
        WebElement rateElement = driver.findElement(By.id("rate"));
        WebElement timestampElement = driver.findElement(By.id("timestamp"));
        WebElement overrideFlagElement = driver.findElement(By.id("override_flag"));

        assertThat(Double.parseDouble(rateElement.getText())).isEqualTo(1.12);
        assertThat(timestampElement.getText()).isEqualTo(now.toString());
        assertThat(Boolean.parseBoolean(overrideFlagElement.getText())).isFalse();
    }

    @Test
    public void testFetchHistoricalCurrencyRate() {
        // Arrange
        String currencyCode = "EUR";
        Instant historicalTimestamp = Instant.now().minus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("rate", 0.89);
        mockResponse.put("timestamp", historicalTimestamp.toString());
        mockResponse.put("override_flag", true);

        when(currencyConvertionController.getCurrencyRate(eq(currencyCode), eq(historicalTimestamp.toString())))
                .thenReturn(mockResponse);

        // Act
        driver.get(baseUrl + "?currencyCode=" + currencyCode + "&timestamp=" + historicalTimestamp.toString());

        // Assert
        WebElement rateElement = driver.findElement(By.id("rate"));
        WebElement timestampElement = driver.findElement(By.id("timestamp"));
        WebElement overrideFlagElement = driver.findElement(By.id("override_flag"));

        assertThat(Double.parseDouble(rateElement.getText())).isEqualTo(0.89);
        assertThat(timestampElement.getText()).isEqualTo(historicalTimestamp.toString());
        assertThat(Boolean.parseBoolean(overrideFlagElement.getText())).isTrue();
    }

    @Test
    public void testFetchCurrencyRateWithFutureTimestamp() {
        // Arrange
        String currencyCode = "USD";
        Instant futureTimestamp = Instant.now().plus(10, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);

        when(currencyConvertionController.getCurrencyRate(eq(currencyCode), eq(futureTimestamp.toString())))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "No data for future timestamp"));

        // Act
        driver.get(baseUrl + "?currencyCode=" + currencyCode + "&timestamp=" + futureTimestamp.toString());

        // Assert
        WebElement errorElement = driver.findElement(By.id("error"));
        assertThat(errorElement.getText()).contains("No data for future timestamp");
    }

    @Test
    public void testFetchCurrencyRateWithInvalidCurrencyCode() {
        // Arrange
        String invalidCurrencyCode = "XYZ";

        when(currencyConvertionController.getCurrencyRate(eq(invalidCurrencyCode), eq(null)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid currency code"));

        // Act
        driver.get(baseUrl + "?currencyCode=" + invalidCurrencyCode);

        // Assert
        WebElement errorElement = driver.findElement(By.id("error"));
        assertThat(errorElement.getText()).contains("Invalid currency code");
    }

    @org.junit.jupiter.api.AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
