/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5270
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:15:18
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.controllers.FpmCommonController;
import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmForecastController;

import java.time.Duration;

/**
 * Integration Selenium test verifying auditor's access to approval history.
 * 
 * Preconditions:
 * - Auditor user is logged in with auditor role.
 * - Approval history data available in system.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // Assuming app runs on fixed port
@ActiveProfiles("test")
public class AuditorApprovalHistoryIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private final String baseUrl = "http://localhost:8080"; // Adjust if different

    @MockBean
    private FpmDealsheetController fpmDealsheetController; // Mock dealsheet service

    @MockBean
    private FpmCommonController fpmCommonController; // For audit history retrieval etc.

    @MockBean
    private FpmUserProfileController userProfileController; // User profile data if needed

    @MockBean
    private FpmForecastController fpmForecastController; // mock other dependencies if needed

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver executable property (assuming chromedriver in system PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocksAndLogin() {
        MockitoAnnotations.openMocks(this);

        // Mock response for audit history - example data
        // This data simulates approval history entries for the auditor
        List<ApprovalHistoryEntry> mockedApprovalHistory = Arrays.asList(
                new ApprovalHistoryEntry("DealSheet-001", "ManagerA", "APPROVED", "auditor01", LocalDateTime.of(2025, 8, 25, 10, 15)),
                new ApprovalHistoryEntry("DealSheet-002", "DirectorB", "REJECTED", "auditor01", LocalDateTime.of(2025, 8, 26, 11, 45)),
                new ApprovalHistoryEntry("DealSheet-003", "ManagerC", "APPROVED", "auditor01", LocalDateTime.of(2025, 9, 1, 9, 30))
        );

        when(fpmCommonController.getApprovalAuditHistory(any(), any(), any()))
                .thenReturn(mockedApprovalHistory);

        // Mock logged in user to be auditor role
        User auditorUser = new User();
        auditorUser.setUsername("auditor01");
        auditorUser.setRoles(Arrays.asList("AUDITOR"));
        when(userProfileController.getCurrentUser()).thenReturn(auditorUser);

        // Navigate to login page and authenticate as auditor user
        // For integration test, assume UI login page exists at /login and autofills
        driver.get(baseUrl + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.clear();
        usernameInput.sendKeys("auditor01");
        passwordInput.clear();
        passwordInput.sendKeys("securePassword");
        loginButton.click();

        // Wait till redirect to home/dashboard page
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void testAuditorCanViewCompleteApprovalHistoryWithTimestampsAndUserDetails() {
        // 1. Navigate to audit history section
        driver.get(baseUrl + "/audit-history");

        // 2. Select a time frame: e.g. last 10 days
        WebElement startDateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startDate")));
        WebElement endDateInput = driver.findElement(By.id("endDate"));
        WebElement searchButton = driver.findElement(By.id("searchBtn"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDate = LocalDateTime.now().minusDays(10).format(formatter);
        String endDate = LocalDateTime.now().format(formatter);

        startDateInput.clear();
        startDateInput.sendKeys(startDate);
        endDateInput.clear();
        endDateInput.sendKeys(endDate);
        searchButton.click();

        // Wait for results table to load
        WebElement resultsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalHistoryTable")));

        // 3. Check the history for deal sheets processed
        List<WebElement> rows = resultsTable.findElements(By.tagName("tr"));

        // There should be header + 3 data rows
        // Minimal assertion that rows > 1
        assertThat(rows.size()).isGreaterThan(1);

        // Verify contents in each row
        boolean dealSheet001Found = false;
        boolean dealSheet002Found = false;
        boolean dealSheet003Found = false;

        for (int i = 1; i < rows.size(); i++) { // skip header
            WebElement row = rows.get(i);
            List<WebElement> cells = row.findElements(By.tagName("td"));
            assertThat(cells.size()).isGreaterThanOrEqualTo(5); // columns: dealSheet, approver, status, auditedBy, timestamp
            String dealSheetId = cells.get(0).getText();
            String approver = cells.get(1).getText();
            String status = cells.get(2).getText();
            String auditedByUser = cells.get(3).getText();
            String timestampStr = cells.get(4).getText();

            assertThat(auditedByUser).isEqualTo("auditor01");

            // Validate timestamp parses
            LocalDateTime timestamp = null;
            try {
                timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                throw new AssertionError("Timestamp format invalid for row " + i + ": " + timestampStr);
            }
            assertThat(timestamp).isNotNull();

            switch (dealSheetId) {
                case "DealSheet-001":
                    dealSheet001Found = true;
                    assertThat(approver).isEqualTo("ManagerA");
                    assertThat(status).isEqualToIgnoringCase("APPROVED");
                    break;
                case "DealSheet-002":
                    dealSheet002Found = true;
                    assertThat(approver).isEqualTo("DirectorB");
                    assertThat(status).isEqualToIgnoringCase("REJECTED");
                    break;
                case "DealSheet-003":
                    dealSheet003Found = true;
                    assertThat(approver).isEqualTo("ManagerC");
                    assertThat(status).isEqualToIgnoringCase("APPROVED");
                    break;
            }
        }

        assertThat(dealSheet001Found).isTrue();
        assertThat(dealSheet002Found).isTrue();
        assertThat(dealSheet003Found).isTrue();
    }

    // Inner class simulating minimal approval history DTO for mocking
    public static class ApprovalHistoryEntry {
        private String dealSheetId;
        private String approver;
        private String status;
        private String auditedBy;
        private LocalDateTime actionTimestamp;

        public ApprovalHistoryEntry(String dealSheetId, String approver, String status, String auditedBy, LocalDateTime actionTimestamp) {
            this.dealSheetId = dealSheetId;
            this.approver = approver;
            this.status = status;
            this.auditedBy = auditedBy;
            this.actionTimestamp = actionTimestamp;
        }

        // Getters required for JSON serialization and consistency if needed
        public String getDealSheetId() {
            return dealSheetId;
        }

        public String getApprover() {
            return approver;
        }

        public String getStatus() {
            return status;
        }

        public String getAuditedBy() {
            return auditedBy;
        }

        public LocalDateTime getActionTimestamp() {
            return actionTimestamp;
        }
    }
}
