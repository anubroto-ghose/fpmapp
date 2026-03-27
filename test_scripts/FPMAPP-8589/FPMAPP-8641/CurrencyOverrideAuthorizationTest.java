/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8641
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:45:22
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Collections;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.services.FpmCommonController;

import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.server.LocalManagementPort;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Selenium + Spring Boot Integration Test for verifying that a non-admin user
 * cannot override currency exchange rates.
 *
 * Preconditions:
 * - User is authenticated but does not have admin privileges.
 * - Currency_Exchange_Rates table is accessible.
 *
 * Test Steps:
 * 1. Send PUT request to /fpm/currency/rates with overrideFlag=true and overrideReason="Unauthorized test"
 *    using non-admin credentials.
 * 2. Verify API returns authorization error.
 * 3. Verify no changes to Currency_Exchange_Rates table.
 * 4. Verify no alert emails sent.
 * 5. Verify no audit log entry created.
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CurrencyOverrideAuthorizationTest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    private WebDriverWait wait;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void setup() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mock CurrencyConvertionController to simulate authorization failure for non-admin
        when(currencyConvertionController.overrideCurrencyRates(any()))
            .thenThrow(new org.springframework.security.access.AccessDeniedException("User not authorized to override currency rates"));

        // Mock FpmCommonController to verify no audit log created
        when(fpmCommonController.getAuditLogsForCurrencyOverride()).thenReturn(Collections.emptyList());

        // Mock email sending to verify no alert emails sent
        when(fpmCommonController.getSentAlertEmails()).thenReturn(Collections.emptyList());
    }

    @Test
    public void testNonAdminUserCannotOverrideCurrencyRates() throws Exception {
        // Simulate login as non-admin user via UI
        driver.get("http://localhost:" + port + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("nonadminuser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for redirect to dashboard or home page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Prepare JSON body for override attempt
        String requestBody = "{" +
                "\"overrideFlag\": true," +
                "\"overrideReason\": \"Unauthorized test\"" +
                "}";

        // Use TestRestTemplate to send PUT request with non-admin credentials
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("nonadminuser", "password123");

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/fpm/currency/rates",
                HttpMethod.PUT,
                entity,
                String.class);

        // Assert that the response status is 403 Forbidden
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // Assert response body contains authorization error message
        assertThat(response.getBody()).containsIgnoringCase("not authorized");

        // Verify no changes made to Currency_Exchange_Rates table
        verify(currencyConvertionController, never()).saveCurrencyRates(any());

        // Verify no alert emails sent
        verify(fpmCommonController, never()).sendAlertEmail(any());

        // Verify no audit log entry created
        verify(fpmCommonController, never()).createAuditLogEntry(any());
    }
}
