/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8812
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:54:24
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for currency override submission by an administrator.
 * 
 * Preconditions:
 * - User logged in as admin
 * - POST /api/currency/override accessible
 * 
 * Validates:
 * - API success response
 * - Override log entry creation
 * - Alert generation
 * - UI visibility of override flags/details
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CurrencyOverrideAdminTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController userProfileController;

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String ADMIN_USERNAME = "adminUser";
    private static final String ADMIN_PASSWORD = "adminPass123";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 10);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock user profile to simulate logged-in admin user
        User adminUser = new User();
        adminUser.setId(1001L);
        adminUser.setUsername(ADMIN_USERNAME);
        adminUser.setRole("ADMIN");
        when(userProfileController.getCurrentUser()).thenReturn(adminUser);

        // Mock currency override API response
        when(currencyConvertionController.overrideCurrencyRate(any(), any(), any()))
            .thenReturn(ResponseEntity.ok(Map.of("status", "success", "message", "Override applied")));

        // Mock log query response
        when(currencyConvertionController.getOverrideLogs())
            .thenReturn(List.of(Map.of(
                "userId", 1001L,
                "timestamp", Instant.now().toString(),
                "reason", "Urgent business need",
                "newRate", 1.25
            )));

        // Mock alert generation
        when(currencyConvertionController.getAlerts())
            .thenReturn(List.of(Map.of(
                "alertId", "ALERT123",
                "type", "CURRENCY_OVERRIDE",
                "message", "Currency override applied by adminUser"
            )));

        // Mock currency data with override flags
        when(currencyConvertionController.getCurrencyData())
            .thenReturn(List.of(Map.of(
                "currencyCode", "USD",
                "rate", 1.25,
                "overrideFlag", true,
                "overrideReason", "Urgent business need"
            )));
    }

    @Test
    public void testSuccessfulCurrencyOverrideSubmission() throws Exception {
        // Step 1: Login as admin user via UI
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(ADMIN_USERNAME);
        passwordInput.sendKeys(ADMIN_PASSWORD);
        loginButton.click();

        // Wait for dashboard or home page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Submit override request via UI form
        driver.get(BASE_URL + "/currency/override");

        WebElement currencyCodeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyCode")));
        WebElement newRateInput = driver.findElement(By.id("newRate"));
        WebElement reasonInput = driver.findElement(By.id("overrideReason"));
        WebElement submitButton = driver.findElement(By.id("submitOverride"));

        currencyCodeInput.sendKeys("USD");
        newRateInput.sendKeys("1.25");
        reasonInput.sendKeys("Urgent business need");
        submitButton.click();

        // Step 3: Verify success message on UI
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        assertThat(successMsg.getText()).contains("Override applied successfully");

        // Step 4: Verify API response via direct call
        Map<String, Object> requestPayload = Map.of(
            "currencyCode", "USD",
            "newRate", 1.25,
            "reason", "Urgent business need"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
            BASE_URL + "/api/currency/override",
            requestPayload,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("success");

        // Step 5: Verify override log entry
        List<Map<String, Object>> logs = currencyConvertionController.getOverrideLogs();
        assertThat(logs).isNotEmpty();
        Map<String, Object> latestLog = logs.get(0);
        assertThat(latestLog.get("userId")).isEqualTo(1001L);
        assertThat(latestLog.get("reason")).isEqualTo("Urgent business need");
        assertThat(latestLog.get("newRate")).isEqualTo(1.25);
        assertThat(latestLog.get("timestamp")).isNotNull();

        // Step 6: Verify alert generated
        List<Map<String, Object>> alerts = currencyConvertionController.getAlerts();
        assertThat(alerts).isNotEmpty();
        Map<String, Object> alert = alerts.get(0);
        assertThat(alert.get("type")).isEqualTo("CURRENCY_OVERRIDE");
        assertThat(alert.get("message")).contains("adminUser");

        // Step 7: Verify override flags and details visible in currency data
        List<Map<String, Object>> currencyData = currencyConvertionController.getCurrencyData();
        assertThat(currencyData).isNotEmpty();
        Map<String, Object> usdData = currencyData.stream()
            .filter(d -> "USD".equals(d.get("currencyCode")))
            .findFirst()
            .orElseThrow(() -> new AssertionError("USD currency data not found"));

        assertThat(usdData.get("overrideFlag")).isEqualTo(true);
        assertThat(usdData.get("overrideReason")).isEqualTo("Urgent business need");
        assertThat(usdData.get("rate")).isEqualTo(1.25);
    }
}
