/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8795
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:32:46
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.services.CurrencySyncService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyOverridePanelOverrideWithoutReasonTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private CurrencySyncService currencySyncService;

    @Autowired
    private com.webapp.fpmapp.controllers.FpmUserProfileController userProfileController;

    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    public void setUp() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run headless for CI
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock the currencySyncService to verify no override is processed
        Mockito.reset(currencySyncService);

        // Simulate administrator login with override permissions
        driver.get(baseUrl + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("adminUser");
        passwordInput.sendKeys("adminPassword");
        loginButton.click();

        // Wait for login to complete and dashboard to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Verify user has override permissions (mock or real check)
        // For this test, assume user has permissions
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testOverrideSubmissionWithoutReasonShowsValidationError() {
        // Navigate to CurrencyOverridePanel
        driver.get(baseUrl + "/currency/override-panel");

        // Wait for panel to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyOverrideForm")));

        // Enter valid currency pair
        WebElement currencyPairInput = driver.findElement(By.id("currencyPair"));
        currencyPairInput.clear();
        currencyPairInput.sendKeys("USD/EUR");

        // Enter new exchange rate
        WebElement exchangeRateInput = driver.findElement(By.id("newExchangeRate"));
        exchangeRateInput.clear();
        exchangeRateInput.sendKeys("1.15");

        // Leave reason field empty
        WebElement reasonInput = driver.findElement(By.id("overrideReason"));
        reasonInput.clear();

        // Submit the override
        WebElement submitButton = driver.findElement(By.id("submitOverride"));
        submitButton.click();

        // Verify validation error is displayed
        WebElement validationError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reasonError")));
        String errorText = validationError.getText();
        assertTrue(errorText.toLowerCase().contains("reason is mandatory") || errorText.toLowerCase().contains("required"),
                "Expected validation error about missing reason, but got: " + errorText);

        // Verify no override is logged - verify currencySyncService.processOverride is NOT called
        Mockito.verify(currencySyncService, Mockito.never()).processOverride(Mockito.any());

        // Verify no alert is triggered - assuming alerting is part of processOverride or separate method
        Mockito.verify(currencySyncService, Mockito.never()).sendOverrideAlert(Mockito.any());

        // Verify UI provides real-time feedback (already checked by presence of validation error)
    }
}
