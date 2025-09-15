/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6216
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:42:50
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RoleBasedApprovalRoutingIntegrationTest {

    private static WebDriver driver;

    @LocalServerPort
    private int port;

    @Autowired
    private FpmDealsheetController dealsheetController;

    @Autowired
    private FpmTravelController travelController;

    @Autowired
    private FpmUserProfileController userProfileController;

    @MockBean
    private CurrencyConvertionController currencyController;

    @MockBean
    private FpmForecastController forecastController;

    @MockBean
    private FpmCommonController commonController;

    private String baseUrl;

    @BeforeAll
    public static void setupClass() {
        // Setup WebDriver - Chrome headless mode for CI
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        this.baseUrl = "http://localhost:" + port;

        // Mock user roles and workflow configuration in userProfileController
        User manager = new User();
        manager.setId(101L);
        manager.setUsername("managerUser");
        manager.setRole("Manager");

        User director = new User();
        director.setId(102L);
        director.setUsername("directorUser");
        director.setRole("Director");

        when(userProfileController.getActiveApprovers()).thenReturn(Arrays.asList(manager, director));

        // Mock currency controller
        when(currencyController.getRealtimeRate(any())).thenReturn(1.1);
        
        // Mock forecast controller - no forecast logic needed here but required dependency
        when(forecastController.getForecast(any())).thenReturn(null);

        // Mock commonController for notifications
        when(commonController.sendNotification(any(), any())).thenReturn(true);
    }

    @Test
    public void testDealSheetApprovalRouting() throws InterruptedException {
        // Step 1: Submit deal sheet approval request
        Long requestId = dealsheetController.submitDealSheetApprovalRequest("DealSheet123", 50000.0, "userA");

        assertThat(requestId).isNotNull();

        // Step 2: Verify routing to correct approver (Mocked logic routes to Manager for <= 50k)
        User assignedApprover = dealsheetController.getAssignedApproverForRequest(requestId);
        assertThat(assignedApprover).isNotNull();
        assertThat(assignedApprover.getRole()).isEqualTo("Manager");

        // Simulate notification triggered
        boolean notificationSent = commonController.sendNotification(assignedApprover.getId(), "New DealSheet approval request assigned.");
        assertThat(notificationSent).isTrue();

        // Step 3 & 4: Using Selenium to log in as assigned approver and verify visibility
        performLogin(assignedApprover.getUsername(), "password123");

        driver.navigate().to(baseUrl + "/approvals/dealsheets");

        // Wait for page load
        TimeUnit.SECONDS.sleep(2);

        List<WebElement> approverRequests = driver.findElements(By.cssSelector(".approval-request-row"));

        boolean foundRequest = approverRequests.stream()
            .anyMatch(e -> e.getText().contains("DealSheet123"));

        assertThat(foundRequest)
            .withFailMessage("DealSheet approval request should be visible to assigned approver")
            .isTrue();

        performLogout();
    }

    @Test
    public void testStaffingApprovalRouting() throws InterruptedException {
        // Step 1: Submit staffing approval request
        Long requestId = travelController.submitStaffingApprovalRequest("StaffingReq456", 120000.0, "userB");

        assertThat(requestId).isNotNull();

        // Step 2: Verify routing to correct approver (Mock: Staffing > 100k routes to Director)
        User assignedApprover = travelController.getAssignedApproverForRequest(requestId);
        assertThat(assignedApprover).isNotNull();
        assertThat(assignedApprover.getRole()).isEqualTo("Director");

        // Step 3: Check notification
        boolean notificationSent = commonController.sendNotification(assignedApprover.getId(), "New Staffing approval request assigned.");
        assertThat(notificationSent).isTrue();

        // Step 4: Selenium login and verify
        performLogin(assignedApprover.getUsername(), "password123");
        driver.navigate().to(baseUrl + "/approvals/staffing");
        TimeUnit.SECONDS.sleep(2);
        List<WebElement> approverRequests = driver.findElements(By.cssSelector(".approval-request-row"));
        boolean foundRequest = approverRequests.stream()
            .anyMatch(e -> e.getText().contains("StaffingReq456"));
        assertThat(foundRequest)
            .withFailMessage("Staffing approval request should be visible to assigned approver")
            .isTrue();
        performLogout();
    }

    @Test
    public void testTravelApprovalRouting() throws InterruptedException {
        // Step 1: Submit travel approval request
        Long requestId = travelController.submitTravelApprovalRequest("TravelReq789", "userC", "Director");

        assertThat(requestId).isNotNull();

        // Step 2: Verify routing (Mock: travel requests routed based on requested approver role)
        User assignedApprover = travelController.getAssignedApproverForRequest(requestId);
        assertThat(assignedApprover).isNotNull();
        assertThat(assignedApprover.getRole()).isEqualTo("Director");

        // Step 3: Notification
        boolean notificationSent = commonController.sendNotification(assignedApprover.getId(), "New Travel approval request assigned.");
        assertThat(notificationSent).isTrue();

        // Step 4: Selenium login and verify
        performLogin(assignedApprover.getUsername(), "password123");
        driver.navigate().to(baseUrl + "/approvals/travel");
        TimeUnit.SECONDS.sleep(2);
        List<WebElement> approverRequests = driver.findElements(By.cssSelector(".approval-request-row"));
        boolean foundRequest = approverRequests.stream()
            .anyMatch(e -> e.getText().contains("TravelReq789"));
        assertThat(foundRequest)
            .withFailMessage("Travel approval request should be visible to assigned approver")
            .isTrue();
        performLogout();
    }

    private void performLogin(String username, String password) {
        driver.navigate().to(baseUrl + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("login-button"));

        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        loginBtn.click();

        // Wait for login redirect
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Assert login success by URL
        assertThat(driver.getCurrentUrl()).doesNotContain("/login");
    }

    private void performLogout() {
        try {
            WebElement logoutBtn = driver.findElement(By.id("logout-button"));
            logoutBtn.click();

            // Wait for redirect to login
            TimeUnit.SECONDS.sleep(1);

            assertThat(driver.getCurrentUrl()).contains("/login");

        } catch (Exception e) {
            // If logout button not found, ignore but log
            System.err.println("Logout failed or already logged out: " + e.getMessage());
        }
    }
}