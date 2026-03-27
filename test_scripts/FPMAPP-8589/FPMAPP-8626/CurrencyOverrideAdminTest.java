/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8626
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:56:16
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import reactor.core.publisher.Mono;

/**
 * Integration test for Admin override of currency rates with logging and alerting.
 * 
 * Preconditions:
 * - Admin user credentials are available.
 * - POST /fpm/currency/rates/override API is accessible.
 * - SMTP alerting system is configured.
 * 
 * This test uses Selenium WebDriver to simulate admin UI interaction and WebTestClient
 * to verify backend API and mocks SMTP email sending.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
public class CurrencyOverrideAdminTest {

    @Autowired
    private WebTestClient webTestClient;

    private WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private EmailServiceMock emailServiceMock;

    @BeforeEach
    public void setUp() {
        // Setup ChromeDriver in headless mode for CI
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Test successful override of currency rate by admin user.
     */
    @Test
    public void testAdminCurrencyOverrideSuccess() throws Exception {
        // Mock admin user login
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole("ADMIN");

        // Prepare override data
        String currencyCode = "USD";
        double newRate = 1.15;
        LocalDate effectiveDate = LocalDate.now().plusDays(1);
        String effectiveDateStr = effectiveDate.format(DateTimeFormatter.ISO_DATE);

        // Mock API response for override
        when(currencyConvertionController.overrideCurrencyRate(any(), anyDouble(), any()))
            .thenReturn(Mono.just(new OverrideResponse(true, "AuditLogRef12345")));

        // Mock database update verification
        when(currencyConvertionController.checkOverrideFlag(currencyCode, effectiveDate))
            .thenReturn(Mono.just(true));

        when(currencyConvertionController.getOverrideMetadata(currencyCode, effectiveDate))
            .thenReturn(Mono.just(new OverrideMetadata(adminUser.getId(), System.currentTimeMillis())));

        // Mock SMTP alert email sending
        doNothing().when(emailServiceMock).sendOverrideAlertEmail(anyString(), anyString(), anyDouble(), any(LocalDate.class));

        // Simulate admin login and navigate to override page
        driver.get("http://localhost:8080/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("adminPass123");
        loginButton.click();

        // Wait for redirect to dashboard
        Thread.sleep(2000);

        // Navigate to currency override page
        driver.get("http://localhost:8080/admin/currency-override");

        // Fill override form
        WebElement currencyCodeInput = driver.findElement(By.id("currencyCode"));
        WebElement newRateInput = driver.findElement(By.id("newRate"));
        WebElement effectiveDateInput = driver.findElement(By.id("effectiveDate"));
        WebElement submitButton = driver.findElement(By.id("submitOverride"));

        currencyCodeInput.sendKeys(currencyCode);
        newRateInput.sendKeys(String.valueOf(newRate));
        effectiveDateInput.sendKeys(effectiveDateStr);
        submitButton.click();

        // Wait for response
        Thread.sleep(2000);

        // Verify API call was made
        verify(currencyConvertionController, times(1)).overrideCurrencyRate(currencyCode, newRate, effectiveDate);

        // Verify override flag and metadata
        Boolean overrideFlag = currencyConvertionController.checkOverrideFlag(currencyCode, effectiveDate).block();
        assertThat(overrideFlag).isTrue();

        OverrideMetadata metadata = currencyConvertionController.getOverrideMetadata(currencyCode, effectiveDate).block();
        assertThat(metadata).isNotNull();
        assertThat(metadata.getOverrideUserId()).isEqualTo(adminUser.getId());
        assertThat(metadata.getOverrideTimestamp()).isGreaterThan(0);

        // Verify alert email sent
        verify(emailServiceMock, times(1)).sendOverrideAlertEmail(eq(currencyCode), eq("admin"), eq(newRate), eq(effectiveDate));

        // Verify UI shows success message
        WebElement successMsg = driver.findElement(By.id("successMessage"));
        assertThat(successMsg.getText()).contains("Override successful");
    }

    /**
     * Test override with invalid data returns validation errors and no override applied.
     */
    @Test
    public void testAdminCurrencyOverrideInvalidData() throws Exception {
        // Invalid data: negative rate and missing currency code
        String invalidCurrencyCode = "";
        double invalidRate = -5.0;
        LocalDate effectiveDate = LocalDate.now().plusDays(1);
        String effectiveDateStr = effectiveDate.format(DateTimeFormatter.ISO_DATE);

        // Mock API to return error response
        when(currencyConvertionController.overrideCurrencyRate(any(), anyDouble(), any()))
            .thenReturn(Mono.error(new IllegalArgumentException("Validation failed: currency_code required, rate must be positive")));

        // Simulate admin login and navigate to override page
        driver.get("http://localhost:8080/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("adminPass123");
        loginButton.click();

        Thread.sleep(2000);

        driver.get("http://localhost:8080/admin/currency-override");

        WebElement currencyCodeInput = driver.findElement(By.id("currencyCode"));
        WebElement newRateInput = driver.findElement(By.id("newRate"));
        WebElement effectiveDateInput = driver.findElement(By.id("effectiveDate"));
        WebElement submitButton = driver.findElement(By.id("submitOverride"));

        currencyCodeInput.sendKeys(invalidCurrencyCode);
        newRateInput.sendKeys(String.valueOf(invalidRate));
        effectiveDateInput.sendKeys(effectiveDateStr);
        submitButton.click();

        Thread.sleep(2000);

        // Verify API call was made
        verify(currencyConvertionController, times(1)).overrideCurrencyRate(invalidCurrencyCode, invalidRate, effectiveDate);

        // Verify no override flag set
        when(currencyConvertionController.checkOverrideFlag(invalidCurrencyCode, effectiveDate))
            .thenReturn(Mono.just(false));
        Boolean overrideFlag = currencyConvertionController.checkOverrideFlag(invalidCurrencyCode, effectiveDate).block();
        assertThat(overrideFlag).isFalse();

        // Verify no email sent
        verify(emailServiceMock, never()).sendOverrideAlertEmail(anyString(), anyString(), anyDouble(), any(LocalDate.class));

        // Verify UI shows error message
        WebElement errorMsg = driver.findElement(By.id("errorMessage"));
        assertThat(errorMsg.getText()).contains("Validation failed");
    }

    // Mock classes for response and metadata
    public static class OverrideResponse {
        private boolean success;
        private String auditLogRef;

        public OverrideResponse(boolean success, String auditLogRef) {
            this.success = success;
            this.auditLogRef = auditLogRef;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getAuditLogRef() {
            return auditLogRef;
        }
    }

    public static class OverrideMetadata {
        private Long overrideUserId;
        private long overrideTimestamp;

        public OverrideMetadata(Long overrideUserId, long overrideTimestamp) {
            this.overrideUserId = overrideUserId;
            this.overrideTimestamp = overrideTimestamp;
        }

        public Long getOverrideUserId() {
            return overrideUserId;
        }

        public long getOverrideTimestamp() {
            return overrideTimestamp;
        }
    }

    /**
     * Mock email service to verify alert email sending.
     */
    public interface EmailServiceMock {
        void sendOverrideAlertEmail(String currencyCode, String username, double newRate, LocalDate effectiveDate);
    }
}
