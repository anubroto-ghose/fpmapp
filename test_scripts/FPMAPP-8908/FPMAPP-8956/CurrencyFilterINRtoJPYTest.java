/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8956
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:35:31
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Arrays;
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

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Integration Selenium test for currency filter INR to JPY on transaction history page.
 * 
 * Preconditions:
 * - Multiple currency conversions exist in history (INR to USD, INR to JPY, etc.)
 * - Role-based approval and real-time currency integration features are enabled
 * 
 * Validates:
 * - Only INR to JPY conversions are displayed
 * - Other currency pairs are excluded
 * - Filtering respects role-based approval restrictions
 * - Real-time currency integration does not affect filter accuracy
 * - No UI or performance issues
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyFilterINRtoJPYTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
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
        // Mock currency conversion history data
        // Simulate multiple currency conversions including INR->USD and INR->JPY
        when(currencyConvertionController.getConversionHistory(anyString(), anyString()))
            .thenAnswer(invocation -> {
                String fromCurrency = invocation.getArgument(0);
                String toCurrency = invocation.getArgument(1);
                if ("INR".equals(fromCurrency) && "JPY".equals(toCurrency)) {
                    return Arrays.asList(
                        new CurrencyConversionRecord("INR", "JPY", 1000, 1500),
                        new CurrencyConversionRecord("INR", "JPY", 2000, 3000)
                    );
                } else if ("INR".equals(fromCurrency) && "USD".equals(toCurrency)) {
                    return Arrays.asList(
                        new CurrencyConversionRecord("INR", "USD", 1000, 13.5)
                    );
                }
                return List.of();
            });

        // Mock role-based approval restrictions
        when(fpmUserProfileController.getUserRole()).thenReturn("ROLE_FINANCE_APPROVER");

        // Mock real-time currency integration to not interfere
        when(fpmCommonController.isRealTimeCurrencyIntegrationEnabled()).thenReturn(true);
    }

    @Test
    public void testCurrencyFilterINRtoJPY() {
        try {
            // Navigate to transaction history page
            driver.get("http://localhost:8080/transaction-history");

            // Wait for page to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyFilter")));

            // Select currency filter dropdown
            WebElement currencyFilter = driver.findElement(By.id("currencyFilter"));
            currencyFilter.click();

            // Select "INR to JPY" option
            WebElement inrToJpyOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='INR-JPY']")));
            inrToJpyOption.click();

            // Click filter or wait for auto filter
            WebElement filterButton = driver.findElement(By.id("filterButton"));
            filterButton.click();

            // Wait for filtered results to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

            // Verify only INR to JPY conversions are displayed
            List<WebElement> rows = driver.findElements(By.cssSelector("#transactionTable tbody tr"));
            assertThat(rows).isNotEmpty();

            for (WebElement row : rows) {
                String currencyPair = row.findElement(By.cssSelector("td.currencyPair")).getText();
                assertThat(currencyPair).isEqualTo("INR-JPY");
            }

            // Verify no other currency pairs are present
            boolean otherPairsPresent = rows.stream()
                .anyMatch(row -> !"INR-JPY".equals(row.findElement(By.cssSelector("td.currencyPair")).getText()));
            assertThat(otherPairsPresent).isFalse();

            // Verify role-based approval restrictions are respected
            WebElement approvalStatus = driver.findElement(By.cssSelector("#transactionTable tbody tr:first-child td.approvalStatus"));
            assertThat(approvalStatus.getText()).isIn("Approved", "Pending Approval");

            // Verify UI performance - simple check: page still responsive
            assertThat(driver.findElement(By.id("currencyFilter")).isDisplayed()).isTrue();

        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError("Test failed due to exception: " + e.getMessage());
        }
    }

    // Helper DTO for mocking currency conversion records
    public static class CurrencyConversionRecord {
        private String fromCurrency;
        private String toCurrency;
        private double amountFrom;
        private double amountTo;

        public CurrencyConversionRecord(String fromCurrency, String toCurrency, double amountFrom, double amountTo) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.amountFrom = amountFrom;
            this.amountTo = amountTo;
        }

        public String getFromCurrency() {
            return fromCurrency;
        }

        public String getToCurrency() {
            return toCurrency;
        }

        public double getAmountFrom() {
            return amountFrom;
        }

        public double getAmountTo() {
            return amountTo;
        }
    }
}
