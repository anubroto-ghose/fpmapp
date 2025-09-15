/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6208
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:49:37
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import java.time.Duration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class InrToJpyPaginationIntegrationTest {

    private WebDriver driver;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    private static final String BASE_URL = "http://localhost:8080";

    // Test user with role-based permissions
    private final User testUser = new User();

    @BeforeEach
    public void setUp() {
        // Setup selenium driver - headless chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Setup test user data
        testUser.setId(1001L);
        testUser.setUsername("testuser");
        testUser.setRole("APPROVER_INR_JPY");

        // Mock service to return INRs to JPY transactions with audit trails for multiple pages
        when(currencyConvertionController.getTransactionHistory(
                any(String.class), // from currency
                any(String.class), // to currency
                any(Integer.class), // page number
                any(Integer.class)))  // page size
                .thenAnswer(invocation -> {
                    String fromCurrency = invocation.getArgument(0);
                    String toCurrency = invocation.getArgument(1);
                    Integer page = invocation.getArgument(2);
                    Integer size = invocation.getArgument(3);

                    // Validate currencies
                    if (!"INR".equalsIgnoreCase(fromCurrency) || !"JPY".equalsIgnoreCase(toCurrency)) {
                        return new ArrayList<>();
                    }

                    int totalRecords = 45; // more than one page (pageSize=20)
                    int startIndex = page * size;
                    if (startIndex >= totalRecords) {
                        return new ArrayList<>();
                    }
                    int endIndex = Math.min(startIndex + size, totalRecords);

                    List<TransactionAuditDTO> results = new ArrayList<>();
                    for (int i = startIndex; i < endIndex; i++) {
                        TransactionAuditDTO tx = new TransactionAuditDTO();
                        tx.setTransactionId(1000 + i);
                        tx.setFromCurrency("INR");
                        tx.setToCurrency("JPY");
                        tx.setAmount(10000 + i * 100);
                        tx.setConvertedAmount(1500000 + i * 15000);
                        tx.setTransactionTimestamp(LocalDateTime.now().minusDays(i));
                        tx.setAuditRemarks("Audit entry for transaction " + (1000 + i));
                        tx.setApprovalStatus(i % 3 == 0 ? "APPROVED" : "PENDING");
                        results.add(tx);
                    }
                    return results;
                });

        // Mock other controllers if needed for data fetching and ACL enforcement
        when(fpmDealsheetController.hasAccess(any(Long.class), any(User.class))).thenReturn(true);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testPaginationOfInrToJpyTransactionsWithAuditTrail() {
        try {
            // Navigate to the transaction history page
            driver.get(BASE_URL + "/transactions/history?from=INR&to=JPY");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait for transaction table to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

            int currentPage = 0;
            int maxPages = 3; // Test for at least 3 pages
            int pageSize = 20;

            while (currentPage < maxPages) {
                // Verify data present in table
                List<WebElement> rows = driver.findElements(By.cssSelector("#transactionTable tbody tr"));
                assertFalse(rows.isEmpty(), "Transaction table rows should not be empty on page " + currentPage);

                // Verify content consistency and audit trail presence for each row
                for (WebElement row : rows) {
                    // Columns: txId, fromCurr, toCurr, amount, convertedAmount, timestamp, approvalStatus, auditLink
                    String fromCurr = row.findElement(By.cssSelector("td.fromCurrency")).getText();
                    String toCurr = row.findElement(By.cssSelector("td.toCurrency")).getText();
                    String approvalStatus = row.findElement(By.cssSelector("td.approvalStatus")).getText();

                    assertEquals("INR", fromCurr, "From Currency must be INR");
                    assertEquals("JPY", toCurr, "To Currency must be JPY");
                    assertTrue(approvalStatus.matches("APPROVED|PENDING|REJECTED"), "Approval status should be valid");

                    // Audit trail link - verify it exists
                    WebElement auditLink = row.findElement(By.cssSelector("td.auditTrail a"));
                    assertNotNull(auditLink, "Audit trail link must be present for each transaction");

                    // Click audit link opens modal/popup - verify modal content
                    auditLink.click();
                    WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailModal")));
                    assertNotNull(modal, "Audit trail modal should appear on click");

                    WebElement modalContent = modal.findElement(By.cssSelector("div.modal-body"));
                    assertTrue(modalContent.getText().length() > 0, "Audit trail modal content should not be empty");

                    // Close modal
                    WebElement closeBtn = modal.findElement(By.cssSelector("button.close-modal"));
                    closeBtn.click();

                    wait.until(ExpectedConditions.invisibilityOf(modal));
                }

                // Test role-based permissions filtering: 
                // The user role APPROVER_INR_JPY should only see INR to JPY transactions
                for (WebElement row : rows) {
                    String fromCurr = row.findElement(By.cssSelector("td.fromCurrency")).getText();
                    String toCurr = row.findElement(By.cssSelector("td.toCurrency")).getText();
                    assertEquals("INR", fromCurr, "Role-based filtering failed for from currency");
                    assertEquals("JPY", toCurr, "Role-based filtering failed for to currency");
                }

                // Pagination controls
                WebElement nextPageBtn = driver.findElement(By.id("paginationNext"));
                if (nextPageBtn.isEnabled()) {
                    nextPageBtn.click();
                    currentPage++;
                    // Wait for new page to load (e.g. table reload)
                    wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));
                } else {
                    // No more pages
                    break;
                }
            }

            // Assert we visited at least 2 pages
            assertTrue(currentPage >= 1, "Should paginate through at least 2 pages");

        } catch (Exception e) {
            fail("Exception during pagination test: " + e.getMessage());
        }
    }

    // DTO class for mocking transactions with audit trails
    public static class TransactionAuditDTO {
        private long transactionId;
        private String fromCurrency;
        private String toCurrency;
        private double amount;
        private double convertedAmount;
        private LocalDateTime transactionTimestamp;
        private String auditRemarks;
        private String approvalStatus;

        public long getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(long transactionId) {
            this.transactionId = transactionId;
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

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getConvertedAmount() {
            return convertedAmount;
        }

        public void setConvertedAmount(double convertedAmount) {
            this.convertedAmount = convertedAmount;
        }

        public LocalDateTime getTransactionTimestamp() {
            return transactionTimestamp;
        }

        public void setTransactionTimestamp(LocalDateTime transactionTimestamp) {
            this.transactionTimestamp = transactionTimestamp;
        }

        public String getAuditRemarks() {
            return auditRemarks;
        }

        public void setAuditRemarks(String auditRemarks) {
            this.auditRemarks = auditRemarks;
        }

        public String getApprovalStatus() {
            return approvalStatus;
        }

        public void setApprovalStatus(String approvalStatus) {
            this.approvalStatus = approvalStatus;
        }
    }
}
