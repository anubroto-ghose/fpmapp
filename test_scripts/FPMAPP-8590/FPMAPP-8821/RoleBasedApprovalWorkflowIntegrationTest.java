/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8821
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-26 15:52:22
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RoleBasedApprovalWorkflowIntegrationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    // Mock user roles and thresholds
    private static final Map<String, Double> ROLE_THRESHOLDS = new HashMap<>();

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

        // Define role thresholds for test
        ROLE_THRESHOLDS.put("Manager", 10000.0);
        ROLE_THRESHOLDS.put("Director", 50000.0);
        ROLE_THRESHOLDS.put("VP", Double.MAX_VALUE); // Highest approver
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock user profiles with roles and thresholds
        Mockito.when(fpmUserProfileController.getUserRoleThreshold("user_manager"))
                .thenReturn(ROLE_THRESHOLDS.get("Manager"));
        Mockito.when(fpmUserProfileController.getUserRoleThreshold("user_director"))
                .thenReturn(ROLE_THRESHOLDS.get("Director"));
        Mockito.when(fpmUserProfileController.getUserRoleThreshold("user_vp"))
                .thenReturn(ROLE_THRESHOLDS.get("VP"));

        // Mock approval routing logic in FpmDealsheetController
        Mockito.when(fpmDealsheetController.routeApprovalRequest(Mockito.anyDouble()))
                .thenAnswer(invocation -> {
                    Double amount = invocation.getArgument(0);
                    if (amount <= ROLE_THRESHOLDS.get("Manager")) {
                        return "Manager";
                    } else if (amount <= ROLE_THRESHOLDS.get("Director")) {
                        return "Director";
                    } else {
                        return "VP";
                    }
                });
    }

    @Test
    public void testApprovalRequestRoutingBasedOnRoleAndThreshold() {
        driver.get("http://localhost:8080/approval-request");

        // Step 1: Submit approval request below first role threshold (e.g., 5000)
        submitApprovalRequest(5000.0);
        String routedRole1 = getRoutedApproverRole();
        assertEquals("Manager", routedRole1, "Request below first threshold should route to Manager");

        // Step 3: Submit approval request exceeding first but within next threshold (e.g., 30000)
        submitApprovalRequest(30000.0);
        String routedRole2 = getRoutedApproverRole();
        assertEquals("Director", routedRole2, "Request between thresholds should route to Director");

        // Step 5: Submit approval request exceeding all thresholds (e.g., 100000)
        submitApprovalRequest(100000.0);
        String routedRole3 = getRoutedApproverRole();
        assertEquals("VP", routedRole3, "Request exceeding all thresholds should route to VP");
    }

    private void submitApprovalRequest(double amount) {
        try {
            WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount")));
            amountInput.clear();
            amountInput.sendKeys(String.valueOf(amount));

            WebElement submitButton = driver.findElement(By.id("submitApprovalRequest"));
            submitButton.click();

            // Wait for routing result to appear
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("routedRole")));
        } catch (Exception e) {
            fail("Failed to submit approval request: " + e.getMessage());
        }
    }

    private String getRoutedApproverRole() {
        try {
            WebElement routedRoleElement = driver.findElement(By.id("routedRole"));
            return routedRoleElement.getText().trim();
        } catch (Exception e) {
            fail("Failed to retrieve routed approver role: " + e.getMessage());
            return null;
        }
    }
}
