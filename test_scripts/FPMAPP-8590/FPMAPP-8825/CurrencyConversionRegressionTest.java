/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8825
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:45:57
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
public class CurrencyConversionRegressionTest {

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
        // Mock currency conversion response for INR to JPY
        when(currencyConvertionController.convertCurrency("INR", "JPY", 5000.0))
                .thenReturn(7500.0); // Assume 1 INR = 1.5 JPY for test

        // Mock transaction history data returned by backend
        // Simulate one INR to JPY transaction with audit trail and approval
        TransactionEntry mockEntry = new TransactionEntry();
        mockEntry.setTransactionId("TXN12345");
        mockEntry.setDate("15-06-2024");
        mockEntry.setFromCurrency("INR");
        mockEntry.setToCurrency("JPY");
        mockEntry.setAmountFrom(5000.0);
        mockEntry.setAmountTo(7500.0);
        mockEntry.setStatus("Completed");
        mockEntry.setAuditTrail("Approved by Manager on 16-06-2024. Role: Finance Approver.");

        when(fpmCommonController.getTransactionHistory())
                .thenReturn(Arrays.asList(mockEntry));
    }

    @Test
    public void testINRtoJPYConversionEntryDisplayedCorrectly() {
        driver.get(BASE_URL + "/transaction-history");

        // Wait for transaction table to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

        WebElement table = driver.findElement(By.id("transactionTable"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        boolean foundINRtoJPYEntry = false;

        for (WebElement row : rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            if (cols.size() < 6) {
                continue; // Skip header or malformed rows
            }

            String dateStr = cols.get(0).getText().trim();
            String fromCurrency = cols.get(1).getText().trim();
            String toCurrency = cols.get(2).getText().trim();
            String amountFromStr = cols.get(3).getText().trim();
            String amountToStr = cols.get(4).getText().trim();
            String status = cols.get(5).getText().trim();

            if ("INR".equalsIgnoreCase(fromCurrency) && "JPY".equalsIgnoreCase(toCurrency)) {
                foundINRtoJPYEntry = true;

                // Validate date format DD-MM-YYYY
                assertThat(isValidDateFormat(dateStr, "dd-MM-yyyy"))
                        .as("Date format should be DD-MM-YYYY but was %s", dateStr)
                        .isTrue();

                // Validate amount in INR displayed correctly with ₹ symbol
                assertThat(amountFromStr).startsWith("₹")
                        .as("Amount in INR should start with ₹ symbol")
                        .isTrue();

                // Parse numeric part and check value
                double amountFromValue = parseCurrencyAmount(amountFromStr, "₹");
                assertThat(amountFromValue).isEqualTo(5000.0);

                // Validate amount in JPY is correctly calculated
                double amountToValue = parseCurrencyAmount(amountToStr, "¥");
                double expectedJPY = 7500.0; // mocked conversion
                assertThat(amountToValue).isEqualTo(expectedJPY);

                // Validate status
                assertThat(status).isEqualToIgnoringCase("Completed");

                // Click to view audit trail details
                WebElement auditTrailButton = row.findElement(By.cssSelector("button.audit-trail-btn"));
                auditTrailButton.click();

                // Wait for audit trail modal/dialog
                WebElement auditTrailModal = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailModal")));

                String auditTrailText = auditTrailModal.getText();

                assertThat(auditTrailText)
                        .contains("Approved by Manager")
                        .contains("Role: Finance Approver")
                        .as("Audit trail details should contain approval info and role");

                // Close audit trail modal
                WebElement closeBtn = auditTrailModal.findElement(By.cssSelector("button.close-modal"));
                closeBtn.click();

                // Verify modal closed
                wait.until(ExpectedConditions.invisibilityOf(auditTrailModal));

                break;
            }
        }

        assertThat(foundINRtoJPYEntry).as("At least one INR to JPY conversion entry should be present").isTrue();
    }

    private boolean isValidDateFormat(String dateStr, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private double parseCurrencyAmount(String amountStr, String currencySymbol) {
        try {
            String numericPart = amountStr.replace(currencySymbol, "").replace(",", "").trim();
            return Double.parseDouble(numericPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid currency amount format: " + amountStr, e);
        }
    }

    // Mock DTO for transaction entry
    public static class TransactionEntry {
        private String transactionId;
        private String date;
        private String fromCurrency;
        private String toCurrency;
        private double amountFrom;
        private double amountTo;
        private String status;
        private String auditTrail;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getFromCurrency() {
            return fromCurrency;
        }

        public void setFromCurrency(String fromCurrency) {
            this.fromCurrency = fromCurrency;
        }

        public String getToCurrency() {
            return toCurrency;
        }

        public void setToCurrency(String toCurrency) {
            this.toCurrency = toCurrency;
        }

        public double getAmountFrom() {
            return amountFrom;
        }

        public void setAmountFrom(double amountFrom) {
            this.amountFrom = amountFrom;
        }

        public double getAmountTo() {
            return amountTo;
        }

        public void setAmountTo(double amountTo) {
            this.amountTo = amountTo;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAuditTrail() {
            return auditTrail;
        }

        public void setAuditTrail(String auditTrail) {
            this.auditTrail = auditTrail;
        }
    }
}
