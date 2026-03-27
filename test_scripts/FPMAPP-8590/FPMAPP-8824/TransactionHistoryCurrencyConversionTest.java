/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8824
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:46:39
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TransactionHistoryCurrencyConversionTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user login and role-based approval enabled
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setRole("APPROVER");

        when(fpmUserProfileController.getCurrentUser()).thenReturn(mockUser);
        when(fpmCommonController.isRoleBasedApprovalEnabled()).thenReturn(true);

        // Mock currency conversion history for INR to JPY
        List<CurrencyConversionTransaction> mockTransactions = Arrays.asList(
            new CurrencyConversionTransaction(
                LocalDateTime.of(2024, 4, 10, 14, 30),
                "INR", 100000.00,
                "JPY", 1600000.00,
                "Completed",
                "Audit trail: Conversion approved by manager on 2024-04-10 15:00"
            ),
            new CurrencyConversionTransaction(
                LocalDateTime.of(2024, 4, 5, 9, 15),
                "INR", 50000.00,
                "JPY", 800000.00,
                "Failed",
                "Audit trail: Conversion failed due to insufficient funds"
            )
        );

        when(currencyConvertionController.getTransactionHistory("INR", "JPY")).thenReturn(mockTransactions);
    }

    @Test
    public void testTransactionHistoryPageForINRtoJPYConversions() {
        try {
            // Step 1: Login simulation (assuming login page at /login)
            driver.get(BASE_URL + "/login");

            WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginButton"));

            usernameInput.sendKeys("testuser");
            passwordInput.sendKeys("password123");
            loginButton.click();

            // Wait for redirect to dashboard/home page
            wait.until(ExpectedConditions.urlContains("/dashboard"));

            // Step 2: Navigate to transaction history page
            driver.get(BASE_URL + "/transaction-history");

            // Step 3: Filter/select currency pair INR to JPY
            WebElement fromCurrencyDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("fromCurrency")));
            fromCurrencyDropdown.click();
            WebElement fromINROption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='INR']")));
            fromINROption.click();

            WebElement toCurrencyDropdown = driver.findElement(By.id("toCurrency"));
            toCurrencyDropdown.click();
            WebElement toJPYOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='JPY']")));
            toJPYOption.click();

            WebElement filterButton = driver.findElement(By.id("filterButton"));
            filterButton.click();

            // Step 4: Verify listed transactions and audit trail indicators
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));
            WebElement transactionTable = driver.findElement(By.id("transactionTable"));
            List<WebElement> rows = transactionTable.findElements(By.tagName("tr"));

            // We expect 2 transactions (header + 2 rows)
            assertThat(rows.size()).isGreaterThanOrEqualTo(3); // header + 2 data rows

            // Validate each transaction row
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            boolean foundFirst = false;
            boolean foundSecond = false;

            for (int i = 1; i < rows.size(); i++) { // skip header
                WebElement row = rows.get(i);
                List<WebElement> cols = row.findElements(By.tagName("td"));
                if (cols.size() < 6) {
                    throw new AssertionError("Transaction row does not have expected 6 columns");
                }

                String dateStr = cols.get(0).getText();
                String amountINR = cols.get(1).getText();
                String amountJPY = cols.get(2).getText();
                String status = cols.get(3).getText();
                String auditTrail = cols.get(4).getText();
                String currencyPair = cols.get(5).getText();

                assertThat(currencyPair).isEqualTo("INR to JPY");

                if (dateStr.equals("2024-04-10 14:30") && amountINR.equals("100,000.00") && amountJPY.equals("1,600,000.00") && status.equals("Completed")) {
                    assertThat(auditTrail).contains("approved by manager");
                    foundFirst = true;
                } else if (dateStr.equals("2024-04-05 09:15") && amountINR.equals("50,000.00") && amountJPY.equals("800,000.00") && status.equals("Failed")) {
                    assertThat(auditTrail).contains("failed due to insufficient funds");
                    foundSecond = true;
                }
            }

            assertThat(foundFirst).isTrue();
            assertThat(foundSecond).isTrue();

        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError("Test failed due to exception: " + e.getMessage());
        }
    }

    // DTO class to mock transaction data
    public static class CurrencyConversionTransaction {
        private LocalDateTime transactionDate;
        private String fromCurrency;
        private double amountFrom;
        private String toCurrency;
        private double amountTo;
        private String status;
        private String auditTrailInfo;

        public CurrencyConversionTransaction(LocalDateTime transactionDate, String fromCurrency, double amountFrom, String toCurrency, double amountTo, String status, String auditTrailInfo) {
            this.transactionDate = transactionDate;
            this.fromCurrency = fromCurrency;
            this.amountFrom = amountFrom;
            this.toCurrency = toCurrency;
            this.amountTo = amountTo;
            this.status = status;
            this.auditTrailInfo = auditTrailInfo;
        }

        public LocalDateTime getTransactionDate() {
            return transactionDate;
        }

        public String getFromCurrency() {
            return fromCurrency;
        }

        public double getAmountFrom() {
            return amountFrom;
        }

        public String getToCurrency() {
            return toCurrency;
        }

        public double getAmountTo() {
            return amountTo;
        }

        public String getStatus() {
            return status;
        }

        public String getAuditTrailInfo() {
            return auditTrailInfo;
        }
    }
}
