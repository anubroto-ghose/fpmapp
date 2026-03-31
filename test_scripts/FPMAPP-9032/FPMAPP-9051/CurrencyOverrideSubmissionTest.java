/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-9051
 * Epic: FPMAPP-9032
 * Generated on: 2026-03-31 16:49:38
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test for currency override submission.
 * 
 * Preconditions:
 * - User logged in as finance admin with override submission permissions.
 * - CurrencyOverrideAdminPanel component loaded.
 * - Backend override API mocked and responsive.
 * 
 * Test Steps:
 * 1. Navigate to override submission section.
 * 2. Enter valid currency code and override rate.
 * 3. Submit override request.
 * 4. Verify UI confirmation.
 * 5. Verify backend API call.
 * 6. Verify alert triggered and audit log created.
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
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

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
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user profile to simulate finance admin with override permissions
        when(fpmUserProfileController.hasOverrideSubmissionPermission()).thenReturn(true);

        // Mock backend override API to accept override request
        when(currencyConvertionController.submitCurrencyOverride(any()))
            .thenReturn(new ResponseEntity<>("Override Accepted", HttpStatus.OK));

        // Mock alert trigger
        when(fpmCommonController.triggerAlert(any())).thenReturn(true);

        // Mock audit log creation
        when(fpmCommonController.createAuditLog(any(), any(), any())).thenReturn(true);
    }

    @Test
    public void testSubmitCurrencyOverrideRequestAndVerifyBackendIntegration() {
        // Step 1: Navigate to the override submission section in the UI
        driver.get("http://localhost:8080/currency-override-admin");

        // Wait for the CurrencyOverrideAdminPanel component to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyOverrideForm")));

        // Step 2: Enter a valid currency code and override rate
        WebElement currencyCodeInput = driver.findElement(By.id("currencyCodeInput"));
        WebElement overrideRateInput = driver.findElement(By.id("overrideRateInput"));

        String testCurrencyCode = "USD";
        String testOverrideRate = "1.15";

        currencyCodeInput.clear();
        currencyCodeInput.sendKeys(testCurrencyCode);

        overrideRateInput.clear();
        overrideRateInput.sendKeys(testOverrideRate);

        // Step 3: Submit the override request
        WebElement submitButton = driver.findElement(By.id("submitOverrideBtn"));
        submitButton.click();

        // Step 4: Monitor the UI for confirmation of submission
        WebElement confirmationMessage = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("overrideSubmissionConfirmation")));

        assertThat(confirmationMessage.getText())
            .as("Check confirmation message text")
            .containsIgnoringCase("Override request submitted successfully");

        // Step 5: Verify that the backend API receives the override request
        ArgumentCaptor<com.webapp.fpmapp.dto.CurrencyOverrideRequest> captor = ArgumentCaptor.forClass(com.webapp.fpmapp.dto.CurrencyOverrideRequest.class);
        verify(currencyConvertionController).submitCurrencyOverride(captor.capture());

        com.webapp.fpmapp.dto.CurrencyOverrideRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest).isNotNull();
        assertThat(capturedRequest.getCurrencyCode()).isEqualTo(testCurrencyCode);
        assertThat(capturedRequest.getOverrideRate()).isEqualTo(Double.parseDouble(testOverrideRate));

        // Step 6: Check that an alert is triggered and audit log entry is created
        verify(fpmCommonController).triggerAlert("Currency override submitted for " + testCurrencyCode);

        verify(fpmCommonController).createAuditLog(
            "CurrencyOverride",
            "Override submitted by finance admin",
            LocalDateTime.now());
    }
}
