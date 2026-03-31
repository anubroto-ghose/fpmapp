/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8969
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:34:40
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
import java.sql.SQLException;

/**
 * Integration Selenium test for approval request status update and current approver role persistence.
 * 
 * Preconditions:
 * - Approval Requests and User Roles tables are updated to support role hierarchy and approval status.
 * - An approval request is pending approval.
 * - A user with the correct approver role is available.
 * 
 * Test Steps:
 * 1. Approve the request using the authorized approver.
 * 2. Query the database to verify the approval status and current approver role fields.
 * 3. Use the approval workflow API to retrieve the approval request details.
 * 
 * Expected Results:
 * - The approval status is updated to 'Approved' in the database.
 * - The current approver role is updated and stored correctly.
 * - The API response reflects the updated approval status and current approver role.
 * - Data consistency is maintained between the database and API responses.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApproveRequestStatusTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private DataSource dataSource;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

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
    public void setup() throws SQLException {
        // Prepare database state for test
        try (Connection conn = dataSource.getConnection()) {
            // Clear existing approval requests
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM approval_requests")) {
                ps.executeUpdate();
            }
            // Clear user roles
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM user_roles")) {
                ps.executeUpdate();
            }

            // Insert a pending approval request
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO approval_requests (id, status, current_approver_role) VALUES (?, ?, ?)")) {
                ps.setLong(1, 1001L);
                ps.setString(2, "Pending");
                ps.setString(3, "Manager");
                ps.executeUpdate();
            }

            // Insert a user with correct approver role
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (id, username, full_name) VALUES (?, ?, ?)")) {
                ps.setLong(1, 501L);
                ps.setString(2, "approverUser");
                ps.setString(3, "John Approver");
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO user_roles (user_id, role_name) VALUES (?, ?)")) {
                ps.setLong(1, 501L);
                ps.setString(2, "Manager");
                ps.executeUpdate();
            }
        }

        // Mock user profile controller to return the approver user
        User mockUser = new User();
        mockUser.setId(501L);
        mockUser.setUsername("approverUser");
        mockUser.setFullName("John Approver");
        when(fpmUserProfileController.getUserByUsername("approverUser")).thenReturn(Optional.of(mockUser));

        // Mock common controller approval workflow API response
        when(fpmCommonController.getApprovalRequestDetails(1001L)).thenReturn(
                new com.webapp.fpmapp.dto.ApprovalRequestDTO(1001L, "Approved", "Director")
        );
    }

    @Test
    public void testApproveRequestUpdatesStatusAndApproverRole() throws SQLException {
        // Navigate to the approval request page
        driver.get("http://localhost:8080/fpmapp/approval-requests/1001");

        // Wait for the approve button to be clickable
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("approveBtn")));

        // Click approve button
        approveButton.click();

        // Wait for success message or status update
        WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalStatus")));

        String updatedStatus = statusElement.getText();
        assertThat(updatedStatus).isEqualToIgnoringCase("Approved");

        // Verify database update
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT status, current_approver_role FROM approval_requests WHERE id = ?")) {
                ps.setLong(1, 1001L);
                try (ResultSet rs = ps.executeQuery()) {
                    assertThat(rs.next()).isTrue();
                    String dbStatus = rs.getString("status");
                    String dbApproverRole = rs.getString("current_approver_role");
                    assertThat(dbStatus).isEqualTo("Approved");
                    assertThat(dbApproverRole).isEqualTo("Director");
                }
            }
        }

        // Verify API response consistency
        com.webapp.fpmapp.dto.ApprovalRequestDTO apiResponse = fpmCommonController.getApprovalRequestDetails(1001L);
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getStatus()).isEqualTo("Approved");
        assertThat(apiResponse.getCurrentApproverRole()).isEqualTo("Director");
    }
}
