/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7225
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:07:55
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class TransactionHistoryTest {

    private WebDriver driver;

    @Mock
    private CurrencyConversionService currencyConversionService;

    @InjectMocks
    private TransactionHistoryController transactionHistoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");
        loginAsTestUser();
    }

    private void loginAsTestUser() {
        // Assuming user credentials are hardcoded for testing purpose
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.id("loginButton")).click();
    }

    @Test
    void testINRtoJPYTransactions() {
        // Mocking service call for INR to JPY transactions
        when(currencyConversionService.getTransactions("INR", "JPY")).thenReturn(createTransactionHistory());

        // Navigate to the transaction history page
        driver.get("http://localhost:8080/transaction-history");

        // Wait for the transaction list to be visible
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

        // Filter for INR to JPY
        driver.findElement(By.id("currencyFilter")).sendKeys("INR-JPY");
        driver.findElement(By.id("filterButton")).click();

        // Assertions
        assertTrue(driver.findElement(By.id("transactionTable")).isDisplayed(), "Transaction table is not displayed");

        // Verify that transaction entries are displayed as expected
        assertEquals(1, driver.findElements(By.cssSelector("#transactionTable tr")).size(), "Expected one transaction entry");
        assertEquals("2025-11-04", driver.findElement(By.cssSelector("#transactionTable tr:nth-child(1) td.date")).getText(), "Date of transaction does not match");
        assertEquals("1000", driver.findElement(By.cssSelector("#transactionTable tr:nth-child(1) td.inrAmount")).getText(), "Amount in INR does not match");
        assertEquals("7350", driver.findElement(By.cssSelector("#transactionTable tr:nth-child(1) td.jpyAmount")).getText(), "Equivalent amount in JPY does not match");
        assertEquals("Completed", driver.findElement(By.cssSelector("#transactionTable tr:nth-child(1) td.status")).getText(), "Transaction status does not match");
    }

    private List<TransactionDTO> createTransactionHistory() {
        List<TransactionDTO> transactions = new ArrayList<>();
        TransactionDTO transaction = new TransactionDTO();
        transaction.setDate(LocalDate.now().minusDays(1));
        transaction.setInrAmount(BigDecimal.valueOf(1000));
        transaction.setJpyAmount(BigDecimal.valueOf(7350));
        transaction.setStatus("Completed");
        transactions.add(transaction);
        return transactions;
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}