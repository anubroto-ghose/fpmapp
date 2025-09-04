/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5263
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:17:37
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.controllers.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Integration test using Selenium WebDriver embedded with Spring Boot test context.
 * 
 * Tests successful logging of rejection actions by a compliance officer.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class FpmAuditTrailRejectionActionTest {

    private static WebDriver driver;
    
    @LocalServerPort
    private int port;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    private String baseUrl;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path (driver must be available on system PATH or specify absolute path here)
        String chromeDriverPath = System.getProperty("webdriver.chrome.driver");
        if (chromeDriverPath == null || chromeDriverPath.isEmpty()) {
            System.setProperty("webdriver.chrome.driver", "./chromedriver"); // Adjust for your env
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run headless to avoid opening browser windows during test
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        baseUrl = "http://localhost:" + port;

        // Mock user profile retrieval to simulate compliance officer
        when(fpmUserProfileController.getCurrentUser()).thenReturn(
                new com.webapp.fpmapp.entities.User("complianceOfficer", "Compliance", "Officer", "ROLE_COMPLIANCE"));

        // Mock audit logging response - simulate successful logging
        when(fpmCommonController.logAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);

        // Additional mocks can be set up here as needed
    }

    @Test
    public void testRejectionActionIsLoggedSuccessfully() {
        try {
            driver.get(baseUrl + "/login");

            // Login as compliance officer
            WebElement usernameInput = driver.findElement(By.id("username"));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginBtn"));

            usernameInput.sendKeys("complianceOfficer");
            passwordInput.sendKeys("password123");
            loginButton.click();

            // Wait for redirect after login and check landing page
            Thread.sleep(1500); // Basic wait for demo; in prod use WebDriverWait

            assertEquals(baseUrl + "/dashboard", driver.getCurrentUrl(), "User should be redirected to dashboard after login");

            // Navigate to a transaction details page
            driver.get(baseUrl + "/transactions/txn12345");

            // Perform a rejection action on the transaction
            WebElement rejectButton = driver.findElement(By.id("rejectTransactionBtn"));
            rejectButton.click();

            // Confirm rejection modal popup
            WebElement rejectionReasonInput = driver.findElement(By.id("rejectionReason"));
            rejectionReasonInput.sendKeys("Compliance check failed due to policy violation.");
            WebElement confirmRejectBtn = driver.findElement(By.id("confirmRejectBtn"));
            confirmRejectBtn.click();

            Thread.sleep(1000); // Wait for action processing

            // Verify that the UI shows success notification
            WebElement notification = driver.findElement(By.id("notification"));
            assertTrue(notification.isDisplayed(), "Notification should be displayed after rejection");
            assertTrue(notification.getText().toLowerCase().contains("rejected"), "Notification should mention rejection");

            // Navigate to audit logs page
            driver.get(baseUrl + "/audit-logs");

            Thread.sleep(1000); // Wait for audit log entries to load

            // Verify audit log contains the rejection action by compliance officer
            WebElement logsTable = driver.findElement(By.id("auditLogsTable"));
            assertNotNull(logsTable, "Audit logs table should be present");

            boolean rejectionLogged = logsTable.findElements(By.tagName("tr")).stream()
                    .anyMatch(tr -> {
                        String rowText = tr.getText().toLowerCase();
                        return rowText.contains("rejection") && 
                               rowText.contains("complianceofficer") &&
                               rowText.contains("txn12345");
                    });

            assertTrue(rejectionLogged, "Audit logs should contain the rejection action with correct user details");

            // Additional verification to check timestamp format
            WebElement rejectionRow = logsTable.findElements(By.tagName("tr")).stream()
                    .filter(tr -> tr.getText().toLowerCase().contains("rejection")
                            && tr.getText().toLowerCase().contains("complianceofficer")
                            && tr.getText().toLowerCase().contains("txn12345"))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Rejection log entry not found in audit logs"));

            // Sample timestamp format: 2024-06-10T14:23:45
            String rowText = rejectionRow.getText();
            String timestampRegex = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}";
            assertTrue(rowText.matches("(?s).*" + timestampRegex + ".*"), "Audit log entry should include a timestamp in ISO format");

        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }
}