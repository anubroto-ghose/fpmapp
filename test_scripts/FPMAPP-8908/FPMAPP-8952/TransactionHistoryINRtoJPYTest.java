/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8952
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:39:04
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.junit.jupiter.api.Assertions.*;
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

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.entities.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TransactionHistoryINRtoJPYTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController userProfileController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        // Mock user profile with role-based approval enabled
        User mockUser = new User();
        mockUser.setId(1001L);
        mockUser.setUsername("testuser");
        mockUser.setRole("APPROVER");
        when(userProfileController.getCurrentUser()).thenReturn(mockUser);

        // Mock currency conversion history for INR to JPY
        List<CurrencyConversionTransaction> mockTransactions = Arrays.asList(
            new CurrencyConversionTransaction(
                LocalDateTime.of(2024, 6, 1, 10, 30, 0),
                10000.00,
                160000.00,
                "Completed"
            ),
            new CurrencyConversionTransaction(
                LocalDateTime.of(2024, 6, 5, 15, 45, 0),
                5000.00,
                80000.00,
                "Failed"
            )
        );

        when(currencyConvertionController.getTransactionHistory("INR", "JPY")).thenReturn(mockTransactions);
    }

    @Test
    public void testTransactionHistoryPageDisplaysINRtoJPYConversions() {
        try {
            // Step 1: Login as user with role-based approval enabled
            driver.get(BASE_URL + "/login");

            WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginBtn"));

            usernameInput.sendKeys("testuser");
            passwordInput.sendKeys("password123");
            loginButton.click();

            // Wait for dashboard or home page
            wait.until(ExpectedConditions.urlContains("/dashboard"));

            // Step 2: Navigate to transaction history page
            driver.get(BASE_URL + "/transactions/history");

            // Step 3: Filter/select currency pair INR to JPY
            WebElement currencyPairDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("currencyPairSelect")));
            currencyPairDropdown.click();
            WebElement inrToJpyOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='INR-JPY']")));
            inrToJpyOption.click();

            // Wait for transactions to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

            WebElement transactionTable = driver.findElement(By.id("transactionTable"));
            List<WebElement> rows = transactionTable.findElements(By.tagName("tr"));

            // There should be at least header + 2 rows (mocked transactions)
            assertTrue(rows.size() >= 3, "Transaction table should have at least 2 data rows");

            // Validate each transaction row
            // Skip header row (index 0)
            for (int i = 1; i < rows.size(); i++) {
                WebElement row = rows.get(i);
                List<WebElement> cols = row.findElements(By.tagName("td"));
                assertEquals(4, cols.size(), "Each transaction row should have 4 columns");

                String dateStr = cols.get(0).getText();
                String amountInINRStr = cols.get(1).getText();
                String amountInJPYStr = cols.get(2).getText();
                String statusStr = cols.get(3).getText();

                // Validate date format
                LocalDateTime date = null;
                try {
                    date = LocalDateTime.parse(dateStr, DATE_FORMATTER);
                } catch (Exception e) {
                    fail("Date format is invalid: " + dateStr);
                }
                assertNotNull(date, "Date should not be null");

                // Validate amounts are numeric and positive
                double amountINR = parseCurrency(amountInINRStr);
                double amountJPY = parseCurrency(amountInJPYStr);
                assertTrue(amountINR > 0, "Amount in INR should be positive");
                assertTrue(amountJPY > 0, "Amount in JPY should be positive");

                // Validate status
                assertTrue(statusStr.equalsIgnoreCase("Completed") || statusStr.equalsIgnoreCase("Failed"),
                        "Status should be either Completed or Failed");
            }

            // Additional check: Role-based approval does not interfere
            // For this test, ensure no approval-related overlays or errors
            List<WebElement> approvalErrors = driver.findElements(By.className("approval-error"));
            assertTrue(approvalErrors.isEmpty(), "No approval errors should be visible");

        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    /**
     * Helper method to parse currency string like "10,000.00" or "10000.00" to double
     */
    private double parseCurrency(String currencyStr) {
        try {
            String normalized = currencyStr.replaceAll(",", "").trim();
            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            fail("Invalid currency format: " + currencyStr);
            return 0;
        }
    }

    /**
     * Mock DTO for currency conversion transaction
     */
    public static class CurrencyConversionTransaction {
        private LocalDateTime transactionDate;
        private double amountInINR;
        private double amountInJPY;
        private String status;

        public CurrencyConversionTransaction(LocalDateTime transactionDate, double amountInINR, double amountInJPY, String status) {
            this.transactionDate = transactionDate;
            this.amountInINR = amountInINR;
            this.amountInJPY = amountInJPY;
            this.status = status;
        }

        public LocalDateTime getTransactionDate() {
            return transactionDate;
        }

        public double getAmountInINR() {
            return amountInINR;
        }

        public double getAmountInJPY() {
            return amountInJPY;
        }

        public String getStatus() {
            return status;
        }
    }
}
