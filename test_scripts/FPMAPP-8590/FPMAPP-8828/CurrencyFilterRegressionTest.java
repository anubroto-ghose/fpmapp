/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8828
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:57:16
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.controllers.FpmCommonController;
import com.webapp.fpmapp.controllers.FpmTravelController;
import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmForecastController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
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
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock currency conversion history with multiple entries
        // Including INR to USD and INR to JPY
        List<String> currencyHistory = Arrays.asList(
                "INR to USD",
                "INR to JPY",
                "USD to EUR"
        );

        // Mock method to return currency conversion history
        Mockito.when(currencyConvertionController.getConversionHistory())
                .thenReturn(currencyHistory);

        // Mock approval status and audit trail for INR to JPY
        Mockito.when(fpmCommonController.getApprovalStatusForCurrencyPair("INR to JPY"))
                .thenReturn("Approved");

        Mockito.when(fpmCommonController.getAuditTrailForCurrencyPair("INR to JPY"))
                .thenReturn(Arrays.asList(
                        "2026-03-25 10:00:00 - Approved by Manager",
                        "2026-03-26 12:00:00 - Delegated to Director"
                ));

        // Mock role-based approval restrictions
        Mockito.when(fpmUserProfileController.getUserRoles())
                .thenReturn(Arrays.asList("Manager", "Director"));
    }

    @Test
    public void testCurrencyFilterINRtoJPY() {
        try {
            // Step 1: Navigate to transaction history page
            driver.get("http://localhost:8080/transaction-history");

            // Wait for page to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currency-filter")));

            // Step 2: Use the currency filter to select "INR to JPY"
            WebElement currencyFilter = driver.findElement(By.id("currency-filter"));
            currencyFilter.click();

            // Select option "INR to JPY" from dropdown
            WebElement inrToJpyOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[text()='INR to JPY']")));
            inrToJpyOption.click();

            // Submit or trigger filter action
            WebElement filterButton = driver.findElement(By.id("filter-submit"));
            filterButton.click();

            // Step 3: Verify that audit trail and approval status are displayed correctly for filtered entries
            // Wait for filtered results to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transaction-list")));

            // Verify only INR to JPY conversions are displayed
            List<WebElement> transactions = driver.findElements(By.cssSelector("#transaction-list .transaction-item"));
            assertFalse(transactions.isEmpty(), "No transactions displayed after filtering");

            for (WebElement transaction : transactions) {
                String currencyPair = transaction.findElement(By.cssSelector(".currency-pair")).getText();
                assertEquals("INR to JPY", currencyPair, "Filtered transaction contains unexpected currency pair");

                // Verify approval status is displayed and correct
                String approvalStatus = transaction.findElement(By.cssSelector(".approval-status")).getText();
                assertEquals("Approved", approvalStatus, "Approval status mismatch for transaction");

                // Verify audit trail is visible and consistent
                WebElement auditTrailElement = transaction.findElement(By.cssSelector(".audit-trail"));
                assertNotNull(auditTrailElement, "Audit trail element missing for transaction");
                String auditTrailText = auditTrailElement.getText();
                assertTrue(auditTrailText.contains("Approved by Manager") || auditTrailText.contains("Delegated to Director"),
                        "Audit trail content incorrect or missing");
            }

            // Verify that other currency pairs are excluded
            // For robustness, check that no transaction with other currency pairs is present
            List<WebElement> otherCurrencyTransactions = driver.findElements(By.xpath("//div[@id='transaction-list']//div[contains(@class,'transaction-item') and not(.//span[contains(@class,'currency-pair') and text()='INR to JPY'])]"));
            assertTrue(otherCurrencyTransactions.isEmpty(), "Transactions with other currency pairs are displayed");

            // Verify role-based approval restrictions respected
            // For example, check that approval buttons or actions are disabled if user role is insufficient
            // Here we assume user roles are Manager and Director, so approval actions should be enabled
            for (WebElement transaction : transactions) {
                WebElement approveButton = transaction.findElement(By.cssSelector(".btn-approve"));
                assertTrue(approveButton.isEnabled(), "Approve button should be enabled for authorized roles");
            }

        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }
}
