/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5270
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:13:33
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class AuditorApprovalHistoryIntegrationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @BeforeAll
    public static void setupClass() {
        // Use headless Chrome for CI environments
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // Mocking service that would normally provide audit/approval history data
        // Sample realistic data
        List<ApprovalHistoryRecord> mockHistory = Arrays.asList(
            new ApprovalHistoryRecord("DealSheet#1234", "approved", "john.doe", LocalDateTime.of(2024, 6, 10, 14, 30)),
            new ApprovalHistoryRecord("DealSheet#1235", "rejected", "jane.smith", LocalDateTime.of(2024, 6, 11, 9, 45))
        );

        when(fpmCommonController.getApprovalHistory(any(), any(), any())).thenReturn(mockHistory);
    }

    @Test
    @DisplayName("FPMAPP-5251-TC03: Verify auditor's access to approval history")
    public void testAuditorAccessApprovalHistory() {
        try {
            // Step 1: Auditor login
            driver.get("http://localhost:" + port + "/login");

            WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginBtn"));

            usernameInput.sendKeys("auditor_user");
            passwordInput.sendKeys("securePass123");
            loginButton.click();

            // Verify redirected to auditor's dashboard
            wait.until(ExpectedConditions.urlContains("/auditor/dashboard"));

            // Step 2: Navigate to audit history section
            WebElement auditHistoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("navAuditHistory")));
            auditHistoryLink.click();

            wait.until(ExpectedConditions.urlContains("/auditor/audit-history"));

            // Step 3: Select time frame
            WebElement fromDateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fromDate")));
            WebElement toDateInput = driver.findElement(By.id("toDate"));
            WebElement filterButton = driver.findElement(By.id("filterHistoryBtn"));

            fromDateInput.clear();
            fromDateInput.sendKeys("2024-06-01");
            toDateInput.clear();
            toDateInput.sendKeys("2024-06-15");
            filterButton.click();

            // Step 4: Verify approval history is displayed correctly
            By historyTableLocator = By.id("approvalHistoryTable");
            wait.until(ExpectedConditions.visibilityOfElementLocated(historyTableLocator));

            WebElement historyTable = driver.findElement(historyTableLocator);
            List<WebElement> rows = historyTable.findElements(By.tagName("tr"));

            // We expect at least header + 2 rows of mocked data
            assertThat(rows.size()).isGreaterThanOrEqualTo(3);

            // Verify each record's details
            boolean foundFirstRecord = false;
            boolean foundSecondRecord = false;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (int i = 1; i < rows.size(); i++) { // skip header
                List<WebElement> cols = rows.get(i).findElements(By.tagName("td"));
                if (cols.size() < 4) {
                    throw new AssertionError("Expected 4 columns in approval history table, got " + cols.size());
                }
                String dealSheet = cols.get(0).getText();
                String status = cols.get(1).getText();
                String user = cols.get(2).getText();
                String timestamp = cols.get(3).getText();

                if (dealSheet.equals("DealSheet#1234") && status.equals("approved") && user.equals("john.doe") 
                        && timestamp.equals("2024-06-10 14:30")) {
                    foundFirstRecord = true;
                }
                if (dealSheet.equals("DealSheet#1235") && status.equals("rejected") && user.equals("jane.smith")
                        && timestamp.equals("2024-06-11 09:45")) {
                    foundSecondRecord = true;
                }
            }

            assertThat(foundFirstRecord).as("First mocked approval history record must be present").isTrue();
            assertThat(foundSecondRecord).as("Second mocked approval history record must be present").isTrue();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AssertionError("Test execution failed due to exception: " + ex.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Inner DTO class for mocking approval history records (Assuming not provided)
    public static class ApprovalHistoryRecord {
        private String dealSheetId;
        private String status;
        private String user;
        private LocalDateTime timestamp;

        public ApprovalHistoryRecord(String dealSheetId, String status, String user, LocalDateTime timestamp) {
            this.dealSheetId = dealSheetId;
            this.status = status;
            this.user = user;
            this.timestamp = timestamp;
        }

        public String getDealSheetId() {
            return dealSheetId;
        }

        public String getStatus() {
            return status;
        }

        public String getUser() {
            return user;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
