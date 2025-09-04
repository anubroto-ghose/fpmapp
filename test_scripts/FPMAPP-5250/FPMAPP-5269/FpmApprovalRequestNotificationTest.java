/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5269
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:14:02
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;

import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class FpmApprovalRequestNotificationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed (assuming chromedriver in PATH)
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().window().maximize();
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mocking the call that fetches current user profile
        when(fpmCommonController.getCurrentUser()).thenReturn(
                new com.webapp.fpmapp.entities.User(1001L, "jane.manager@bank.com", "Jane Manager", "ROLE_FINANCIAL_MANAGER")
        );

        // Mocking Currency Conversion
        when(currencyConvertionController.convert(any(String.class), any(String.class), any(Double.class)))
                .thenAnswer(invocation -> invocation.getArgument(2)); // return amount as is

        // Mocking Forecast Controller (if needed for request submission)
        when(fpmForecastController.calculateForecast(any())).thenReturn(1200.0);
    }

    @Test
    public void testNotificationOnTravelExpenseRequestSubmission() {
        String baseUrl = "http://localhost:" + port + "/fpmapp";

        // Step 1: User logs in (simulate login)
        driver.get(baseUrl + "/login");

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("loginBtn"));

        usernameField.clear();
        usernameField.sendKeys("jane.manager@bank.com");
        passwordField.clear();
        passwordField.sendKeys("SecurePassword123");
        loginBtn.click();

        // Validate login success by user homepage or dashboard presence
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Navigate to Travel Expense Request Submission page
        driver.get(baseUrl + "/travel/submit");

        // Fill form with realistic data
        WebElement destinationField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("destination")));
        WebElement startDateField = driver.findElement(By.id("startDate"));
        WebElement endDateField = driver.findElement(By.id("endDate"));
        WebElement amountField = driver.findElement(By.id("amount"));
        WebElement currencyField = driver.findElement(By.id("currency"));
        WebElement submitBtn = driver.findElement(By.id("submitRequest"));

        destinationField.clear();
        destinationField.sendKeys("New York, NY");
        startDateField.clear();
        startDateField.sendKeys("2024-07-01");
        endDateField.clear();
        endDateField.sendKeys("2024-07-05");
        amountField.clear();
        amountField.sendKeys("1500");
        currencyField.clear();
        currencyField.sendKeys("USD");

        submitBtn.click();

        // Step 3: Wait and verify notification
        try {
            WebElement notification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notificationMessage")));

            String notificationText = notification.getText();
            assertThat(notificationText).isNotEmpty();
            assertThat(notificationText).containsIgnoringCase("request submission");
            assertThat(notificationText).containsIgnoringCase("confirmed");

        } catch (Exception e) {
            throw new AssertionError("Notification not received or incorrect after travel expense request submission", e);
        }
    }
}