/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5256
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:20:52
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Integration test using Selenium WebDriver for admin currency rate override scenario.
 * Preconditions: Admin is logged into the system.
 * 
 * Validates:
 * - Successful update of currency rate
 * - Alert triggered after override
 * - Logging action in the system
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyRateOverrideAdminTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @BeforeAll
    public void setUpClass() {
        // Set the path to your WebDriver executable if needed, e.g.: 
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
    }

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Mock the service response for currency update
        Mockito.when(currencyConvertionController.overrideCurrencyRate(eq("USD"), eq(1.25)))
               .thenReturn(true);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testCurrencyRateOverrideByAdmin() {
        try {
            // 1. Login as Admin
            driver.get(BASE_URL + "/login");

            WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement password = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginBtn"));

            username.sendKeys("adminUser");
            password.sendKeys("SecureP@ssw0rd");
            loginButton.click();

            // Verify successful login by presence of admin dashboard
            wait.until(ExpectedConditions.urlContains("/admin/dashboard"));
            assertTrue(driver.getCurrentUrl().contains("/admin/dashboard"), "Admin should be redirected to dashboard after login");

            // 2. Navigate to currency rate management
            WebElement currencyMenu = wait.until(ExpectedConditions.elementToBeClickable(By.id("menu_currency_rate")));
            currencyMenu.click();

            wait.until(ExpectedConditions.urlContains("/admin/currency-rates"));

            // 3. Select currency USD and enter new rate 1.25
            WebElement usdRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[text()='USD']]");
            WebElement rateInput = usdRow.findElement(By.cssSelector("input.currency-rate"));

            rateInput.clear();
            rateInput.sendKeys("1.25");

            // 4. Submit the changes
            WebElement submitBtn = driver.findElement(By.id("submitCurrencyRate"));
            submitBtn.click();

            // Validate that service was called with correct arguments
            verify(currencyConvertionController, times(1)).overrideCurrencyRate("USD", 1.25);

            // 5. Confirm alert popup for override
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            assertTrue(alertText.contains("Currency rate for USD overridden successfully"),
                    "Alert should confirm override action");
            alert.accept();

            // 6. Verify change logged in UI (e.g. a log panel or recent actions table)
            WebElement logSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("adminLogs")));
            boolean logFound = logSection.getText().contains("Currency rate for USD overridden by adminUser");
            assertTrue(logFound, "Override action should be logged in admin logs");

        } catch (Exception e) {
            fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }
}
