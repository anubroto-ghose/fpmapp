/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8821
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:48:26
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration Selenium test for approval request routing based on user role and financial threshold.
 * 
 * Preconditions:
 * - User accounts with roles and thresholds exist (mocked).
 * - Approval workflow configured with role-based routing and thresholds (mocked).
 * 
 * This test uses mocked services to simulate backend responses and Selenium WebDriver
 * to interact with the UI.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApprovalRequestRoutingTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private FpmDealsheetController dealsheetController;

    @MockBean
    private FpmCommonController commonController;

    // Mocked user roles and thresholds
    private static final Map<String, Double> ROLE_THRESHOLDS = new HashMap<>();

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Define role thresholds (example realistic banking roles)
        // Role hierarchy: JuniorApprover < SeniorApprover < ManagerApprover
        ROLE_THRESHOLDS.put("JuniorApprover", 10000.0); // up to 10k
        ROLE_THRESHOLDS.put("SeniorApprover", 50000.0); // up to 50k
        ROLE_THRESHOLDS.put("ManagerApprover", Double.MAX_VALUE); // above 50k
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user profile service to return roles and thresholds
        when(userProfileController.getUserRoleThresholds()).thenReturn(ROLE_THRESHOLDS);

        // Mock approval workflow routing logic
        when(commonController.routeApprovalRequest(any(Double.class))).thenAnswer(invocation -> {
            Double amount = invocation.getArgument(0);
            if (amount <= ROLE_THRESHOLDS.get("JuniorApprover")) {
                return "JuniorApprover";
            } else if (amount <= ROLE_THRESHOLDS.get("SeniorApprover")) {
                return "SeniorApprover";
            } else {
                return "ManagerApprover";
            }
        });
    }

    /**
     * Helper method to submit approval request via UI.
     * @param amount the financial amount to submit
     */
    private void submitApprovalRequest(double amount) {
        driver.get("http://localhost:8080/approval-request");

        // Wait for page load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amountInput")));

        WebElement amountInput = driver.findElement(By.id("amountInput"));
        amountInput.clear();
        amountInput.sendKeys(String.valueOf(amount));

        WebElement submitButton = driver.findElement(By.id("submitApprovalRequestBtn"));
        submitButton.click();
    }

    /**
     * Helper method to get routed approver role from UI after submission.
     * @return routed approver role as String
     */
    private String getRoutedApproverRole() {
        // Wait for routing result element
        WebElement routedRoleElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("routedApproverRole")));
        return routedRoleElement.getText().trim();
    }

    @Test
    public void testApprovalRequestRoutingBelowFirstThreshold() {
        double amount = 5000.0; // below JuniorApprover threshold
        submitApprovalRequest(amount);

        String routedRole = getRoutedApproverRole();
        assertThat(routedRole).isEqualTo("JuniorApprover");
    }

    @Test
    public void testApprovalRequestRoutingBetweenFirstAndSecondThreshold() {
        double amount = 30000.0; // between JuniorApprover and SeniorApprover
        submitApprovalRequest(amount);

        String routedRole = getRoutedApproverRole();
        assertThat(routedRole).isEqualTo("SeniorApprover");
    }

    @Test
    public void testApprovalRequestRoutingAboveAllThresholds() {
        double amount = 100000.0; // above SeniorApprover threshold
        submitApprovalRequest(amount);

        String routedRole = getRoutedApproverRole();
        assertThat(routedRole).isEqualTo("ManagerApprover");
    }

    @Test
    public void testApprovalRequestRoutingInvalidAmount() {
        double amount = -100.0; // invalid negative amount
        submitApprovalRequest(amount);

        WebElement errorElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        String errorMessage = errorElement.getText();
        assertThat(errorMessage).contains("Invalid amount");
    }

}
