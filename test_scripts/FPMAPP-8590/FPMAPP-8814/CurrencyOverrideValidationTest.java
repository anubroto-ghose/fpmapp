/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8814
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:53:03
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

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Integration Selenium test for currency override submission validation.
 * 
 * Preconditions:
 * - User logged in as authorized administrator.
 * - API endpoint POST /api/currency/override accessible.
 * 
 * Test verifies that submitting override without reason is rejected,
 * no logs or alerts are generated, and currency data remains unchanged.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyOverrideValidationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private CurrencyConvertionController mockCurrencyConvertionController;

    @MockBean
    private FpmUserProfileController userProfileController;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
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
        // Mock user profile to simulate logged-in admin user
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("adminUser");
        adminUser.setRoles(Collections.singletonList("ROLE_ADMIN"));
        when(userProfileController.getCurrentUser()).thenReturn(adminUser);

        // Mock currency override logs count before test
        when(mockCurrencyConvertionController.getOverrideLogCount()).thenReturn(0L);

        // Mock alert generation check
        when(mockCurrencyConvertionController.isAlertGeneratedForLastOverride()).thenReturn(false);
    }

    /**
     * Test submitting currency override without reason field.
     * 
     * Steps:
     * 1. Login as admin (mocked).
     * 2. Submit override request via UI form with valid exchange rate but missing reason.
     * 3. Verify validation error is shown.
     * 4. Verify no override log entry created.
     * 5. Verify no alert generated.
     * 6. Verify currency data unchanged.
     */
    @Test
    public void testOverrideSubmissionFailsWithoutReason() {
        // Navigate to currency override page
        driver.get(BASE_URL + "/currency/override");

        // Wait for page to load and form to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overrideForm")));

        // Fill in exchange rate field with valid value
        WebElement exchangeRateInput = driver.findElement(By.id("exchangeRate"));
        exchangeRateInput.clear();
        exchangeRateInput.sendKeys("1.25");

        // Leave reason field empty intentionally
        WebElement reasonInput = driver.findElement(By.id("reason"));
        reasonInput.clear();

        // Submit the form
        WebElement submitButton = driver.findElement(By.id("submitOverride"));
        submitButton.click();

        // Wait for validation error message
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reasonError")));

        // Assert validation error message text
        assertThat(errorMsg.getText()).contains("Reason is required");

        // Verify no override log entry created
        verify(mockCurrencyConvertionController, never()).logCurrencyOverride(any());

        // Verify no alert generated
        verify(mockCurrencyConvertionController, never()).generateAlertForOverride(any());

        // Verify currency data remains unchanged
        double currentRate = currencyConvertionController.getCurrentExchangeRate("USD", "EUR");
        assertThat(currentRate).isNotEqualTo(1.25); // Assuming 1.25 is new override, so unchanged means not updated
    }

}
