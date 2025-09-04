/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5268
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:14:33
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmDealsheetController;

import java.time.Duration;

/**
 * Integration test for role-based approval on deal sheets.
 * Validates the approval workflow via Selenium WebDriver.
 * Mocks service layer responses.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FpmDealsheetApprovalIntegrationTest {

    private static WebDriver driver;

    @Autowired
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmCommonController fpmCommonController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String APPROVALS_SECTION_PATH = "/approvals";

    @BeforeAll
    public static void setup() {
        // Set ChromeDriver path or rely on webdriver.manager
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Headless for CI environments
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Test role-based approval workflow for deal sheets.
     * Preconditions: Logged in user has approval rights.
     * Steps:
     * 1. Navigate to approvals section.
     * 2. Select a deal sheet request.
     * 3. Click Approve.
     * Assertions:
     * - Approval processed.
     * - Notification sent.
     */
    @Test
    public void testRoleBasedApprovalForDealSheet() {
        // Mock service behavior for approval processing
        when(fpmCommonController.approveDealSheet(anyLong(), org.mockito.ArgumentMatchers.anyString())).thenReturn(true);
        when(fpmCommonController.sendNotification(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())).thenReturn(true);

        // Perform login simulation: For simplicity, navigate directly or set authenticated context
        driver.get(BASE_URL + "/login");

        // Simulated login
        waitAndSendKeys(By.id("username"), "fin_manager_user");
        waitAndSendKeys(By.id("password"), "securePassword123");
        waitAndClick(By.id("loginButton"));

        // Wait for navigation to dashboard/home
        waitForUrl(BASE_URL + "/dashboard");

        // Navigate to Approvals section
        driver.get(BASE_URL + APPROVALS_SECTION_PATH);
        waitForUrl(BASE_URL + APPROVALS_SECTION_PATH);

        // Wait to load list of pending deal sheet requests
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".deal-sheet-request")));

        // Select the first deal sheet request
        WebElement firstRequest = driver.findElement(By.cssSelector(".deal-sheet-request"));
        assertThat(firstRequest).isNotNull();

        // Click on it to open details / modal
        firstRequest.click();

        // Wait for Approve button to be clickable
        By approveButtonLocator = By.id("approveDealSheetBtn");
        wait.until(ExpectedConditions.elementToBeClickable(approveButtonLocator));

        // Click Approve button
        waitAndClick(approveButtonLocator);

        // Wait for confirmation message 
        By confirmationMsgLocator = By.id("approvalSuccessMessage");
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationMsgLocator));

        WebElement confirmationMsg = driver.findElement(confirmationMsgLocator);
        assertThat(confirmationMsg.getText()).containsIgnoringCase("Approval successfully processed");

        // Verify that service methods were called as expected
        verify(fpmCommonController, times(1)).approveDealSheet(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.eq("fin_manager_user"));
        verify(fpmCommonController, times(1)).sendNotification(org.mockito.ArgumentMatchers.eq("requester@example.com"), org.mockito.ArgumentMatchers.contains("approved"));
    }

    private void waitAndClick(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    private void waitAndSendKeys(By locator, String keys) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(keys);
    }

    private void waitForUrl(String expectedUrl) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean urlMatches = wait.until(driver -> driver.getCurrentUrl().equalsIgnoreCase(expectedUrl));
        assertThat(urlMatches).isTrue();
    }
}
