/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8631
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:53:01
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Integration Selenium test for real-time in-app notification updates on approval status change.
 * 
 * Preconditions:
 * - Approver is logged in and connected to real-time notification service.
 * - Approval request is assigned and pending.
 * 
 * Test Steps:
 * 1. Change approval status.
 * 2. Observe in-app notification update.
 * 3. Verify notification content.
 * 4. Confirm notification event logged in DB.
 * 
 * Uses mocked services for backend responses.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApprovalNotificationRealtimeUpdateTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @Autowired
    private DataSource dataSource;

    private AutoCloseable mocks;

    private final String baseUrl = "http://localhost:";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock backend service responses as needed
        // For example, mock currency conversion to return fixed rate
        when(currencyConvertionController.convertCurrency("USD", "EUR", 100.0)).thenReturn(85.0);

        // Mock forecast controller to return dummy forecast data
        when(fpmForecastController.getForecastById(anyLong())).thenReturn(
                new com.webapp.fpmapp.dto.FpmForecastController.ForecastDTO(1L, "Q2 Forecast", 100000.0));

        // Mock common controller for approval status update
        when(fpmCommonController.updateApprovalStatus(anyLong(), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(true);
    }

    @Test
    public void testRealTimeNotificationOnApprovalStatusChange() throws SQLException {
        // Step 0: Login as approver
        driver.get(baseUrl + port + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("approverUser");
        passwordInput.sendKeys("securePassword123");
        loginButton.click();

        // Wait for dashboard page to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 1: Ensure approval request is pending and visible
        driver.get(baseUrl + port + "/approvals/pending");

        // Locate the approval request row by some unique identifier (e.g. request id or description)
        WebElement approvalRequestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[td[contains(text(),'Request #12345')]]")
        ));

        assertThat(approvalRequestRow).isNotNull();

        // Step 2: Change approval status from pending to approved
        WebElement approveButton = approvalRequestRow.findElement(By.cssSelector("button.approve-btn"));
        approveButton.click();

        // Wait for status update confirmation
        WebElement statusCell = approvalRequestRow.findElement(By.cssSelector("td.status"));
        wait.until(ExpectedConditions.textToBePresentInElement(statusCell, "Approved"));

        // Step 3: Observe in-app notification area for real-time update
        WebElement notificationArea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notification-area")));

        // Wait up to 10 seconds for notification to appear
        WebElement notification = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='notification-area']//div[contains(@class,'notification') and contains(text(),'approved')]")
        ));

        assertThat(notification.getText()).contains("Request #12345");
        assertThat(notification.getText().toLowerCase()).contains("approved");
        assertThat(notification.getText()).contains("delegated to");

        // Step 4: Confirm notification event is logged in notification_logs DB table
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT notification_type, recipient_id, content FROM notification_logs WHERE content LIKE ? ORDER BY created_at DESC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%Request #12345%");
                try (ResultSet rs = ps.executeQuery()) {
                    assertThat(rs.next()).isTrue();
                    String notificationType = rs.getString("notification_type");
                    long recipientId = rs.getLong("recipient_id");
                    String content = rs.getString("content");

                    assertThat(notificationType).isEqualTo("APPROVAL_STATUS_UPDATE");
                    assertThat(recipientId).isGreaterThan(0);
                    assertThat(content).contains("approved");
                    assertThat(content).contains("Request #12345");
                }
            }
        }
    }
}
