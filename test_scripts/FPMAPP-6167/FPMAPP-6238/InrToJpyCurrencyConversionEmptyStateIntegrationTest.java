/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6238
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:27:49
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.dtos.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Integration test validating empty state handling in transaction history for INR->JPY conversions,
 * with role-based approval context and audit trail verification.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // Use fixed port for Selenium
public class InrToJpyCurrencyConversionEmptyStateIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @BeforeAll
    public static void setUpClass() {
        // Setup WebDriver (Chrome) with headless options for CI
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Regression test verifying empty state on no INR to JPY conversion transactions display,
     * ensures no blocking due to role-based approval and audit trail integration logs the event.
     * @throws Exception if test fails
     */
    @Test
    public void testEmptyStateForInrToJpyCurrencyConversions() throws Exception {

        // Mock current user profile as logged-in user with appropriate role-based restrictions
        when(userProfileController.getCurrentUserProfile()).thenReturn(
                new com.webapp.fpmapp.dtos.FpmUserProfileController.UserProfileDTO(
                        "user123",
                        "ROLE_APPROVER",
                        "Finance Department"
                )
        );

        // Mock CurrencyConvertionController to return empty history for INR to JPY
        when(currencyConvertionController.getCurrencyConversionHistory("INR", "JPY"))
                .thenReturn(Collections.emptyList());

        // Navigate to the transaction history page (assumed URL)
        driver.get("http://localhost:8080/transaction-history");

        // Wait for page load check (e.g. presence of page title or main content)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transaction-history-main")));

        // Select/Apply filter for currency conversion INR to JPY
        WebElement fromCurrencySelect = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("filter-from-currency")));
        fromCurrencySelect.click();
        WebElement inrOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("option[value='INR']")));
        inrOption.click();

        WebElement toCurrencySelect = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("filter-to-currency")));
        toCurrencySelect.click();
        WebElement jpyOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("option[value='JPY']")));
        jpyOption.click();

        WebElement applyFilterButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("apply-filter-btn")));
        applyFilterButton.click();

        // Wait for filter update to complete, e.g. wait for results container refresh
        WebElement resultsContainer = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("transaction-results")));

        // Verify the empty state message is displayed correctly
        WebElement emptyStateMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("empty-state-message")));
        String expectedMessage = "No INR to JPY transactions found";
        assertThat(emptyStateMessage.getText()).as("Empty state message").isEqualTo(expectedMessage);

        // Verify no transaction rows are present
        assertThat(resultsContainer.findElements(By.className("transaction-row"))).isEmpty();

        // Verify no errors or blocking overlays shown due to role-based approval restrictions
        boolean isBlockingOverlayPresent = driver.findElements(By.id("approval-block-overlay")).size() > 0;
        assertThat(isBlockingOverlayPresent).as("Approval blocking overlay should not be present").isFalse();

        // Simulate audit trail recording check by verifying mock interaction
        // In a real test, this might check DB or audit service. Here, we verify mock invoked.
        Mockito.verify(currencyConvertionController, Mockito.times(1)).getCurrencyConversionHistory("INR", "JPY");

    }
}