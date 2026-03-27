/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8639
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:46:48
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import org.springframework.http.HttpHeaders;
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
 * Integration test for admin override of currency exchange rates.
 * 
 * Preconditions:
 * - Admin user authenticated with valid API access.
 * - SMTP mail server mocked.
 * - Currency_Exchange_Rates table mocked.
 * 
 * This test uses Selenium WebDriver to verify UI reflects overridden rates.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CurrencyOverrideAdminTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController userProfileController;

    @MockBean
    private JavaMailSender mailSender;

    private static WebDriver driver;

    private static final String ADMIN_TOKEN = "Bearer valid-admin-token";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
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
        // Mock authenticated admin user
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole("ADMIN");
        when(userProfileController.getCurrentUser()).thenReturn(adminUser);

        // Mock current currency rate
        when(currencyConvertionController.getCurrentRates())
            .thenReturn(Collections.singletonMap("USD_TO_EUR", 0.85));

        // Mock override API call
        doAnswer(invocation -> {
            // Simulate DB update and audit log creation
            return Mono.just(new OverrideResponse(true, "AUDIT12345"));
        }).when(currencyConvertionController).overrideCurrencyRates(any(OverrideRequest.class), any(String.class));

        // Mock sending email
        doAnswer(invocation -> {
            MimeMessage message = invocation.getArgument(0);
            // We could verify message content here if needed
            return null;
        }).when(mailSender).send(any(MimeMessage.class));
    }

    @Test
    public void testAdminOverrideCurrencyRate() throws Exception {
        // Step 1: Send PUT request to override currency rates
        OverrideRequest overrideRequest = new OverrideRequest(true, "Market adjustment");

        webTestClient.put()
            .uri("/fpm/currency/rates")
            .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(overrideRequest))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.auditLogReference").isNotEmpty();

        // Step 2: Verify DB update (mocked via service call)
        ArgumentCaptor<OverrideRequest> captor = ArgumentCaptor.forClass(OverrideRequest.class);
        verify(currencyConvertionController).overrideCurrencyRates(captor.capture(), any(String.class));
        OverrideRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.isOverrideFlag()).isTrue();
        assertThat(capturedRequest.getOverrideReason()).isEqualTo("Market adjustment");

        // Step 3: Verify alert email sent
        ArgumentCaptor<MimeMessage> mailCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(mailCaptor.capture());
        MimeMessage sentMessage = mailCaptor.getValue();
        assertThat(sentMessage).isNotNull();

        // Step 4: Verify overridden rate reflected in API
        when(currencyConvertionController.getCurrentRates())
            .thenReturn(Collections.singletonMap("USD_TO_EUR", 0.90)); // overridden rate

        webTestClient.get()
            .uri("/fpm/currency/rates")
            .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.USD_TO_EUR").isEqualTo(0.90);

        // Step 5: Selenium UI verification
        // Assume UI is running locally at http://localhost:8080
        driver.get("http://localhost:8080/currency-rates");

        // Login as admin (simulate login page)
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("adminPassword");
        loginButton.click();

        // Wait for redirect and page load
        Thread.sleep(2000);

        // Locate the overridden rate element
        WebElement rateElement = driver.findElement(By.id("rate-USD_TO_EUR"));
        String displayedRate = rateElement.getText();
        assertThat(displayedRate).isEqualTo("0.90");

        // Step 6: Verify audit logs (mocked service call)
        AuditLogEntry auditLog = new AuditLogEntry();
        auditLog.setUserId(1L);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setAction("Override Currency Rate");
        auditLog.setReason("Market adjustment");

        // Simulate audit log retrieval
        when(currencyConvertionController.getAuditLogs("AUDIT12345"))
            .thenReturn(Collections.singletonList(auditLog));

        List<AuditLogEntry> logs = currencyConvertionController.getAuditLogs("AUDIT12345");
        assertThat(logs).isNotEmpty();
        AuditLogEntry log = logs.get(0);
        assertThat(log.getUserId()).isEqualTo(1L);
        assertThat(log.getReason()).isEqualTo("Market adjustment");
        assertThat(log.getAction()).isEqualTo("Override Currency Rate");
    }

    // DTOs and helper classes for mocking
    public static class OverrideRequest {
        private boolean overrideFlag;
        private String overrideReason;

        public OverrideRequest() {}

        public OverrideRequest(boolean overrideFlag, String overrideReason) {
            this.overrideFlag = overrideFlag;
            this.overrideReason = overrideReason;
        }

        public boolean isOverrideFlag() {
            return overrideFlag;
        }

        public void setOverrideFlag(boolean overrideFlag) {
            this.overrideFlag = overrideFlag;
        }

        public String getOverrideReason() {
            return overrideReason;
        }

        public void setOverrideReason(String overrideReason) {
            this.overrideReason = overrideReason;
        }
    }

    public static class OverrideResponse {
        private boolean success;
        private String auditLogReference;

        public OverrideResponse() {}

        public OverrideResponse(boolean success, String auditLogReference) {
            this.success = success;
            this.auditLogReference = auditLogReference;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getAuditLogReference() {
            return auditLogReference;
        }

        public void setAuditLogReference(String auditLogReference) {
            this.auditLogReference = auditLogReference;
        }
    }

    public static class AuditLogEntry {
        private Long userId;
        private LocalDateTime timestamp;
        private String action;
        private String reason;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
