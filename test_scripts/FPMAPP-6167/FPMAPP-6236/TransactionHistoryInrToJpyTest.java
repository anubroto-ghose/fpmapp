/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6236
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:29:01
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmForecastController;

/**
 * Regression test based on FPMAPP-6167 epic for 
 * accessing transaction history page for INR to JPY conversions
 * with role based approval enabled.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TransactionHistoryInrToJpyTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @BeforeAll
    public static void setUp() {
        // Assuming chromedriver executable is set in system path or webdriver.chrome.driver property is set
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void loginAsUser(String username, String password) {
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitBtn = driver.findElement(By.id("submitLogin"));

        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        submitBtn.click();

        // Wait for home page or dashboard element that confirms login success
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    /**
     * Mock the currency conversion history with one INR to JPY transaction entry.
     * The mock simulates data fetched by underlying service handling currency conversion history.
     */
    private void setupMockCurrencyConversionHistory() {
        // Prepare mock data
        // Example transaction data
        class Transaction {
            String date;
            String fromCurrency;
            String toCurrency;
            double amountFrom;
            double amountTo;
            String status;

            Transaction(String date, String fromCurrency, String toCurrency, double amountFrom, double amountTo, String status) {
                this.date = date;
                this.fromCurrency = fromCurrency;
                this.toCurrency = toCurrency;
                this.amountFrom = amountFrom;
                this.amountTo = amountTo;
                this.status = status;
            }
        }

        Transaction txn = new Transaction("2025-09-10T12:00:00", "INR", "JPY", 100000.00, 135000.50, "COMPLETED");

        List<Transaction> mockTransactions = List.of(txn);

        Mockito.when(currencyConvertionController.getTransactionHistory(Mockito.eq("INR"), Mockito.eq("JPY")))
               .thenReturn(mockTransactions);

        // Mock user profile role to allow approved viewing
        Mockito.when(userProfileController.getUserRoles(Mockito.anyString()))
               .thenReturn(List.of("ROLE_APPROVER", "ROLE_USER"));
    }

    @Test
    public void testTransactionHistoryDisplaysAllInrToJpyConversions() {
        try {
            setupMockCurrencyConversionHistory();

            // Login as a user with role-based access
            loginAsUser("testuser", "testpassword");

            // Navigate to transaction history page
            driver.get(BASE_URL + "/transactions/history");

            // Wait for currency pair filter UI
            WebElement currencyPairFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyPairFilter")));
            currencyPairFilter.click();

            // Select INR to JPY from dropdown
            WebElement inrOption = driver.findElement(By.xpath("//option[@value='INR-JPY']"));
            inrOption.click();

            // Submit the filter/search
            WebElement filterSubmitBtn = driver.findElement(By.id("filterSubmit"));
            filterSubmitBtn.click();

            // Wait for results to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionsTable")));

            WebElement transactionsTable = driver.findElement(By.id("transactionsTable"));

            List<WebElement> rows = transactionsTable.findElements(By.cssSelector("tbody tr"));
            assertFalse(rows.isEmpty(), "Transaction history should not be empty for INR to JPY.");

            boolean foundExpectedTxn = false;
            for (WebElement row : rows) {
                List<WebElement> cols = row.findElements(By.tagName("td"));
                assertTrue(cols.size() >= 5, "Each row should have at least 5 columns displaying details.");

                String date = cols.get(0).getText().trim();
                String amountInr = cols.get(1).getText().trim();
                String amountJpy = cols.get(2).getText().trim();
                String status = cols.get(3).getText().trim();
                String auditTrailLink = cols.get(4).findElement(By.tagName("a")).getAttribute("href");

                if (date.contains("2025-09-10") && amountInr.contains("100,000") && amountJpy.contains("135,000") && status.equals("COMPLETED")) {
                    foundExpectedTxn = true;

                    // Verify Audit trail link is clickable and opens audit modal/page
                    assertNotNull(auditTrailLink);
                    // Simulate clicking and verifying audit trail modal appears

                    cols.get(4).findElement(By.tagName("a")).click();
                    WebElement auditModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailModal")));
                    assertTrue(auditModal.isDisplayed(), "Audit trail modal should appear on clicking link.");

                    // Verify audit trail entries present
                    List<WebElement> auditEntries = auditModal.findElements(By.cssSelector(".audit-entry"));
                    assertFalse(auditEntries.isEmpty(), "Audit trail should show at least one entry.");

                    // Close modal
                    WebElement closeModalBtn = auditModal.findElement(By.cssSelector("button.close-modal"));
                    closeModalBtn.click();
                    wait.until(ExpectedConditions.invisibilityOf(auditModal));
                    break;
                }
            }

            assertTrue(foundExpectedTxn, "Expected INR to JPY transaction entry not found in history.");

            // Additional verification - real-time update indicator present
            WebElement realTimeStatus = driver.findElement(By.id("realTimeStatusIndicator"));
            assertTrue(realTimeStatus.isDisplayed(), "Real-time status indicator should be visible on transaction history page.");
            String statusText = realTimeStatus.getText();
            assertTrue(statusText.toLowerCase().contains("up-to-date") || statusText.toLowerCase().contains("latest"),
                    "Real-time integration status text should indicate freshness.");

        } catch (NoSuchElementException nsee) {
            fail("Expected UI element not found during test execution: " + nsee.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception during test execution: " + e.getMessage());
        }
    }
}
