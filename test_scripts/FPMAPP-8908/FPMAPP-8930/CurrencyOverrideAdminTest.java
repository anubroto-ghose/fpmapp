/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8930
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:59:40
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyOverrideAdminTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController userProfileController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
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
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock admin user login
        doReturn(true).when(userProfileController).isUserAdmin(any());

        // Mock existing currency rates
        doReturn(Map.of("USD", 1.0, "EUR", 0.85)).when(currencyConvertionController).getCurrentRates();

        // Mock audit log creation
        doReturn(true).when(currencyConvertionController).logAuditEntry(any(), any(), any(), any(), any());

        // Mock alert sending
        doReturn(true).when(currencyConvertionController).sendOverrideAlert(any(), any(), any());
    }

    @Test
    public void testAdminOverrideCurrencyRate_Success() {
        // Navigate to login page
        driver.get("http://localhost:8080/login");

        // Login as admin user
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("adminUser");
        passwordInput.sendKeys("adminPass123");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to currency override page
        driver.get("http://localhost:8080/admin/currency-override");

        // Wait for currency override form
        WebElement currencySelect = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencySelect")));
        WebElement rateInput = driver.findElement(By.id("rateInput"));
        WebElement overrideButton = driver.findElement(By.id("overrideBtn"));

        // Select currency EUR
        currencySelect.click();
        WebElement eurOption = driver.findElement(By.xpath("//option[@value='EUR']"));
        eurOption.click();

        // Override rate to 0.90
        rateInput.clear();
        rateInput.sendKeys("0.90");

        // Click override
        overrideButton.click();

        // Verify success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        assertThat(successMsg.getText()).contains("Override saved successfully");

        // Verify the override is reflected immediately
        WebElement currentRateDisplay = driver.findElement(By.id("currentRate"));
        assertThat(currentRateDisplay.getText()).isEqualTo("0.90");

        // Verify audit log entry creation
        verify(currencyConvertionController, times(1)).logAuditEntry(
                "adminUser",
                "EUR",
                0.85,
                0.90,
                any(LocalDateTime.class));

        // Verify alert sent
        verify(currencyConvertionController, times(1)).sendOverrideAlert(
                "adminUser",
                "EUR",
                0.90);
    }

    @Test
    public void testAdminOverrideCurrencyRate_InvalidNegativeRate() {
        // Navigate to login page
        driver.get("http://localhost:8080/login");

        // Login as admin user
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("adminUser");
        passwordInput.sendKeys("adminPass123");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to currency override page
        driver.get("http://localhost:8080/admin/currency-override");

        // Wait for currency override form
        WebElement currencySelect = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencySelect")));
        WebElement rateInput = driver.findElement(By.id("rateInput"));
        WebElement overrideButton = driver.findElement(By.id("overrideBtn"));

        // Select currency USD
        currencySelect.click();
        WebElement usdOption = driver.findElement(By.xpath("//option[@value='USD']"));
        usdOption.click();

        // Enter invalid negative rate
        rateInput.clear();
        rateInput.sendKeys("-1.00");

        // Click override
        overrideButton.click();

        // Verify error message
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        assertThat(errorMsg.getText()).contains("Invalid rate: must be positive");

        // Verify override NOT saved
        WebElement currentRateDisplay = driver.findElement(By.id("currentRate"));
        assertThat(currentRateDisplay.getText()).isEqualTo("1.0"); // original USD rate

        // Verify audit log NOT created
        verify(currencyConvertionController, times(0)).logAuditEntry(any(), any(), any(), any(), any());

        // Verify alert NOT sent
        verify(currencyConvertionController, times(0)).sendOverrideAlert(any(), any(), any());
    }
}
