/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6206
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:51:07
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

import org.openqa.selenium.support.ui.ExpectedCondition;

import java.time.Duration;

/**
 * Integration test verifying correct data displayed for INR to JPY currency conversion history.
 * 
 * It validates data formatting, real-time currency conversion, audit trails,
 * and role-based access control.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FpmCurrencyConversionHistoryIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmTravelController travelController;

    @MockBean
    private FpmCommonController commonController;

    @MockBean
    private FpmForecastController forecastController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void setUp() {
        // Setup ChromeDriver (assuming ChromeDriver executable is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // headless for CI
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDown() {
        if(driver != null) {
            driver.quit();
        }
    }

    /**
     * Test steps based on regression scenario REGRESSION-FPMAPP-6167-TC02
     */
    @Test
    public void testInrToJpyConversionEntryDisplayWithAuditAndRoleBasedAccess() throws Exception {

        // Prepare test data
        String testApprovalId = "APPROVAL-12345";
        String testUserId = "user_001";
        String testUserRole = "ROLE_COMPLIANCE_OFFICER"; // sufficient role to view

        // Prepare a User mock for role-based access
        User mockUser = new User();
        mockUser.setId(testUserId);
        mockUser.setUsername("compliance.user");
        mockUser.setRoles(List.of(testUserRole));

        when(userProfileController.getUserById(testUserId)).thenReturn(mockUser);

        // Prepare mock for currency conversion history API
        // Suppose endpoint returns history entries with audit summary included
        Map<String, Object> conversionEntry = Map.of(
            "transactionId", "TXN1001",
            "fromCurrency", "INR",
            "toCurrency", "JPY",
            "amountFrom", 5000.0,
            "amountTo", 7500.0,  // pretend conversion rate 1.5
            "conversionDate", "15-09-2025",
            "status", "Completed",
            "auditTrail", List.of(
                Map.of("action", "Submitted", "user", "requester.user", "timestamp", "14-09-2025 08:00:00"),
                Map.of("action", "Approved", "user", "approver.user", "timestamp", "15-09-2025 06:45:00")
            )
        );

        when(currencyConvertionController.getConversionHistory(anyString(), anyString()))
            .thenReturn(List.of(conversionEntry));

        // Simulate login as mock user with role
        driver.get("http://localhost:8080/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(mockUser.getUsername());
        driver.findElement(By.id("password")).sendKeys("dummyPassword");
        driver.findElement(By.id("loginButton")).click();

        // Wait till dashboard or home page loaded
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to transaction history page
        driver.get("http://localhost:8080/transactions/history");

        // Wait for page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyConversionHistoryTable")));

        // Locate INR to JPY conversion entry row by transactionId
        WebElement table = driver.findElement(By.id("currencyConversionHistoryTable"));

        WebElement targetRow = null;
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        for (WebElement row : rows) {
            try {
                WebElement txnCell = row.findElement(By.className("transactionId"));
                if (txnCell.getText().equals("TXN1001")) {
                    targetRow = row;
                    break;
                }
            } catch (Exception e) {
                // Row does not have transactionId cell
            }
        }

        // Assert the row exists
        assert targetRow != null : "Transaction with ID TXN1001 should exist in table";

        // Validate Date format in DD-MM-YYYY
        WebElement dateCell = targetRow.findElement(By.className("conversionDate"));
        String dateText = dateCell.getText();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(dateText, formatter);
        } catch (Exception e) {
            throw new AssertionError("Date format is incorrect. Expected DD-MM-YYYY but got: " + dateText);
        }

        // Validate Amount in INR displayed correctly
        WebElement amountFromCell = targetRow.findElement(By.className("amountFrom"));
        String amountFromText = amountFromCell.getText();
        // Should be like ₹5000
        if (!amountFromText.matches("₹\\d+(\\.\\d{1,2})?")) {
            throw new AssertionError("Amount in INR is not displayed correctly: " + amountFromText);
        }

        // Validate Amount in JPY is correct with mock rate considered
        WebElement amountToCell = targetRow.findElement(By.className("amountTo"));
        String amountToText = amountToCell.getText();

        // Parsing amounts - remove currency symbols
        double amountFromValue = extractAmount(amountFromText, "₹");
        double amountToValue = extractAmount(amountToText, "¥");

        // Expected mock conversion rate used in mock is 1.5
        double expectedAmountTo = amountFromValue * 1.5;

        // Allow minimal floating diff
        if (Math.abs(amountToValue - expectedAmountTo) > 0.01) {
            throw new AssertionError(String.format("Converted amount incorrect. Expected approx %.2f but found %.2f", expectedAmountTo, amountToValue));
        }

        // Validate status display
        WebElement statusCell = targetRow.findElement(By.className("status"));
        String statusText = statusCell.getText();
        if (!statusText.equalsIgnoreCase("Completed")) {
            throw new AssertionError("Status should be Completed but found: " + statusText);
        }

        // Validate Audit Trail Data is available and accurate
        WebElement auditButton = targetRow.findElement(By.className("auditTrailButton"));
        auditButton.click();

        // Wait for audit modal/dialog
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailModal")));

        WebElement auditModal = driver.findElement(By.id("auditTrailModal"));
        List<WebElement> auditEntries = auditModal.findElements(By.className("auditEntry"));

        // Verify at least 2 audit entries as per mock
        if (auditEntries.size() < 2) {
            throw new AssertionError("Audit trail should have at least 2 entries but found " + auditEntries.size());
        }

        boolean foundSubmit = false;
        boolean foundApprove = false;

        for (WebElement auditEntry : auditEntries) {
            String action = auditEntry.findElement(By.className("actionType")).getText();
            String user = auditEntry.findElement(By.className("actionUser")).getText();
            String timestamp = auditEntry.findElement(By.className("actionTimestamp")).getText();

            if (action.equalsIgnoreCase("Submitted") && user.equalsIgnoreCase("requester.user")) {
                foundSubmit = true;
            }

            if (action.equalsIgnoreCase("Approved") && user.equalsIgnoreCase("approver.user")) {
                foundApprove = true;
            }

            if (!timestamp.matches("\d{2}-\d{2}-\d{4} \d{2}:\d{2}:\d{2}")) {
                throw new AssertionError("Audit timestamp format invalid for entry: " + action);
            }
        }

        if (!foundSubmit || !foundApprove) {
            throw new AssertionError("Audit trail does not contain required approval stages: submission and approval");
        }

        // Validate role-based visibility: For ROLE_COMPLIANCE_OFFICER all data shown
        // For unauthorized role, audit button should NOT be present or disabled - simulate by switching user

        // Logout first
        driver.findElement(By.id("logoutButton")).click();
        wait.until(ExpectedConditions.urlContains("/login"));

        // Login as unauthorized role
        User unauthorizedUser = new User();
        unauthorizedUser.setId("user_unauth");
        unauthorizedUser.setUsername("unauthorized.user");
        unauthorizedUser.setRoles(List.of("ROLE_BASIC_USER"));
        when(userProfileController.getUserById("user_unauth")).thenReturn(unauthorizedUser);

        driver.findElement(By.id("username")).sendKeys(unauthorizedUser.getUsername());
        driver.findElement(By.id("password")).sendKeys("dummyPassword");
        driver.findElement(By.id("loginButton")).click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));

        driver.get("http://localhost:8080/transactions/history");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyConversionHistoryTable")));

        WebElement tableUnauthorized = driver.findElement(By.id("currencyConversionHistoryTable"));
        WebElement rowUnauthorized = null;
        List<WebElement> rowsUnauthorized = tableUnauthorized.findElements(By.tagName("tr"));
        for (WebElement row : rowsUnauthorized) {
            try {
                WebElement txnCell = row.findElement(By.className("transactionId"));
                if (txnCell.getText().equals("TXN1001")) {
                    rowUnauthorized = row;
                    break;
                }
            } catch (Exception e) {
                // ignore
            }
        }

        assert rowUnauthorized != null : "Transaction row should appear for unauthorized role";

        boolean auditButtonPresent;
        try {
            rowUnauthorized.findElement(By.className("auditTrailButton"));
            auditButtonPresent = true;
        } catch (Exception e) {
            auditButtonPresent = false;
        }

        if (auditButtonPresent) {
            throw new AssertionError("Unauthorized user must not see audit trail button");
        }
    }

    /**
     * Helper method to extract numerical amount from a text with currency symbol
     * @param text the currency text (e.g. "₹5000")
     * @param symbol the currency symbol (e.g. "₹")
     * @return parsed double amount
     */
    private double extractAmount(String text, String symbol) {
        try {
            String value = text.replace(symbol, "").replace(",", "").trim();
            return Double.parseDouble(value);
        } catch (Exception e) {
            throw new AssertionError("Failed to parse amount from text: " + text);
        }
    }
}
