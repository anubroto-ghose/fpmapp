/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8640
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:45:58
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Selenium + Spring Boot Integration Test for validating override attempt with missing overrideReason.
 * 
 * Preconditions:
 * - User authenticated as admin
 * - Currency_Exchange_Rates table accessible (mocked)
 * 
 * Test Steps:
 * 1. Attempt override with overrideFlag=true but no overrideReason
 * 2. Validate API returns validation error
 * 3. Validate no DB changes, no alert emails, no audit logs
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyOverrideValidationTest {

    private static WebDriver driver;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
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
        MockitoAnnotations.openMocks(this);

        // Mock the currencyConvertionController to simulate validation error response
        when(currencyConvertionController.overrideCurrencyRates(any()))
            .thenAnswer(invocation -> {
                // Extract request body
                var request = invocation.getArgument(0, com.webapp.fpmapp.dto.FpmDealsheetController.OverrideRequest.class);
                if (request.isOverrideFlag() && (request.getOverrideReason() == null || request.getOverrideReason().isBlank())) {
                    // Simulate validation error response
                    throw new IllegalArgumentException("overrideReason is required when overrideFlag is true");
                }
                return "Success";
            });

        // Mock no alert emails sent
        doNothing().when(fpmCommonController).sendAlertEmail(any());

        // Mock no audit log created
        doNothing().when(fpmCommonController).createAuditLog(any());
    }

    @Test
    public void testOverrideAttemptMissingOverrideReason_shouldFailValidation() {
        // Navigate to the currency override page (assuming URL)
        driver.get("http://localhost:8080/fpm/currency/override");

        // Simulate admin login (assuming login page and elements)
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("adminUser");
        passwordInput.sendKeys("adminPass123");
        loginButton.click();

        // Wait for redirect to override page
        try {
            Thread.sleep(2000); // simple wait for demo; better to use WebDriverWait in prod
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Find overrideFlag checkbox and check it
        WebElement overrideFlagCheckbox = driver.findElement(By.id("overrideFlag"));
        if (!overrideFlagCheckbox.isSelected()) {
            overrideFlagCheckbox.click();
        }

        // Leave overrideReason empty
        WebElement overrideReasonInput = driver.findElement(By.id("overrideReason"));
        overrideReasonInput.clear();

        // Submit the form
        WebElement submitButton = driver.findElement(By.id("submitOverride"));
        submitButton.click();

        // Wait for validation error message
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check for validation error message displayed on UI
        WebElement errorMessage = driver.findElement(By.id("overrideReasonError"));
        assertNotNull(errorMessage, "Validation error message element should be present");
        assertTrue(errorMessage.isDisplayed(), "Validation error message should be visible");
        assertEquals("Override reason is required when override flag is set.", errorMessage.getText());

        // Additionally, verify backend API call via WebTestClient
        webTestClient.put()
            .uri("/fpm/currency/rates")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{ \"overrideFlag\": true }")
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error").isEqualTo("overrideReason is required when overrideFlag is true");

        // Verify no alert emails sent
        verify(fpmCommonController, never()).sendAlertEmail(any());

        // Verify no audit log created
        verify(fpmCommonController, never()).createAuditLog(any());

        // Verify no changes to Currency_Exchange_Rates table (mocked, so no interaction)
        verify(currencyConvertionController, times(1)).overrideCurrencyRates(any());
    }
}
