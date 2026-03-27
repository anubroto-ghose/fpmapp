/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8818
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:50:25
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Integration test using Selenium WebDriver and Spring Boot context
 * to verify audit trail logging of approval actions.
 * 
 * Preconditions:
 * - Approval_Audit_Trail table exists and accessible
 * - User is authenticated and authorized
 * - System configured to log audit trail entries
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalAuditTrailIntegrationTest {

    private WebDriver driver;

    @Autowired
    private DataSource dataSource;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private final String baseUrl = "http://localhost:8080";

    private final String testUserId = "user123";
    private final String testUsername = "compliance_officer";

    @BeforeEach
    public void setUp() {
        // Setup ChromeDriver (headless for CI environments)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        // Mock user profile service to return authenticated user
        User mockUser = new User();
        mockUser.setId(testUserId);
        mockUser.setUsername(testUsername);
        when(fpmUserProfileController.getAuthenticatedUser()).thenReturn(mockUser);

        // Mock other services as needed
        when(currencyConvertionController.convert(any(), any(), any())).thenReturn(1.0);
        when(fpmForecastController.getForecast(any())).thenReturn(Optional.empty());
        when(fpmCommonController.isAuditTrailEnabled()).thenReturn(true);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testApprovalActionCreatesAuditTrailEntry() throws Exception {
        // Step 1: Login as authorized user
        driver.get(baseUrl + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(testUsername);
        passwordInput.sendKeys("password123"); // assuming test password
        loginButton.click();

        // Wait for redirect to dashboard
        Thread.sleep(1500);
        assertThat(driver.getCurrentUrl()).endsWith("/dashboard");

        // Step 2: Navigate to approval request page
        driver.get(baseUrl + "/approval/requests/valid-approval-request-id");

        // Step 3: Perform approval action with comments
        WebElement approveButton = driver.findElement(By.id("approveBtn"));
        WebElement commentsInput = driver.findElement(By.id("approvalComments"));

        String approvalComment = "Approved after review - all checks passed.";
        commentsInput.sendKeys(approvalComment);
        approveButton.click();

        // Wait for approval processing
        Thread.sleep(2000);

        // Step 4: Verify success message
        WebElement successMsg = driver.findElement(By.id("approvalSuccessMsg"));
        assertThat(successMsg.isDisplayed()).isTrue();
        assertThat(successMsg.getText()).contains("Approval successful");

        // Step 5: Verify audit trail entry in DB
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT action_type, timestamp, user_id, username, comments FROM Approval_Audit_Trail " +
                         "WHERE user_id = ? AND action_type = 'approval' ORDER BY timestamp DESC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, testUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    assertThat(rs.next()).isTrue();
                    String actionType = rs.getString("action_type");
                    Timestamp timestamp = rs.getTimestamp("timestamp");
                    String userId = rs.getString("user_id");
                    String username = rs.getString("username");
                    String comments = rs.getString("comments");

                    // Assertions
                    assertThat(actionType).isEqualTo("approval");
                    assertThat(timestamp.toInstant()).isBefore(Instant.now().plusSeconds(5));
                    assertThat(userId).isEqualTo(testUserId);
                    assertThat(username).isEqualTo(testUsername);
                    assertThat(comments).isEqualTo(approvalComment);

                    // Verify immutability: try to update the record (simulate)
                    String updateSql = "UPDATE Approval_Audit_Trail SET comments = ? WHERE user_id = ? AND timestamp = ?";
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setString(1, "Tampered comment");
                        updatePs.setString(2, userId);
                        updatePs.setTimestamp(3, timestamp);
                        int rowsUpdated = updatePs.executeUpdate();
                        // Expect 0 rows updated because audit trail entries are immutable
                        assertThat(rowsUpdated).isEqualTo(0);
                    }
                }
            }
        }
    }
}
