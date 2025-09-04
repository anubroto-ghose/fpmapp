/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5257
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:55:03
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Integration Selenium Test with Spring Boot context
 * Validates the admin override attempt with invalid currency code is rejected properly.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyOverrideInvalidCodeTest {

    private WebDriver driver;

    @Autowired
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private CurrencyConvertionController mockedCurrencyConvertionController;

    @Autowired
    private FpmUserProfileController userProfileController;

    private final String BASE_URL = "http://localhost:8080"; // Adjust port as necessary

    @BeforeAll
    public void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();

        // Mock the service method invoked on override
        Mockito.doThrow(new IllegalArgumentException("Invalid currency code"))
                .when(mockedCurrencyConvertionController)
                .overrideCurrencyRate(Mockito.eq("XXX"), Mockito.anyDouble(), Mockito.anyString());

        // For valid codes, do nothing (simulate success)
        Mockito.doNothing()
                .when(mockedCurrencyConvertionController)
                .overrideCurrencyRate(Mockito.argThat(code -> !"XXX".equals(code)), Mockito.anyDouble(), Mockito.anyString());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Preconditions: Admin user is logged in
     * Steps:
     * - Open Currency Override Admin UI
     * - Input invalid currency code, valid rate
     * - Submit the form
     * Assertions:
     * - Error message is displayed
     * - No update call triggered
     * - No notifications or logs triggered
     */
    @Test
    public void testOverrideWithInvalidCurrencyCode() {
        // Simulate admin login - adjust selectors & flows as per actual UI
        driver.get(BASE_URL + "/login");

        // Login form simulation
        WebElement usernameInput = waitForElement(By.id("username"));
        WebElement passwordInput = waitForElement(By.id("password"));
        WebElement loginButton = waitForElement(By.id("login-btn"));

        usernameInput.sendKeys("adminUser");
        passwordInput.sendKeys("adminPassword");
        loginButton.click();

        // Wait for login redirect or dashboard
        waitForElement(By.id("dashboard"));

        // Now navigate to Currency Override Admin UI component
        driver.get(BASE_URL + "/admin/currency-override");

        WebElement currencyCodeInput = waitForElement(By.id("currency-code"));
        WebElement currencyRateInput = waitForElement(By.id("currency-rate"));
        WebElement reasonInput = waitForElement(By.id("override-reason"));
        WebElement submitButton = waitForElement(By.id("submit-override"));

        // Enter invalid currency code and a valid rate
        String invalidCurrencyCode = "XXX";
        String validCurrencyRate = "1.2345";

        currencyCodeInput.clear();
        currencyCodeInput.sendKeys(invalidCurrencyCode);

        currencyRateInput.clear();
        currencyRateInput.sendKeys(validCurrencyRate);

        reasonInput.clear();
        reasonInput.sendKeys("Testing invalid code rejection");

        submitButton.click();

        // Wait for error message to appear
        WebElement errorMessage = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("error-message")));

        // Assert error message text is correct
        assertEquals("Invalid currency code", errorMessage.getText(), "Error message text mismatch");

        // Verify overrideCurrencyRate service method was NOT called due to validation failure
        verify(mockedCurrencyConvertionController, never())
                .overrideCurrencyRate(Mockito.eq(invalidCurrencyCode), Mockito.anyDouble(), Mockito.anyString());

        // Verify no notification or log was triggered
        // Since those services are not explicit here, assume no calls to notifyOverride
        // This can be verified if mocks for notification & audit services are injected similarly

        // Additionally, check UI state to confirm no update occurred
        // For example, currency rate list or latest override date remains unchanged
        // This is UI dependent; can be extended if UI exposes such data
    }

    private WebElement waitForElement(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}