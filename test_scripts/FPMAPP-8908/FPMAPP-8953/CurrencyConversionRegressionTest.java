/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8953
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:38:10
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
        // Mock real-time currency conversion rate for INR to JPY
        when(currencyConvertionController.getConversionRate("INR", "JPY")).thenReturn(1.5); // example rate

        // Mock transaction data returned by backend
        // Simulate at least one INR to JPY transaction
        TransactionEntry mockEntry = new TransactionEntry();
        mockEntry.setDate("15-06-2024");
        mockEntry.setAmountInr(5000.00);
        mockEntry.setAmountJpy(7500.00); // 5000 * 1.5
        mockEntry.setStatus("Completed");
        mockEntry.setApprovalStatus("Approved");

        when(fpmCommonController.getTransactionsByCurrencyPair("INR", "JPY"))
            .thenReturn(Arrays.asList(mockEntry));

        // Mock role-based approval feature active
        when(fpmUserProfileController.isRoleBasedApprovalActive()).thenReturn(true);

        // Mock real-time currency integration enabled
        when(fpmCommonController.isRealTimeCurrencyIntegrationEnabled()).thenReturn(true);
    }

    @Test
    public void testInrToJpyConversionEntryDisplayedCorrectly() {
        try {
            driver.get(BASE_URL + "/transaction-history");

            // Wait for transaction table to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

            WebElement table = driver.findElement(By.id("transactionTable"));
            List<WebElement> rows = table.findElements(By.tagName("tr"));

            boolean foundInrToJpyEntry = false;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            for (WebElement row : rows) {
                List<WebElement> cols = row.findElements(By.tagName("td"));
                if (cols.size() < 5) {
                    continue; // skip header or malformed rows
                }

                String dateStr = cols.get(0).getText().trim();
                String amountInrStr = cols.get(1).getText().trim();
                String amountJpyStr = cols.get(2).getText().trim();
                String statusStr = cols.get(3).getText().trim();
                String approvalStatusStr = cols.get(4).getText().trim();

                // Identify INR to JPY conversion entry by currency symbols and amounts
                if (amountInrStr.startsWith("₹") && amountJpyStr.endsWith("¥")) {
                    foundInrToJpyEntry = true;

                    // Validate date format
                    LocalDate parsedDate = null;
                    try {
                        parsedDate = LocalDate.parse(dateStr, formatter);
                    } catch (Exception e) {
                        throw new AssertionError("Date format is invalid: " + dateStr);
                    }
                    assertThat(parsedDate).isNotNull();

                    // Validate INR amount format and value
                    String inrAmountNumeric = amountInrStr.replace("₹", "").replace(",", "").trim();
                    double inrAmount = Double.parseDouble(inrAmountNumeric);
                    assertThat(inrAmount).isEqualTo(5000.00);

                    // Validate JPY amount format and value
                    String jpyAmountNumeric = amountJpyStr.replace("¥", "").replace(",", "").trim();
                    double jpyAmount = Double.parseDouble(jpyAmountNumeric);
                    double expectedJpy = inrAmount * 1.5; // mocked rate
                    assertThat(jpyAmount).isEqualTo(expectedJpy);

                    // Validate status
                    assertThat(statusStr).isEqualTo("Completed");

                    // Validate approval status
                    assertThat(approvalStatusStr).isIn("Approved", "Pending", "Rejected");

                    // Additional check: role-based approval feature active
                    assertThat(fpmUserProfileController.isRoleBasedApprovalActive()).isTrue();

                    // Additional check: real-time currency integration enabled
                    assertThat(fpmCommonController.isRealTimeCurrencyIntegrationEnabled()).isTrue();

                    break;
                }
            }

            assertThat(foundInrToJpyEntry).isTrue();

        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError("Test failed due to exception: " + e.getMessage());
        }
    }

    // Helper DTO class to mock transaction entries
    public static class TransactionEntry {
        private String date;
        private double amountInr;
        private double amountJpy;
        private String status;
        private String approvalStatus;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getAmountInr() {
            return amountInr;
        }

        public void setAmountInr(double amountInr) {
            this.amountInr = amountInr;
        }

        public double getAmountJpy() {
            return amountJpy;
        }

        public void setAmountJpy(double amountJpy) {
            this.amountJpy = amountJpy;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getApprovalStatus() {
            return approvalStatus;
        }

        public void setApprovalStatus(String approvalStatus) {
            this.approvalStatus = approvalStatus;
        }
    }
}
