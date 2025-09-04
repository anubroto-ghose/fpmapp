/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5262
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:18:03
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.controllers.FpmDealsheetController;
import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // Runs on fixed port for Selenium WebDriver
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalActionAuditLogIntegrationTest {

    private static WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080";

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @BeforeAll
    public static void setUpClass() {
        // Set path to chromedriver executable if needed, e.g. System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setUpMocks() {
        // Mock the audit log retrieval - simulate audit logs stored with approval action
        Mockito.when(fpmCommonController.getAuditLogs()).thenReturn(
            List.of(
                new AuditLogEntry("compliance_officer_user", "APPROVAL", Instant.now().minusSeconds(30)),
                new AuditLogEntry("another_user", "REJECTION", Instant.now().minusSeconds(60))
            )
        );
    }

    /**
     * End-to-end integration test that covers:
     * - Logging in as compliance officer user
     * - Performing approval action on a transaction
     * - Verifying the audit log for the approval action with correct details
     */
    @Test
    public void testSuccessfulLoggingOfApprovalActions() {
        try {
            driver.get(BASE_URL + "/login");

            // Step 1: Login as compliance officer
            WebElement usernameInput = driver.findElement(By.id("username"));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("login-button"));

            usernameInput.sendKeys("compliance_officer");
            passwordInput.sendKeys("securePassword123");
            loginButton.click();

            // Wait until dashboard page loads
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("/dashboard"));

            // Verify user is logged in
            WebElement welcomeBanner = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("welcome-banner")));
            Assertions.assertTrue(welcomeBanner.getText().contains("compliance_officer"), "User login failed or wrong user logged in.");

            // Step 2: Perform approval action on transaction
            driver.get(BASE_URL + "/transactions/pending");

            // Assume a transaction row with approval button exists with known transaction id
            WebElement transactionRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tr[data-transaction-id='TXN12345']")));
            WebElement approveButton = transactionRow.findElement(By.cssSelector("button.approve-action"));

            approveButton.click();

            // Wait for confirmation alert
            WebElement confirmationAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("action-confirmation")));
            Assertions.assertTrue(confirmationAlert.getText().contains("approved"), "Approval confirmation message not shown.");

            // Step 3: Access audit logs
            driver.get(BASE_URL + "/audit-logs");

            // Wait for audit logs table
            WebElement auditLogTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("audit-log-table")));

            // Find approval action row matching compliance_officer user and APPROVAL action
            List<WebElement> logRows = auditLogTable.findElements(By.tagName("tr"));
            boolean foundApprovalLog = false;
            for (WebElement row : logRows) {
                List<WebElement> cols = row.findElements(By.tagName("td"));
                if (cols.size() < 3) continue; // insufficient columns

                String logUser = cols.get(0).getText();
                String logAction = cols.get(1).getText();
                String logTimestamp = cols.get(2).getText();

                if ("compliance_officer".equalsIgnoreCase(logUser) && "APPROVAL".equalsIgnoreCase(logAction)) {
                    // Validate timestamp is parseable and recent
                    Instant timestampInstant = Instant.parse(logTimestamp);
                    Instant now = Instant.now();
                    Assertions.assertTrue(!timestampInstant.isAfter(now), "Audit log timestamp cannot be in the future");
                    Assertions.assertTrue(now.minusSeconds(300).isBefore(timestampInstant), "Audit log timestamp is too old");
                    foundApprovalLog = true;
                    break;
                }
            }

            Assertions.assertTrue(foundApprovalLog, "Approval action not found in audit logs for compliance_officer.");

        } catch (Exception e) {
            Assertions.fail("Test failed due to unexpected error: " + e.getMessage(), e);
        }
    }

    // Helper DTO class to mock audit log entries
    static class AuditLogEntry {
        private final String user;
        private final String action;
        private final Instant timestamp;

        public AuditLogEntry(String user, String action, Instant timestamp) {
            this.user = user;
            this.action = action;
            this.timestamp = timestamp;
        }

        public String getUser() {
            return user;
        }

        public String getAction() {
            return action;
        }

        public Instant getTimestamp() {
            return timestamp;
        }
    }
}
