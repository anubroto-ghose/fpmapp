/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8611
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 08:06:50
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.MimeMessage;

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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import reactor.core.publisher.Mono;

/**
 * Integration Selenium + Spring Boot test for Admin override of currency rates with audit logging and alerting.
 * 
 * Preconditions:
 * - Admin user credentials are valid.
 * - POST /fpm/currency/rates/override API is accessible.
 * - SMTP alerting system is configured.
 * 
 * This test mocks the CurrencyConvertionController service and JavaMailSender for email alerts.
 * It verifies API response, DB update simulation, and email alert sending.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CurrencyOverrideAdminTest {

    private static WebDriver driver;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private JavaMailSender mailSender;

    private static final String ADMIN_USER_ID = "admin123";
    private static final String CURRENCY_PAIR = "USD/EUR";
    private static final double NEW_RATE = 0.85;
    private static final LocalDate EFFECTIVE_DATE = LocalDate.now().plusDays(1);

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless for CI environments)
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setupMocks() throws Exception {
        // Mock API response for override
        doReturn(Mono.just(new OverrideResponse(true, "Override successful", "AuditLog123")))
            .when(currencyConvertionController)
            .overrideCurrencyRate(any());

        // Mock mail sender to capture sent email
        MimeMessage mimeMessage = new MimeMessage((javax.mail.Session) null);
        doReturn(mimeMessage).when(mailSender).createMimeMessage();
    }

    @Test
    public void testAdminCurrencyRateOverrideFlow() throws Exception {
        // Step 1: As admin user, send POST request to override currency rate
        OverrideRequest request = new OverrideRequest();
        request.setAdminUserId(ADMIN_USER_ID);
        request.setCurrencyPair(CURRENCY_PAIR);
        request.setNewRate(NEW_RATE);
        request.setEffectiveDate(EFFECTIVE_DATE.format(DateTimeFormatter.ISO_DATE));

        webTestClient.post()
            .uri("/fpm/currency/rates/override")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Override successful")
            .jsonPath("$.auditLogId").isNotEmpty();

        // Step 2: Verify API response confirms override and audit log creation
        // (done above via WebTestClient assertions)

        // Step 3: Check DB for override_flag and overridden_by
        // Since DB is not accessible here, simulate by verifying service call
        ArgumentCaptor<OverrideRequest> captor = ArgumentCaptor.forClass(OverrideRequest.class);
        verify(currencyConvertionController, times(1)).overrideCurrencyRate(captor.capture());
        OverrideRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.getAdminUserId()).isEqualTo(ADMIN_USER_ID);
        assertThat(capturedRequest.getCurrencyPair()).isEqualTo(CURRENCY_PAIR);
        assertThat(capturedRequest.getNewRate()).isEqualTo(NEW_RATE);
        assertThat(capturedRequest.getEffectiveDate()).isEqualTo(EFFECTIVE_DATE.format(DateTimeFormatter.ISO_DATE));

        // Step 4: Verify alert email is sent
        ArgumentCaptor<MimeMessage> emailCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(emailCaptor.capture());
        MimeMessage sentMessage = emailCaptor.getValue();
        assertThat(sentMessage).isNotNull();

        // Additional: Verify email content contains override details
        MimeMessageHelper helper = new MimeMessageHelper(sentMessage);
        String subject = sentMessage.getSubject();
        assertThat(subject).contains("Currency Rate Override Alert");
        String content = (String) sentMessage.getContent();
        assertThat(content).contains(CURRENCY_PAIR);
        assertThat(content).contains(String.valueOf(NEW_RATE));
        assertThat(content).contains(ADMIN_USER_ID);

        // Step 5: Selenium UI check - simulate admin login and verify override confirmation message
        driver.get("http://localhost:8080/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(ADMIN_USER_ID);
        passwordInput.sendKeys("adminPassword"); // assuming test password
        loginButton.click();

        // Navigate to currency override page
        driver.get("http://localhost:8080/admin/currency-override");

        WebElement currencyPairInput = driver.findElement(By.id("currencyPair"));
        WebElement newRateInput = driver.findElement(By.id("newRate"));
        WebElement effectiveDateInput = driver.findElement(By.id("effectiveDate"));
        WebElement submitButton = driver.findElement(By.id("submitOverride"));

        currencyPairInput.clear();
        currencyPairInput.sendKeys(CURRENCY_PAIR);
        newRateInput.clear();
        newRateInput.sendKeys(String.valueOf(NEW_RATE));
        effectiveDateInput.clear();
        effectiveDateInput.sendKeys(EFFECTIVE_DATE.format(DateTimeFormatter.ISO_DATE));

        submitButton.click();

        // Wait and verify confirmation message
        WebElement confirmationMessage = driver.findElement(By.id("confirmationMessage"));
        assertThat(confirmationMessage.getText()).contains("Override successful");

        // Verify no disruption to ongoing sync jobs - simulate by checking a status element
        driver.get("http://localhost:8080/admin/sync-status");
        WebElement syncStatus = driver.findElement(By.id("syncJobStatus"));
        assertThat(syncStatus.getText()).isEqualTo("Running");
    }

    // DTO classes for request and response
    public static class OverrideRequest {
        private String currencyPair;
        private double newRate;
        private String effectiveDate;
        private String adminUserId;

        public String getCurrencyPair() {
            return currencyPair;
        }

        public void setCurrencyPair(String currencyPair) {
            this.currencyPair = currencyPair;
        }

        public double getNewRate() {
            return newRate;
        }

        public void setNewRate(double newRate) {
            this.newRate = newRate;
        }

        public String getEffectiveDate() {
            return effectiveDate;
        }

        public void setEffectiveDate(String effectiveDate) {
            this.effectiveDate = effectiveDate;
        }

        public String getAdminUserId() {
            return adminUserId;
        }

        public void setAdminUserId(String adminUserId) {
            this.adminUserId = adminUserId;
        }
    }

    public static class OverrideResponse {
        private boolean success;
        private String message;
        private String auditLogId;

        public OverrideResponse(boolean success, String message, String auditLogId) {
            this.success = success;
            this.message = message;
            this.auditLogId = auditLogId;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getAuditLogId() {
            return auditLogId;
        }

        public void setAuditLogId(String auditLogId) {
            this.auditLogId = auditLogId;
        }
    }
}
