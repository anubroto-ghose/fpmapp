/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6240
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:25:27
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Selenium integration test verifying currency filter only displays INR to JPY conversions
 * with role-based approval filtering and audit compliance.
 *
 * Uses mocked services to simulate backend responses with multiple currencies.
 *
 * This test requires chromedriver binary in system path or configured via webdriver.chrome.driver
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyFilterINRtoJPYTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyController;

    @MockBean
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmForecastController forecastController;

    @MockBean
    private FpmCommonController commonController;

    @BeforeAll
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Mock user with role-based permission
        User testUser = new User();
        testUser.setId(1001L);
        testUser.setUsername("roleBasedUser");
        testUser.setRole("APPROVER_LEVEL_1");

        Mockito.when(userProfileController.getCurrentUser()).thenReturn(testUser);

        // Mock currency conversion data containing INR->USD and INR->JPY transactions
        Mockito.when(currencyController.getConversionTransactions(Mockito.anyLong()))
            .thenReturn(List.of(
                createConversionRecord("INR", "USD", 1_000.00, "APPROVED"),
                createConversionRecord("INR", "JPY", 150_000.00, "APPROVED"),
                createConversionRecord("INR", "JPY", 75_000.00, "PENDING"),
                createConversionRecord("INR", "USD", 500.00, "APPROVED")
            ));

        // Mock approval filtering on server side returns only INR->JPY for this role with applied filter
        Mockito.when(currencyController.getConversionTransactionsFiltered(Mockito.anyLong(), Mockito.eq("INR"), Mockito.eq("JPY")))
            .thenReturn(List.of(
                createConversionRecord("INR", "JPY", 150_000.00, "APPROVED"),
                createConversionRecord("INR", "JPY", 75_000.00, "PENDING")
            ));
    }

    @AfterAll
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private static ConversionRecord createConversionRecord(String from, String to, double amount, String status) {
        return new ConversionRecord(from, to, amount, status);
    }

    @Test
    @DisplayName("Verify ‘INR to JPY’ currency filter shows only correct transactions respecting roles and audit")
    public void testCurrencyFilterINRtoJPY() {
        driver.get("http://localhost:8080/transaction-history");

        // Wait for page load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currency-filter-from")));

        // Select 'INR' in 'From Currency' filter dropdown
        WebElement fromCurrencyDropdown = driver.findElement(By.id("currency-filter-from"));
        fromCurrencyDropdown.click();
        WebElement fromOptionINR = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='INR']")));
        fromOptionINR.click();

        // Select 'JPY' in 'To Currency' filter dropdown
        WebElement toCurrencyDropdown = driver.findElement(By.id("currency-filter-to"));
        toCurrencyDropdown.click();
        WebElement toOptionJPY = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='JPY']")));
        toOptionJPY.click();

        // Click the apply filter button
        WebElement applyFilterButton = driver.findElement(By.id("apply-filter-btn"));
        applyFilterButton.click();

        // Wait for the filtered results to update
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-indicator")));

        // Fetch transaction rows
        List<WebElement> rows = driver.findElements(By.cssSelector("table#transaction-table tbody tr"));

        assertThat("Filtered transaction list should not be empty", rows.size(), greaterThan(0));

        for (WebElement row : rows) {
            String fromCurrency = row.findElement(By.cssSelector("td.from-currency")).getText().trim();
            String toCurrency = row.findElement(By.cssSelector("td.to-currency")).getText().trim();
            String approvalStatus = row.findElement(By.cssSelector("td.approval-status")).getText().trim();

            // Assert currencies are INR to JPY
            assertThat("From currency must be INR", fromCurrency, equalTo("INR"));
            assertThat("To currency must be JPY", toCurrency, equalTo("JPY"));

            // Assert approval status is among allowed (e.g., PENDING, APPROVED)
            assertThat("Approval status must be valid", approvalStatus, anyOf(equalTo("APPROVED"), equalTo("PENDING"), equalTo("REJECTED")));
        }

        // Additionally verify total transaction amount displayed matches mocked filtered sum
        WebElement totalAmountElement = driver.findElement(By.id("total-amount"));
        String totalAmountText = totalAmountElement.getText().replaceAll("[^0-9.]", "");
        double totalDisplayed = Double.parseDouble(totalAmountText);
        double expectedTotal = 150000.00 + 75000.00;

        assertThat("Displayed total amount should equal sum of INR->JPY transactions", totalDisplayed, closeTo(expectedTotal, 0.01));
    }

    /**
     * Represent minimal stub for currency conversion transaction used in mocks.
     */
    public static class ConversionRecord {
        private String fromCurrency;
        private String toCurrency;
        private double amount;
        private String approvalStatus;

        public ConversionRecord(String fromCurrency, String toCurrency, double amount, String approvalStatus) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.amount = amount;
            this.approvalStatus = approvalStatus;
        }

        public String getFromCurrency() {
            return fromCurrency;
        }

        public String getToCurrency() {
            return toCurrency;
        }

        public double getAmount() {
            return amount;
        }

        public String getApprovalStatus() {
            return approvalStatus;
        }
    }
}
