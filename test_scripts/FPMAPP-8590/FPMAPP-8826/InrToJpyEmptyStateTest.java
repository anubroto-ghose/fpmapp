/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8826
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:55:56
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Selenium test for regression FPMAPP-8826:
 * Verify empty state handling when no INR to JPY conversions exist.
 * 
 * Preconditions:
 * - User logged in with role-based approval enabled
 * - No previous INR to JPY transactions exist
 * 
 * Test Steps:
 * 1. Navigate to transaction history page
 * 2. Filter to INR to JPY conversions
 * 
 * Expected:
 * - "No INR to JPY transactions found" message displayed
 * - No unrelated transactions shown
 * - Message respects role-based access and audit trail visibility
 * - UI remains consistent and responsive
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class InrToJpyEmptyStateTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController userProfileController;

    private static final String BASE_URL = "http://localhost:8080";

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
    public void setupMocks() {
        // Mock user profile with role-based approval enabled
        Mockito.when(userProfileController.getCurrentUserRole()).thenReturn("Manager");
        Mockito.when(userProfileController.isRoleBasedApprovalEnabled()).thenReturn(true);

        // Mock currency conversion history to return empty list for INR to JPY
        Mockito.when(currencyConvertionController.getConversionHistory("INR", "JPY", "Manager"))
                .thenReturn(Collections.emptyList());
    }

    @Test
    public void testEmptyStateForInrToJpyConversions() {
        try {
            // Step 1: Navigate to transaction history page
            driver.get(BASE_URL + "/transactions/history");

            // Wait for page to load main container
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transaction-history-container")));

            // Step 2: Filter to INR to JPY conversions
            WebElement fromCurrencySelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("filter-from-currency")));
            fromCurrencySelect.click();
            WebElement fromInrOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='INR']")));
            fromInrOption.click();

            WebElement toCurrencySelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("filter-to-currency")));
            toCurrencySelect.click();
            WebElement toJpyOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='JPY']")));
            toJpyOption.click();

            // Click filter button
            WebElement filterButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("filter-submit")));
            filterButton.click();

            // Wait for results or empty state message
            WebElement emptyStateMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("empty-state-message")));

            // Assertions
            String expectedMessage = "No INR to JPY transactions found";
            assertEquals(expectedMessage, emptyStateMessage.getText().trim(), "Empty state message should be displayed correctly.");

            // Verify no transaction rows are displayed
            List<WebElement> transactionRows = driver.findElements(By.cssSelector("#transaction-history-table tbody tr"));
            assertTrue(transactionRows.isEmpty(), "No transaction rows should be displayed when empty.");

            // Verify UI responsiveness: check filter controls still enabled
            assertTrue(fromCurrencySelect.isEnabled(), "From currency filter should remain enabled.");
            assertTrue(toCurrencySelect.isEnabled(), "To currency filter should remain enabled.");
            assertTrue(filterButton.isEnabled(), "Filter button should remain enabled.");

        } catch (Exception e) {
            fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }
}
