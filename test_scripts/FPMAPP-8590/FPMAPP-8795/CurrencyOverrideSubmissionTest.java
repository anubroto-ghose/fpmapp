/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8795
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:05:16
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.dto.FpmUserProfileController;

/**
 * Integration Selenium test for currency override submission without reason.
 * 
 * Preconditions:
 * - Administrator logged in with override permissions.
 * - CurrencyOverridePanel UI accessible.
 * 
 * Validates that submission without reason is prevented with proper UI feedback,
 * no logs are created, and no alerts triggered.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyOverrideSubmissionTest {

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
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user profile to simulate admin with override permissions
        when(userProfileController.isUserLoggedIn()).thenReturn(true);
        when(userProfileController.hasOverridePermission()).thenReturn(true);

        // Mock currencyConvertionController to verify no override logs or alerts are triggered
        // We do not expect any calls to logOverride or triggerAlert
    }

    @Test
    public void testOverrideSubmissionWithoutReasonShowsValidationError() {
        // Step 1: Navigate to CurrencyOverridePanel
        driver.get("http://localhost:8080/currency-override-panel");

        // Wait for page to load and verify presence of form elements
        WebElement currencyPairInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyPair")));
        WebElement exchangeRateInput = driver.findElement(By.id("newExchangeRate"));
        WebElement reasonInput = driver.findElement(By.id("overrideReason"));
        WebElement submitButton = driver.findElement(By.id("submitOverride"));

        // Step 2: Enter valid currency pair and new exchange rate
        currencyPairInput.clear();
        currencyPairInput.sendKeys("USD/EUR");

        exchangeRateInput.clear();
        exchangeRateInput.sendKeys("0.85");

        // Step 3: Leave reason field empty
        reasonInput.clear();

        // Step 4: Attempt to submit the override
        submitButton.click();

        // Expected Results:
        // - Validation error displayed indicating reason is mandatory
        WebElement validationError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reasonError")));
        String errorText = validationError.getText();
        assertThat(errorText).containsIgnoringCase("reason is mandatory");

        // - No override is logged in Currency_Override_Logs table
        // Verify currencyConvertionController.logOverride() is never called
        verify(currencyConvertionController, never()).logOverride(any(), any(), any(), any());

        // - No alert is triggered
        verify(currencyConvertionController, never()).triggerOverrideAlert(any());

        // - UI provides real-time feedback (already checked by presence of validation error)

        // Additional: Ensure submit button is still enabled (user can correct input)
        assertThat(submitButton.isEnabled()).isTrue();
    }
}
