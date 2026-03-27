/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8794
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 08:05:50
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;

import java.time.Duration;

/**
 * Integration Selenium test for currency override submission by admin user.
 * 
 * Preconditions:
 * - Admin user logged in with override permissions
 * - CurrencyOverridePanel UI accessible
 * 
 * Test Steps:
 * 1. Navigate to CurrencyOverridePanel
 * 2. Enter valid currency pair and new exchange rate
 * 3. Provide mandatory reason
 * 4. Submit override
 * 5. Verify override accepted with feedback
 * 6. Verify override logged in DB
 * 7. Verify alert notification sent
 * 8. Verify override retrievable via API
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyOverrideAdminTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private final String baseUrl = "http://localhost:8080";

    private final String adminUsername = "adminUser";
    private final String adminPassword = "adminPass123";

    @BeforeEach
    public void setUp() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock user profile to simulate logged-in admin user with override permissions
        User adminUser = new User();
        adminUser.setUsername(adminUsername);
        adminUser.setRole("ADMIN");
        adminUser.setOverridePermission(true);
        when(userProfileController.getCurrentUser()).thenReturn(adminUser);

        // Mock alert sending to verify alert triggered
        when(fpmCommonController.sendOverrideAlert(any())).thenReturn(true);

        // Mock DB logging to verify override logged
        when(currencyConvertionController.logCurrencyOverride(any(), any(), any(), any(), any())).thenReturn(true);

        // Mock API retrieval of override
        when(currencyConvertionController.getLatestOverrideForPair("USD", "EUR"))
            .thenReturn(Optional.of(new com.webapp.fpmapp.dto.CurrencyOverrideDTO(
                "USD", "EUR", 1.15, "Admin override for testing", adminUsername, LocalDateTime.now())));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testSubmitCurrencyOverrideWithValidReason() {
        // Step 1: Navigate to CurrencyOverridePanel
        driver.get(baseUrl + "/currency-override-panel");

        // Wait for panel to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyOverrideForm")));

        // Step 2: Enter valid currency pair and new exchange rate
        WebElement fromCurrencyInput = driver.findElement(By.id("fromCurrency"));
        WebElement toCurrencyInput = driver.findElement(By.id("toCurrency"));
        WebElement exchangeRateInput = driver.findElement(By.id("exchangeRate"));

        fromCurrencyInput.clear();
        fromCurrencyInput.sendKeys("USD");

        toCurrencyInput.clear();
        toCurrencyInput.sendKeys("EUR");

        exchangeRateInput.clear();
        exchangeRateInput.sendKeys("1.15");

        // Step 3: Provide mandatory reason for override
        WebElement reasonInput = driver.findElement(By.id("overrideReason"));
        reasonInput.clear();
        reasonInput.sendKeys("Admin override for testing");

        // Step 4: Submit the override
        WebElement submitButton = driver.findElement(By.id("submitOverride"));
        submitButton.click();

        // Step 5: Verify override accepted with real-time feedback
        WebElement feedbackMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("feedbackMessage")));
        String feedbackText = feedbackMessage.getText();
        assertThat(feedbackText).containsIgnoringCase("Override submitted successfully");

        // Step 6: Verify override logged in DB
        verify(currencyConvertionController, times(1))
            .logCurrencyOverride("USD", "EUR", 1.15, "Admin override for testing", adminUsername);

        // Step 7: Verify alert notification sent
        verify(fpmCommonController, times(1)).sendOverrideAlert(any());

        // Step 8: Verify override retrievable via API with correct data
        Optional<com.webapp.fpmapp.dto.CurrencyOverrideDTO> overrideOpt = currencyConvertionController.getLatestOverrideForPair("USD", "EUR");
        assertThat(overrideOpt).isPresent();
        com.webapp.fpmapp.dto.CurrencyOverrideDTO override = overrideOpt.get();
        assertThat(override.getFromCurrency()).isEqualTo("USD");
        assertThat(override.getToCurrency()).isEqualTo("EUR");
        assertThat(override.getNewExchangeRate()).isEqualTo(1.15);
        assertThat(override.getReason()).isEqualTo("Admin override for testing");
        assertThat(override.getOverriddenBy()).isEqualTo(adminUsername);
        assertThat(override.getTimestamp()).isNotNull();
    }
}
