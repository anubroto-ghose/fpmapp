/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5256
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:55:52
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.FpmAppApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webapp.fpmapp.services.CurrencyIntegrationService;
import com.webapp.fpmapp.services.AuditTrailService;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Integration Selenium test class for verifying the successful admin override of currency rates.
 * Preconditions: Admin user is logged in.
 * 
 * This test mocks the CurrencyIntegrationService to simulate service behavior.
 * It verifies that currency rate is updated, notification alert is triggered,
 * and audit log is recorded correctly.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FpmAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrencyOverrideAdminTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @MockBean
    private CurrencyIntegrationService currencyIntegrationService;

    @MockBean
    private AuditTrailService auditTrailService;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String ADMIN_USERNAME = "adminUser";
    private static final String ADMIN_PASSWORD = "adminPass123";

    @BeforeAll
    public void setUpClass() {
        // Setup WebDriver path if required.
        // Example for ChromeDriver system property setup - assumes chromedriver binary is in PATH
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run headless for CI/CD
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock the currencyIntegrationService behavior for override
        Mockito.doNothing().when(currencyIntegrationService).updateOverride(Mockito.anyString(), Mockito.anyDouble(), Mockito.anyString());
        Mockito.doNothing().when(currencyIntegrationService).notifyOverride(Mockito.anyString(), Mockito.anyDouble(), Mockito.anyString());

        // Mock the auditTrailService logAction to verify call happened
        Mockito.doNothing().when(auditTrailService).logAction(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @AfterAll
    public void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Test Case: Successful currency rate override by admin.
     *
     * Steps:
     * 1. Login as admin.
     * 2. Navigate to currency rate management section.
     * 3. Select a currency.
     * 4. Enter a new override rate.
     * 5. Submit the override.
     *
     * Expected Results:
     * - The currency rate is updated successfully.
     * - An alert is shown confirming override.
     * - Audit trail service logs the override action.
     */
    @Test
    public void testSuccessfulCurrencyRateOverrideByAdmin() {
        // 1. Login as Admin
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.clear();
        usernameInput.sendKeys(ADMIN_USERNAME);
        passwordInput.clear();
        passwordInput.sendKeys(ADMIN_PASSWORD);
        loginButton.click();

        // Verify login success by presence of admin dashboard link or element
        wait.until(ExpectedConditions.urlContains("/admin/dashboard"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/admin/dashboard"), "Admin dashboard URL expected after login.");

        // 2. Navigate to currency rate management section
        WebElement currencyManagementLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-currency-management")));
        currencyManagementLink.click();

        wait.until(ExpectedConditions.urlContains("/admin/currency-management"));

        // 3. Select a currency - example USD
        WebElement currencyDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("currency-select")));
        currencyDropdown.click();
        WebElement usdOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='USD']")));
        usdOption.click();

        // 4. Enter a new override rate
        WebElement rateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("override-rate")));
        rateInput.clear();
        double newRate = 1.25;
        rateInput.sendKeys(String.valueOf(newRate));

        // 5. Submit the override
        WebElement submitButton = driver.findElement(By.id("submit-override"));
        submitButton.click();

        // Wait for alert confirming override
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            String alertText = driver.switchTo().alert().getText();
            Assertions.assertTrue(alertText.toLowerCase().contains("override"), "Alert message should confirm override action.");
            driver.switchTo().alert().accept();
        } catch (NoAlertPresentException ex) {
            Assertions.fail("Expected alert not displayed upon currency override submission.");
        }

        // Verify that currencyIntegrationService.updateOverride() was called with correct params
        Mockito.verify(currencyIntegrationService, Mockito.times(1)).updateOverride(Mockito.eq("USD"), Mockito.eq(newRate), Mockito.anyString());

        // Verify that notification was triggered
        Mockito.verify(currencyIntegrationService, Mockito.times(1)).notifyOverride(Mockito.eq("USD"), Mockito.eq(newRate), Mockito.anyString());

        // Verify that audit trail logAction was called
        Mockito.verify(auditTrailService, Mockito.times(1)).logAction(Mockito.anyString(), Mockito.contains("USD"), Mockito.anyString());
    }
}
