/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8882
 * Epic: FPMAPP-8837
 * Generated on: 2026-03-27 14:36:12
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
import org.mockito.ArgumentCaptor;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration test for validating admin override of exchange rates with logging and alerting.
 * 
 * Preconditions:
 * - Admin user logged in with override permissions.
 * - Exchange rates present in DB.
 * - Alerting and audit logging operational.
 * 
 * This test uses Selenium WebDriver to simulate UI interaction and mocks backend services to verify behavior.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyExchangeOverrideIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String ADMIN_USERNAME = "adminUser";
    private static final String ADMIN_PASSWORD = "adminPass123";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
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
    public void setupMocks() {
        // Mock admin user profile with override permissions
        doReturn(true).when(fpmUserProfileController).hasOverridePermission(ADMIN_USERNAME);

        // Mock existing exchange rates
        Map<String, Double> existingRates = Collections.singletonMap("USD_EUR", 0.85);
        doReturn(existingRates).when(currencyConvertionController).getCurrentExchangeRates();

        // Mock alerting system operational
        doReturn(true).when(fpmCommonController).isAlertingSystemOperational();

        // Mock audit logging operational
        doReturn(true).when(fpmCommonController).isAuditLoggingOperational();

        // Mock saving override returns success
        doReturn(true).when(currencyConvertionController).saveOverride(any(String.class), any(Double.class), any(String.class));

        // Mock fetching overridden rate
        doReturn(0.90).when(currencyConvertionController).getExchangeRate("USD", "EUR");
    }

    @Test
    public void testAdminOverrideExchangeRateWithLoggingAndAlerting() {
        // Step 1: Admin logs in
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(ADMIN_USERNAME);
        passwordInput.sendKeys(ADMIN_PASSWORD);
        loginButton.click();

        // Verify login success by presence of dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertThat(driver.getCurrentUrl()).contains("/dashboard");

        // Step 2: Navigate to currency exchange override interface
        WebElement navMenu = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-currency-exchange")));
        navMenu.click();

        wait.until(ExpectedConditions.urlContains("/currency-exchange/override"));

        // Step 3: Select currency pair USD/EUR
        WebElement currencyPairSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("currencyPairSelect")));
        currencyPairSelect.click();
        WebElement usdEurOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='USD_EUR']")));
        usdEurOption.click();

        // Step 4: Change exchange rate to 0.90
        WebElement rateInput = driver.findElement(By.id("exchangeRateInput"));
        rateInput.clear();
        rateInput.sendKeys("0.90");

        // Step 5: Save override
        WebElement saveButton = driver.findElement(By.id("saveOverrideBtn"));
        saveButton.click();

        // Wait for success notification
        WebElement successAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overrideSuccessAlert")));
        assertThat(successAlert.getText()).contains("Override saved successfully");

        // Verify backend saveOverride called with correct params
        ArgumentCaptor<String> currencyPairCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> rateCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);

        verify(currencyConvertionController, times(1)).saveOverride(currencyPairCaptor.capture(), rateCaptor.capture(), userCaptor.capture());

        assertThat(currencyPairCaptor.getValue()).isEqualTo("USD_EUR");
        assertThat(rateCaptor.getValue()).isEqualTo(0.90);
        assertThat(userCaptor.getValue()).isEqualTo(ADMIN_USERNAME);

        // Step 6: Verify audit log entry
        verify(fpmCommonController, times(1)).logAuditEntry(
                "Currency override",
                "User '" + ADMIN_USERNAME + "' overrode USD_EUR rate to 0.90",
                LocalDateTime.now());

        // Step 7: Verify alert generated and visible
        verify(fpmCommonController, times(1)).triggerAlert(
                "Currency override",
                "USD_EUR exchange rate overridden by user '" + ADMIN_USERNAME + "' to 0.90");

        // Step 8: Verify subsequent API call returns overridden rate
        double overriddenRate = currencyConvertionController.getExchangeRate("USD", "EUR");
        assertThat(overriddenRate).isEqualTo(0.90);

        // Step 9: Verify no system errors on page
        boolean errorVisible = driver.findElements(By.className("error-message")).size() > 0;
        assertThat(errorVisible).isFalse();
    }
}
