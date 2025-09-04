/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5268
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:40:30
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DealSheetApprovalSeleniumIT {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private WebDriverWait wait;

    // Mock dependencies to isolate the approval behavior
    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private final String baseUrl = "http://localhost:";

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void teardownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupTest() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock the deal sheet approval - simulate successful approval process
        Mockito.when(fpmDealsheetController.approveDealSheetRequest(eq(1001L), any(String.class)))
                .thenReturn(ResponseEntity.ok("Approval successful"));

        // Mock the notification behavior - simulate notification sent status
        Mockito.when(fpmCommonController.sendApprovalNotification(eq(1001L), any(String.class)))
                .thenReturn(true);
    }

    @Test
    public void testRoleBasedDealSheetApprovalProcess() {
        try {
            // Step 1: Navigate to login page and login with role that has approval rights
            driver.get(baseUrl + port + "/login");

            WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginButton"));

            usernameInput.sendKeys("financeManagerUser");
            passwordInput.sendKeys("SecurePa55!");
            loginButton.click();

            // Verify login success by waiting for dashboard/approvals UI
            wait.until(ExpectedConditions.urlContains("/dashboard"));

            // Step 2: Navigate to the approvals section
            WebElement approvalsMenu = wait.until(ExpectedConditions.elementToBeClickable(By.id("menu-approvals")));
            approvalsMenu.click();

            // Wait for approvals list to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dealSheetRequestsList")));

            // Step 3: Select a deal sheet request to approve
            // We assume a list where each deal sheet has an approve button with id pattern 'approve-btn-<requestId>'
            WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("approve-btn-1001")));
            approveButton.click();

            // Step 4: Confirm approval modal is displayed and click confirm
            WebElement confirmDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalConfirmDialog")));
            WebElement confirmApproveButton = confirmDialog.findElement(By.id("confirmApproveButton"));
            confirmApproveButton.click();

            // Step 5: Wait for success message
            WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approvalSuccessMessage")));
            assertTrue(successMsg.getText().contains("Approval successful"), "Success message check");

            // Verify the approve button is disabled or removed after approval
            assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("approve-btn-1001"))), 
                       "Approve button should be disabled or disappear after approval");

            // Additional verification that notification was sent - verify the mocked method was called
            Mockito.verify(fpmCommonController).sendApprovalNotification(eq(1001L), eq("financeManagerUser"));

        } catch (Exception ex) {
            fail("Test failed with unexpected exception: " + ex.getMessage());
        }
    }
}