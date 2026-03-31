/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8954
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:37:19
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

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

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for verifying empty state handling when no INR to JPY conversions exist.
 * 
 * Preconditions:
 * - User is logged in with role-based approval enabled
 * - No previous INR to JPY transactions in their history
 * 
 * This test mocks the CurrencyConvertionController service to return empty transaction list for INR to JPY.
 * It verifies the UI displays the correct empty state message and no errors occur.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class InrToJpyConversionEmptyStateTest {

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
        // Mock user profile with role-based approval enabled
        when(userProfileController.isRoleBasedApprovalEnabled(anyString())).thenReturn(true);

        // Mock currency conversion transactions to return empty list for INR to JPY
        when(currencyConvertionController.getTransactions("INR", "JPY", "user123"))
                .thenReturn(Collections.emptyList());
    }

    @Test
    public void testEmptyStateForInrToJpyConversions() {
        // Simulate user login by navigating to login page and performing login
        driver.get("http://localhost:8080/login");

        // Enter username
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameInput.sendKeys("user123");

        // Enter password
        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys("password123");

        // Click login button
        WebElement loginButton = driver.findElement(By.id("loginButton"));
        loginButton.click();

        // Wait for navigation to dashboard/home page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to transaction history page
        driver.get("http://localhost:8080/transactions/history");

        // Wait for filter dropdowns to be visible
        WebElement fromCurrencyFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filter-from-currency")));
        WebElement toCurrencyFilter = driver.findElement(By.id("filter-to-currency"));

        // Select INR in from currency filter
        fromCurrencyFilter.click();
        WebElement inrOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='INR']")));
        inrOption.click();

        // Select JPY in to currency filter
        toCurrencyFilter.click();
        WebElement jpyOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='JPY']")));
        jpyOption.click();

        // Click filter/apply button
        WebElement applyFilterButton = driver.findElement(By.id("applyFilterButton"));
        applyFilterButton.click();

        // Wait for results or empty state message
        WebElement emptyStateMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("emptyStateMessage")));

        // Assert the empty state message text
        String expectedMessage = "No INR to JPY transactions found";
        assertThat(emptyStateMessage.getText()).isEqualToIgnoringCase(expectedMessage);

        // Assert no transaction rows are displayed
        List<WebElement> transactionRows = driver.findElements(By.cssSelector(".transaction-row"));
        assertThat(transactionRows).isEmpty();

        // Assert no error messages or alerts are present
        List<WebElement> errorAlerts = driver.findElements(By.cssSelector(".alert-error, .error-message"));
        assertThat(errorAlerts).isEmpty();

        // Assert UI consistency: check header and footer presence
        WebElement header = driver.findElement(By.tagName("header"));
        WebElement footer = driver.findElement(By.tagName("footer"));
        assertThat(header.isDisplayed()).isTrue();
        assertThat(footer.isDisplayed()).isTrue();
    }
}
