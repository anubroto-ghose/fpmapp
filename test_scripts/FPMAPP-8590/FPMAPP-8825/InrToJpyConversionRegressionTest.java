/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8825
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:55:25
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Selenium test for regression: Verify correct data is displayed for each INR to JPY conversion entry
 * Regression based on epic test cases FPMAPP-2345 and FPMAPP-6789
 * 
 * Preconditions:
 * - At least one INR to JPY transaction exists with role-based approval and audit trail enabled
 * 
 * This test mocks backend services to simulate real-time currency conversion and audit trail data.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class InrToJpyConversionRegressionTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private FpmForecastController fpmForecastController;

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

    @Test
    public void testInrToJpyConversionEntryDisplay() {
        // Mock backend service responses
        // Mock currency conversion rate INR -> JPY
        double mockConversionRate = 1.5; // Example: 1 INR = 1.5 JPY

        Mockito.when(currencyConvertionController.getConversionRate("INR", "JPY"))
                .thenReturn(mockConversionRate);

        // Mock transaction data with role-based approval and audit trail enabled
        // For simplicity, assume a transaction with:
        // Date: 26-03-2026
        // Amount INR: 5000
        // Status: Completed
        // Audit trail: 2 entries

        // Since we do not have direct API calls here, assume the UI page will render this data
        // The test will verify UI elements accordingly

        // Navigate to transaction history page
        driver.get("http://localhost:8080/transaction-history");

        // Wait for the transaction table to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

        // Locate all rows in the transaction table
        List<WebElement> rows = driver.findElements(By.cssSelector("#transactionTable tbody tr"));

        assertFalse(rows.isEmpty(), "Transaction table should not be empty");

        boolean foundInrToJpyEntry = false;

        DateTimeFormatter expectedDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);

        for (WebElement row : rows) {
            try {
                // Columns assumed: Date | From Currency | To Currency | Amount From | Amount To | Status | Audit Trail Link
                String dateText = row.findElement(By.cssSelector("td.date")).getText().trim();
                String fromCurrency = row.findElement(By.cssSelector("td.fromCurrency")).getText().trim();
                String toCurrency = row.findElement(By.cssSelector("td.toCurrency")).getText().trim();

                if ("INR".equalsIgnoreCase(fromCurrency) && "JPY".equalsIgnoreCase(toCurrency)) {
                    foundInrToJpyEntry = true;

                    // Validate date format
                    LocalDate parsedDate = null;
                    try {
                        parsedDate = LocalDate.parse(dateText, expectedDateFormat);
                    } catch (Exception e) {
                        fail("Date format is incorrect for INR to JPY entry: " + dateText);
                    }
                    assertNotNull(parsedDate, "Parsed date should not be null");

                    // Validate amount in INR
                    String amountInrText = row.findElement(By.cssSelector("td.amountFrom")).getText().trim();
                    assertTrue(amountInrText.startsWith("₹"), "Amount in INR should start with ₹");
                    String amountInrNumberStr = amountInrText.replaceAll("[^0-9.]", "");
                    double amountInr = Double.parseDouble(amountInrNumberStr);
                    assertEquals(5000.0, amountInr, 0.01, "Amount in INR should be 5000");

                    // Validate amount in JPY
                    String amountJpyText = row.findElement(By.cssSelector("td.amountTo")).getText().trim();
                    assertTrue(amountJpyText.endsWith("¥"), "Amount in JPY should end with ¥");
                    String amountJpyNumberStr = amountJpyText.replaceAll("[^0-9.]", "");
                    double amountJpy = Double.parseDouble(amountJpyNumberStr);
                    double expectedJpy = amountInr * mockConversionRate;
                    assertEquals(expectedJpy, amountJpy, 0.1, "Amount in JPY should be correctly converted");

                    // Validate status
                    String statusText = row.findElement(By.cssSelector("td.status")).getText().trim();
                    assertEquals("Completed", statusText, "Status should be Completed");

                    // Validate audit trail link and details
                    WebElement auditTrailLink = row.findElement(By.cssSelector("td.auditTrail a"));
                    assertNotNull(auditTrailLink, "Audit trail link should be present");

                    // Click audit trail link to open modal or navigate
                    auditTrailLink.click();

                    // Wait for audit trail modal/dialog
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailModal")));

                    WebElement auditModal = driver.findElement(By.id("auditTrailModal"));
                    List<WebElement> auditEntries = auditModal.findElements(By.cssSelector(".audit-entry"));
                    assertTrue(auditEntries.size() >= 1, "Audit trail should have at least one entry");

                    // Validate audit entries content
                    for (WebElement entry : auditEntries) {
                        String actionType = entry.findElement(By.cssSelector(".action-type")).getText().trim();
                        String performedBy = entry.findElement(By.cssSelector(".performed-by")).getText().trim();
                        String actionTimestamp = entry.findElement(By.cssSelector(".action-timestamp")).getText().trim();

                        assertFalse(actionType.isEmpty(), "Audit action type should not be empty");
                        assertFalse(performedBy.isEmpty(), "Audit performed by should not be empty");
                        assertFalse(actionTimestamp.isEmpty(), "Audit action timestamp should not be empty");
                    }

                    // Close audit trail modal
                    WebElement closeBtn = auditModal.findElement(By.cssSelector("button.close"));
                    closeBtn.click();

                    // Wait for modal to disappear
                    wait.until(ExpectedConditions.invisibilityOf(auditModal));

                    // Since we found and validated one INR->JPY entry, break loop
                    break;
                }
            } catch (Exception e) {
                fail("Exception during validation of transaction row: " + e.getMessage());
            }
        }

        assertTrue(foundInrToJpyEntry, "At least one INR to JPY conversion entry should be present in the transaction history");
    }
}
