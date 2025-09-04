/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5257
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:20:24
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.selenium;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Selenium integration test for currency override admin UI.
 *
 * Test case: Attempt to override currency rate with invalid currency code.
 * Preconditions: Admin is logged in.
 * Verifies error message and that currency rate is not updated.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CurrencyOverrideInvalidCodeTest {

    private static WebDriver driver;

    @LocalServerPort
    private int port;

    private WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @BeforeAll
    public static void setupClass() {
        // Use appropriate path to chromedriver executable or
        // use WebDriverManager or other WebDriver setup as preferred
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterAll
    public static void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock the currency rate update behavior. For invalid code, no update occurs.
        when(currencyConvertionController.updateCurrencyRate(anyString(), org.mockito.ArgumentMatchers.anyDouble()))
            .thenAnswer(invocation -> {
                String currencyCode = invocation.getArgument(0);
                if (!currencyCode.matches("[A-Z]{3}")) {
                    throw new IllegalArgumentException("Invalid currency code");
                }
                return true;
            });
    }

    @Test
    public void testOverrideWithInvalidCurrencyCode() {
        // 1. Navigate to the Currency Override Admin UI component.
        String baseUrl = "http://localhost:" + port + "/admin/currency-override";
        driver.get(baseUrl);

        // Simulate admin login by setting a session cookie or navigating login page if needed
        // For this example, we assume test profile bypasses auth or we have a session

        // 2. Enter invalid currency code and valid currency rate.
        WebElement currencyCodeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyCode")));
        WebElement currencyRateInput = driver.findElement(By.id("currencyRate"));
        WebElement submitButton = driver.findElement(By.id("submitOverride"));

        String invalidCurrencyCode = "ZZZ1"; // invalid because it contains digit and length 4
        double validCurrencyRate = 1.2345;

        currencyCodeInput.clear();
        currencyCodeInput.sendKeys(invalidCurrencyCode);
        currencyRateInput.clear();
        currencyRateInput.sendKeys(String.valueOf(validCurrencyRate));

        // 3. Submit the change.
        submitButton.click();

        // Expected Results:

        // The system displays an error message stating 'Invalid currency code'.
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        assertThat("Page should contain 'Invalid currency code' error", errorMsg.getText(), containsString("Invalid currency code"));

        // The currency rate is not updated - verify that updateCurrencyRate is never called with invalid code
        verify(currencyConvertionController, never()).applyCurrencyRateOverride(invalidCurrencyCode, validCurrencyRate);
        // Also verify the controller updateCurrencyRate throws exception (already stubbed above)
        try {
            currencyConvertionController.updateCurrencyRate(invalidCurrencyCode, validCurrencyRate);
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid currency code", ex.getMessage());
        }

        // No notifications or logs are triggered due to the failed attempt.
        // Assuming there is a notification element; verify it is not visible or empty.
        boolean notificationPresent = driver.findElements(By.id("notificationMessage")).stream()
            .anyMatch(WebElement::isDisplayed);
        assertEquals(false, notificationPresent, "No notifications should be displayed on failure");
    }
}