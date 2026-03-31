/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8955
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:36:32
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
 * Selenium integration test for transaction history pagination.
 * 
 * Regression test for FPMAPP-8955:
 * Ensures pagination works correctly for large number of INR to JPY entries.
 * 
 * Preconditions:
 * - User has more than one page of INR to JPY transactions
 * - Role-based approval and real-time currency integration features are active
 * 
 * Test Steps:
 * 1. Navigate to transaction history page
 * 2. Scroll to the bottom or use pagination controls
 * 3. Switch pages to view more INR to JPY entries
 * 
 * Expected Results:
 * - User can navigate between pages without data loss
 * - Each page consistently loads INR to JPY transactions
 * - Pagination performance is not degraded by new features
 * - Role-based approval status and currency data remain consistent across pages
 * - No UI glitches or data inconsistencies occur
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TransactionHistoryPaginationTest {

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

    private static final int PAGE_SIZE = 20;
    private static final int TOTAL_ENTRIES = 45; // More than 2 pages

    /**
     * Represents a mock transaction entry for INR to JPY conversion.
     */
    public static class TransactionEntry {
        private String fromCurrency = "INR";
        private String toCurrency = "JPY";
        private double amount;
        private double convertedAmount;
        private String approvalStatus;
        private String transactionId;

        public TransactionEntry(String transactionId, double amount, double convertedAmount, String approvalStatus) {
            this.transactionId = transactionId;
            this.amount = amount;
            this.convertedAmount = convertedAmount;
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

        public double getConvertedAmount() {
            return convertedAmount;
        }

        public String getApprovalStatus() {
            return approvalStatus;
        }

        public String getTransactionId() {
            return transactionId;
        }
    }

    private List<TransactionEntry> allTransactions;

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
    public void setup() {
        // Prepare mock data for transactions
        allTransactions = IntStream.rangeClosed(1, TOTAL_ENTRIES)
                .mapToObj(i -> new TransactionEntry(
                        "TXN" + i,
                        1000 + i * 10, // amount in INR
                        (1000 + i * 10) * 1.5, // converted amount in JPY (mock rate 1.5)
                        (i % 3 == 0) ? "Approved" : "Pending"))
                .collect(Collectors.toList());

        // Mock currency conversion service to always return 1.5 for INR to JPY
        when(currencyConvertionController.convert("INR", "JPY", any(Double.class)))
                .thenAnswer(invocation -> {
                    Double amount = invocation.getArgument(2);
                    return amount * 1.5;
                });

        // Mock FpmCommonController to return role-based approval status
        when(fpmCommonController.getApprovalStatus(any(String.class)))
                .thenAnswer(invocation -> {
                    String txnId = invocation.getArgument(0);
                    return allTransactions.stream()
                            .filter(t -> t.getTransactionId().equals(txnId))
                            .findFirst()
                            .map(TransactionEntry::getApprovalStatus)
                            .orElse("Pending");
                });

        // Mock FpmDealsheetController or other services as needed
        // For this test, assume no additional mocks needed
    }

    /**
     * Helper method to mock backend API response for a given page.
     * In a real scenario, this would be done via MockMvc or WireMock.
     * Here, we simulate by injecting JS to override fetch or by assuming backend test profile.
     * 
     * For demonstration, this method is a placeholder to indicate mocking strategy.
     */
    private void mockBackendForPage(int pageNumber) {
        // This is a placeholder: in a real integration test, you would mock REST API responses
        // or use a test profile with preloaded data.
        // Since this is a Selenium test, we assume the backend is preloaded with allTransactions.
    }

    @Test
    public void testPaginationLoadsINRtoJPYTransactionsCorrectly() {
        driver.get(BASE_URL + "/transaction-history");

        // Wait for page to load transaction table
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

        int totalPages = (int) Math.ceil((double) TOTAL_ENTRIES / PAGE_SIZE);

        for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
            // Mock backend for current page (if applicable)
            mockBackendForPage(currentPage);

            // Use pagination controls to navigate to the page
            if (currentPage > 1) {
                WebElement paginationInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("paginationInput")));
                paginationInput.clear();
                paginationInput.sendKeys(String.valueOf(currentPage));

                WebElement goButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("paginationGoButton")));
                goButton.click();

                // Wait for page data to load
                wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("currentPageIndicator"), String.valueOf(currentPage)));
            }

            // Verify transactions on current page
            List<TransactionEntry> expectedPageTransactions = allTransactions.stream()
                    .skip((currentPage - 1) * PAGE_SIZE)
                    .limit(PAGE_SIZE)
                    .collect(Collectors.toList());

            List<WebElement> rows = driver.findElements(By.cssSelector("#transactionTable tbody tr"));

            assertThat(rows.size()).as("Number of rows on page %d", currentPage).isEqualTo(expectedPageTransactions.size());

            for (int i = 0; i < rows.size(); i++) {
                WebElement row = rows.get(i);
                TransactionEntry expected = expectedPageTransactions.get(i);

                String txnId = row.findElement(By.cssSelector("td.txnId")).getText();
                String fromCurrency = row.findElement(By.cssSelector("td.fromCurrency")).getText();
                String toCurrency = row.findElement(By.cssSelector("td.toCurrency")).getText();
                String amountStr = row.findElement(By.cssSelector("td.amount")).getText();
                String convertedAmountStr = row.findElement(By.cssSelector("td.convertedAmount")).getText();
                String approvalStatus = row.findElement(By.cssSelector("td.approvalStatus")).getText();

                assertThat(txnId).isEqualTo(expected.getTransactionId());
                assertThat(fromCurrency).isEqualTo("INR");
                assertThat(toCurrency).isEqualTo("JPY");

                double amount = Double.parseDouble(amountStr.replaceAll(",", ""));
                double convertedAmount = Double.parseDouble(convertedAmountStr.replaceAll(",", ""));

                assertThat(amount).isEqualTo(expected.getAmount());
                assertThat(convertedAmount).isCloseTo(expected.getConvertedAmount(),
                        org.assertj.core.data.Offset.offset(0.01));

                assertThat(approvalStatus).isEqualTo(expected.getApprovalStatus());
            }

            // Scroll to bottom to simulate user behavior
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

            // Small wait to simulate user pause
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
