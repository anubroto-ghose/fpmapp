/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6209
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:48:56
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import io.github.bonigarcia.wdm.WebDriverManager;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Selenium test class that tests the currency filter functionality for INR to JPY conversion transactions.
 * It verifies role-based approval restrictions and audit trail visibility.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyFilterINRtoJPYIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private com.webapp.fpmapp.services.CurrencyConvertionController currencyConversionService;

    @MockBean
    private com.webapp.fpmapp.services.FpmCommonController approvalCommonService;

    @MockBean
    private com.webapp.fpmapp.services.FpmForecastController forecastService;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
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

    /**
     * Prepares mock data for currency conversion history with INR to USD and INR to JPY.
     * Also mocks role-based approval visibility and audit trail responses.
     */
    private void prepareMockData() {
        // Mock currency conversion entries - realistic banking domain data
        List<CurrencyConversionRecord> allConversions = Arrays.asList(
                new CurrencyConversionRecord(101L, "INR", "USD", 75.50, "2025-09-10T12:30:00Z"),
                new CurrencyConversionRecord(102L, "INR", "JPY", 1.56, "2025-09-12T08:15:00Z"),
                new CurrencyConversionRecord(103L, "INR", "JPY", 1.58, "2025-09-14T14:45:00Z"),
                new CurrencyConversionRecord(104L, "USD", "JPY", 110.00, "2025-09-13T09:00:00Z")
        );

        Mockito.when(currencyConversionService.getConversionHistory()).thenReturn(allConversions);

        // Mock role-based approval filter: user has approval role that restricts view to INR->JPY only
        User testUser = new User();
        testUser.setId(1001L);
        testUser.setUsername("test_approver");
        testUser.setRoles(Arrays.asList("ROLE_APPROVER", "ROLE_VIEW_INR_TO_JPY"));

        Mockito.when(userProfileController.getCurrentUser()).thenReturn(testUser);

        // Mock approval restrictions applied by FpmCommonController
        Mockito.when(approvalCommonService.userHasAccessToCurrencyConversion(Mockito.any(User.class), Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    String fromCurrency = invocation.getArgument(1);
                    String toCurrency = invocation.getArgument(2);
                    // Only allow if user has ROLE_VIEW_INR_TO_JPY and currencies match
                    return user.getRoles().contains("ROLE_VIEW_INR_TO_JPY")
                            && "INR".equals(fromCurrency) && "JPY".equals(toCurrency);
                });

        // Mock audit trail data for conversion id 102 and 103
        List<AuditRecord> auditRecords = Arrays.asList(
                new AuditRecord(201L, 102L, 1001L, "APPROVAL_GRANTED", "2025-09-12T09:00:00Z", "Approved by role approver"),
                new AuditRecord(202L, 103L, 1001L, "APPROVAL_GRANTED", "2025-09-14T15:00:00Z", "Approved by role approver")
        );
        Mockito.when(approvalCommonService.getAuditTrail(102L)).thenReturn(auditRecords.subList(0,1));
        Mockito.when(approvalCommonService.getAuditTrail(103L)).thenReturn(auditRecords.subList(1,2));
    }

    @Test
    public void testFilterINRtoJPYCurrencyConversionsWithApprovalsAndAuditTrail() {
        prepareMockData();

        try {
            driver.get("http://localhost:8080/transaction-history");

            // Wait for page to load transaction history table
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyFilter")));

            // Select filter dropdown and choose "INR to JPY"
            WebElement currencyFilterDropdown = driver.findElement(By.id("currencyFilter"));
            currencyFilterDropdown.click();
            
            // Option with value "INR_JPY" assumed
            WebElement inrToJpyOption = driver.findElement(By.xpath("//option[@value='INR_JPY']"));
            inrToJpyOption.click();

            // Wait for filter to apply and table to update
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("transactionTable"), "INR"));

            List<WebElement> rows = driver.findElements(By.cssSelector("#transactionTable tbody tr"));

            // Assert that all rows only contain INR to JPY conversions
            assertFalse(rows.isEmpty(), "Filtered results should not be empty");

            for (WebElement row : rows) {
                String fromCurrency = row.findElement(By.cssSelector("td.fromCurrency")).getText();
                String toCurrency = row.findElement(By.cssSelector("td.toCurrency")).getText();

                assertEquals("INR", fromCurrency, "From currency should be INR");
                assertEquals("JPY", toCurrency, "To currency should be JPY");
            }

            // Test that audit trail links/buttons exist and show only allowed info
            for (WebElement row : rows) {
                WebElement auditTrailBtn = row.findElement(By.cssSelector("button.auditTrailButton"));
                assertNotNull(auditTrailBtn, "Audit trail button must be present");

                auditTrailBtn.click();

                // Wait for audit modal or panel to appear
                WebElement auditPanel = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailPanel")));

                String auditText = auditPanel.getText();

                // Confirm audit text contains approval granted action and timestamp
                assertTrue(auditText.contains("APPROVAL_GRANTED"), "Audit trail must contain approval granted action");

                // Close audit panel
                WebElement closeBtn = auditPanel.findElement(By.cssSelector("button.closeAuditPanel"));
                closeBtn.click();

                wait.until(ExpectedConditions.invisibilityOf(auditPanel));
            }

        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    // Entity classes used for mocking
    static class CurrencyConversionRecord {
        private Long id;
        private String fromCurrency;
        private String toCurrency;
        private Double rate;
        private String timestamp;

        public CurrencyConversionRecord(Long id, String fromCurrency, String toCurrency, Double rate, String timestamp) {
            this.id = id;
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.rate = rate;
            this.timestamp = timestamp;
        }

        public Long getId() { return id; }
        public String getFromCurrency() { return fromCurrency; }
        public String getToCurrency() { return toCurrency; }
        public Double getRate() { return rate; }
        public String getTimestamp() { return timestamp; }
    }

    static class AuditRecord {
        private Long auditId;
        private Long approvalId;
        private Long userId;
        private String actionType;
        private String actionTimestamp;
        private String remarks;

        public AuditRecord(Long auditId, Long approvalId, Long userId, String actionType, String actionTimestamp, String remarks) {
            this.auditId = auditId;
            this.approvalId = approvalId;
            this.userId = userId;
            this.actionType = actionType;
            this.actionTimestamp = actionTimestamp;
            this.remarks = remarks;
        }

        public Long getAuditId() { return auditId; }
        public Long getApprovalId() { return approvalId; }
        public Long getUserId() { return userId; }
        public String getActionType() { return actionType; }
        public String getActionTimestamp() { return actionTimestamp; }
        public String getRemarks() { return remarks; }
    }
}
