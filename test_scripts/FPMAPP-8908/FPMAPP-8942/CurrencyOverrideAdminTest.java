/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8942
 * Epic: FPMAPP-8908
 * Generated on: 2026-03-31 14:48:22
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;

import org.springframework.boot.web.server.LocalServerPort;

/**
 * Integration Selenium test for admin override of currency rates with reason logging and alert triggering.
 * 
 * Preconditions:
 * - Admin user authenticated and authorized
 * - Currency rate data exists
 * - Alerting system operational
 * 
 * This test mocks service layers and verifies UI and API behavior.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CurrencyOverrideAdminTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    private final String adminUsername = "adminUser";
    private final String adminPassword = "adminPass123";

    private final String currencyCode = "USD";
    private final double originalRate = 1.0;
    private final double overriddenRate = 1.15;
    private final String overrideReason = "Quarterly adjustment due to market volatility";

    private final String alertMessage = "Currency rate USD overridden by admin";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver path if needed
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() {
        // Mock authentication for admin user
        when(fpmUserProfileController.isUserAdmin(adminUsername)).thenReturn(true);

        // Mock existing currency rate
        Map<String, Object> existingRate = new HashMap<>();
        existingRate.put("currencyCode", currencyCode);
        existingRate.put("rate", originalRate);
        existingRate.put("adminOverrideFlag", false);
        when(currencyConvertionController.getCurrencyRate(currencyCode)).thenReturn(existingRate);

        // Mock alerting system
        doNothing().when(fpmCommonController).triggerAlert(anyString());

        // Mock override update
        doAnswer(invocation -> {
            String currCode = invocation.getArgument(0);
            double newRate = invocation.getArgument(1);
            String reason = invocation.getArgument(2);
            Map<String, Object> updatedRate = new HashMap<>();
            updatedRate.put("currencyCode", currCode);
            updatedRate.put("rate", newRate);
            updatedRate.put("adminOverrideFlag", true);
            updatedRate.put("overrideReason", reason);
            updatedRate.put("overrideTimestamp", Instant.now().toString());
            return updatedRate;
        }).when(currencyConvertionController).overrideCurrencyRate(anyString(), anyDouble(), anyString());
    }

    @Test
    public void testAdminOverrideCurrencyRate() throws InterruptedException {
        // Step 1: Admin user logs in and submits override request via UI
        driver.get("http://localhost:" + port + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(adminUsername);
        passwordInput.sendKeys(adminPassword);
        loginButton.click();

        // Wait for redirect to dashboard
        Thread.sleep(1000);

        // Navigate to currency override page
        driver.get("http://localhost:" + port + "/currency/override");

        WebElement currencyInput = driver.findElement(By.id("currencyCode"));
        WebElement rateInput = driver.findElement(By.id("newRate"));
        WebElement reasonInput = driver.findElement(By.id("overrideReason"));
        WebElement submitButton = driver.findElement(By.id("submitOverride"));

        currencyInput.clear();
        currencyInput.sendKeys(currencyCode);
        rateInput.clear();
        rateInput.sendKeys(String.valueOf(overriddenRate));
        reasonInput.clear();
        reasonInput.sendKeys(overrideReason);

        submitButton.click();

        // Wait for processing
        Thread.sleep(1000);

        // Verify UI confirmation message
        WebElement confirmation = driver.findElement(By.id("confirmationMessage"));
        assertNotNull(confirmation);
        assertTrue(confirmation.getText().contains("Override successful"));

        // Step 2 & 3: Verify service method called with correct parameters and logging
        ArgumentCaptor<String> currencyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> rateCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);

        verify(currencyConvertionController, atLeastOnce()).overrideCurrencyRate(currencyCaptor.capture(), rateCaptor.capture(), reasonCaptor.capture());

        assertEquals(currencyCode, currencyCaptor.getValue());
        assertEquals(overriddenRate, rateCaptor.getValue());
        assertEquals(overrideReason, reasonCaptor.getValue());

        // Step 4: Verify alert triggered
        verify(fpmCommonController, atLeastOnce()).triggerAlert(contains(currencyCode));

        // Step 5: Query API for overridden currency rate
        Map<String, Object> apiResponse = currencyConvertionController.getCurrencyRate(currencyCode);

        assertNotNull(apiResponse);
        assertEquals(currencyCode, apiResponse.get("currencyCode"));
        assertEquals(overriddenRate, (Double) apiResponse.get("rate"), 0.0001);
        assertTrue((Boolean) apiResponse.getOrDefault("adminOverrideFlag", false));
        assertEquals(overrideReason, apiResponse.get("overrideReason"));
        assertNotNull(apiResponse.get("overrideTimestamp"));

        // Negative test: unauthorized user cannot override
        when(fpmUserProfileController.isUserAdmin("normalUser")).thenReturn(false);

        boolean unauthorizedOverrideAttempted = false;
        try {
            currencyConvertionController.overrideCurrencyRate(currencyCode, 1.20, "Unauthorized attempt");
        } catch (Exception e) {
            unauthorizedOverrideAttempted = true;
        }
        assertTrue(unauthorizedOverrideAttempted, "Unauthorized user should not be able to override currency rate");
    }
}
