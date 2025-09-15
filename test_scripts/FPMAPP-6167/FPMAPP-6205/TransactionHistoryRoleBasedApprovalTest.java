/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6205
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:51:43
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class TransactionHistoryRoleBasedApprovalTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConversionController;

    @MockBean
    private FpmUserProfileController userProfileController;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().window().maximize();
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mocking user profile to simulate logged-in user with role based approval
        when(userProfileController.getCurrentUserRoles(anyString()))
            .thenReturn(Arrays.asList("ROLE_APPROVER", "ROLE_USER"));

        // Mocking currency conversion history for INR to JPY including audit data
        when(currencyConversionController.getConversionHistory(eq("INR"), eq("JPY"), anyInt(), anyInt()))
            .thenReturn(generateMockTransactionHistory());
    }

    private List<CurrencyConvertionController.ConversionTransactionDTO> generateMockTransactionHistory() {
        CurrencyConvertionController.ConversionTransactionDTO tx1 = new CurrencyConvertionController.ConversionTransactionDTO(
            "TX123456",
            "2025-09-14T10:15:30Z",
            "INR",
            100000.0,
            "JPY",
            1500000.0,
            "Completed",
            Arrays.asList(
                new CurrencyConvertionController.AuditEntryDTO("user1", "APPROVED", "2025-09-14T10:17:00Z", "Approved for processing"),
                new CurrencyConvertionController.AuditEntryDTO("user2", "REVIEWED", "2025-09-14T10:18:00Z", "Reviewed by compliance")
            )
        );

        CurrencyConvertionController.ConversionTransactionDTO tx2 = new CurrencyConvertionController.ConversionTransactionDTO(
            "TX654321",
            "2025-09-10T09:00:00Z",
            "INR",
            50000.0,
            "JPY",
            750000.0,
            "Failed",
            Arrays.asList(
                new CurrencyConvertionController.AuditEntryDTO("user3", "REJECTED", "2025-09-10T09:05:00Z", "Insufficient funds")
            )
        );

        return Arrays.asList(tx1, tx2);
    }

    @Test
    public void testAccessConversionHistoryWithRoleBasedApprovalAndAuditTrail() {
        try {
            // Step 1: Log in simulation - navigating directly to login page and setting user session if required
            driver.get("http://localhost:8080/login");

            // Assuming login form with username & password fields
            WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginBtn"));

            usernameInput.clear();
            usernameInput.sendKeys("testuser");
            passwordInput.clear();
            passwordInput.sendKeys("P@ssw0rd");
            loginButton.click();

            // Step 2: Navigate to transaction history page
            wait.until(ExpectedConditions.urlContains("/dashboard"));
            driver.get("http://localhost:8080/transaction-history");

            // Step 3: Filter/select INR to JPY currency pair
            WebElement fromCurrencySelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("fromCurrency")));
            fromCurrencySelect.click();
            WebElement fromOption = driver.findElement(By.xpath("//option[@value='INR']"));
            fromOption.click();

            WebElement toCurrencySelect = driver.findElement(By.id("toCurrency"));
            toCurrencySelect.click();
            WebElement toOption = driver.findElement(By.xpath("//option[@value='JPY']"));
            toOption.click();

            WebElement filterButton = driver.findElement(By.id("filterBtn"));
            filterButton.click();

            // Step 4: Verify role-based view permissions (UI shows expected transactions only)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionTable")));

            WebElement transactionTable = driver.findElement(By.id("transactionTable"));
            List<WebElement> rows = transactionTable.findElements(By.tagName("tr"));

            // We expect 2 transactions based on mock
            assertThat(rows.size()).isEqualTo(3); // 1 header + 2 rows

            // Verify the details of 1st transaction
            WebElement firstDataRow = rows.get(1);
            List<WebElement> columns = firstDataRow.findElements(By.tagName("td"));

            assertThat(columns.get(0).getText()).isEqualTo("2025-09-14T10:15:30Z"); // date
            assertThat(columns.get(1).getText()).isEqualTo("100000.0"); // amount in INR
            assertThat(columns.get(2).getText()).isEqualTo("1500000.0"); // equivalent in JPY
            assertThat(columns.get(3).getText()).isEqualTo("Completed"); // transaction status

            // Audit trail indicator presence (e.g. an icon or a link)
            WebElement auditIndicator = columns.get(4).findElement(By.cssSelector(".audit-trail-indicator"));
            assertThat(auditIndicator).isNotNull();

            // Clicking audit trail indicator opens audit trail panel or modal
            auditIndicator.click();
            WebElement auditTrailPanel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auditTrailPanel")));
            assertThat(auditTrailPanel.isDisplayed()).isTrue();

            // Verify audit entries inside audit trail panel
            List<WebElement> auditRows = auditTrailPanel.findElements(By.cssSelector(".audit-entry"));
            assertThat(auditRows.size()).isEqualTo(2);
            // Check content of first audit entry
            WebElement firstAuditEntry = auditRows.get(0);
            assertThat(firstAuditEntry.getText()).contains("user1");
            assertThat(firstAuditEntry.getText()).contains("APPROVED");
            assertThat(firstAuditEntry.getText()).contains("Approved for processing");

        } catch (Exception e) {
            e.printStackTrace();
            assertThat(false).withFailMessage("Test failed due to exception: " + e.getMessage()).isTrue();
        }
    }

}
