/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8828
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:44:08
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import org.openqa.selenium.support.ui.Select;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyFilterRegressionTest {

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
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock currency conversion history data with multiple currency pairs
        List<TransactionHistoryEntry> mockHistory = Arrays.asList(
            new TransactionHistoryEntry("INR", "USD", 1000.0, "Approved", "Audit trail data 1", LocalDateTime.now().minusDays(2), "ROLE_FINANCE"),
            new TransactionHistoryEntry("INR", "JPY", 50000.0, "Approved", "Audit trail data 2", LocalDateTime.now().minusDays(1), "ROLE_MANAGER"),
            new TransactionHistoryEntry("INR", "JPY", 75000.0, "Pending", "Audit trail data 3", LocalDateTime.now().minusHours(5), "ROLE_MANAGER"),
            new TransactionHistoryEntry("USD", "JPY", 1200.0, "Rejected", "Audit trail data 4", LocalDateTime.now().minusDays(3), "ROLE_FINANCE")
        );

        when(currencyConvertionController.getTransactionHistory(any(), any())).thenAnswer(invocation -> {
            String fromCurrency = invocation.getArgument(0);
            String toCurrency = invocation.getArgument(1);
            // Filter mock data based on from and to currency
            return mockHistory.stream()
                .filter(e -> e.getFromCurrency().equals(fromCurrency) && e.getToCurrency().equals(toCurrency))
                .toList();
        });

        // Mock user profile with role-based approval restrictions
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setRoles(Arrays.asList("ROLE_MANAGER"));
        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);
    }

    @Test
    public void testCurrencyFilter_INRtoJPY_ShowsCorrectEntriesWithAuditAndApproval() {
        driver.get("http://localhost:8080/transaction-history");

        // Wait for page to load currency filter dropdown
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyFilter")));

        // Select currency filter "INR to JPY"
        Select currencyFilter = new Select(driver.findElement(By.id("currencyFilter")));
        currencyFilter.selectByVisibleText("INR to JPY");

        // Click filter button
        WebElement filterButton = driver.findElement(By.id("filterButton"));
        filterButton.click();

        // Wait for filtered results to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

        WebElement table = driver.findElement(By.id("transactionTable"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        // Assert that only INR to JPY entries are displayed
        assertThat(rows).isNotEmpty();

        for (WebElement row : rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            if (cols.size() < 6) {
                continue; // skip header or malformed rows
            }
            String fromCurrency = cols.get(1).getText();
            String toCurrency = cols.get(2).getText();
            String approvalStatus = cols.get(4).getText();
            String auditTrail = cols.get(5).getText();

            // Check currency pair
            assertThat(fromCurrency).isEqualTo("INR");
            assertThat(toCurrency).isEqualTo("JPY");

            // Check approval status is visible and valid
            assertThat(approvalStatus).isIn("Approved", "Pending", "Rejected");

            // Check audit trail is not empty
            assertThat(auditTrail).isNotBlank();
        }

        // Verify that entries respect role-based approval restrictions
        // For ROLE_MANAGER, only entries with ROLE_MANAGER approval should be visible
        for (WebElement row : rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            if (cols.size() < 7) {
                continue;
            }
            String role = cols.get(6).getText();
            assertThat(role).isEqualTo("ROLE_MANAGER");
        }

        // Verify backward compatibility: filter dropdown still contains previous options
        Select currencyFilterAfter = new Select(driver.findElement(By.id("currencyFilter")));
        List<WebElement> options = currencyFilterAfter.getOptions();
        assertThat(options).extracting(WebElement::getText).contains("INR to USD", "INR to JPY", "USD to JPY");
    }

    // Helper DTO class to mock transaction history entries
    public static class TransactionHistoryEntry {
        private String fromCurrency;
        private String toCurrency;
        private Double amount;
        private String approvalStatus;
        private String auditTrail;
        private LocalDateTime transactionDate;
        private String approvalRole;

        public TransactionHistoryEntry(String fromCurrency, String toCurrency, Double amount, String approvalStatus, String auditTrail, LocalDateTime transactionDate, String approvalRole) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.amount = amount;
            this.approvalStatus = approvalStatus;
            this.auditTrail = auditTrail;
            this.transactionDate = transactionDate;
            this.approvalRole = approvalRole;
        }

        public String getFromCurrency() {
            return fromCurrency;
        }

        public String getToCurrency() {
            return toCurrency;
        }

        public Double getAmount() {
            return amount;
        }

        public String getApprovalStatus() {
            return approvalStatus;
        }

        public String getAuditTrail() {
            return auditTrail;
        }

        public LocalDateTime getTransactionDate() {
            return transactionDate;
        }

        public String getApprovalRole() {
            return approvalRole;
        }
    }
}
