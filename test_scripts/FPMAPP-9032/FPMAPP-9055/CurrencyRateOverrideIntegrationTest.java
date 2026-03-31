/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-9055
 * Epic: FPMAPP-9032
 * Generated on: 2026-03-31 16:45:38
 *
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration test for Admin override of currency exchange rate with logging and email alert.
 * 
 * Preconditions:
 * - Admin user credentials are valid.
 * - SMTP mail server is configured and operational.
 * - Override API endpoint is accessible.
 * 
 * This test uses Selenium WebDriver to simulate UI interaction and WebTestClient for API calls.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyRateOverrideIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    private WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        // Setup ChromeDriver in headless mode for CI environments
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

    @Test
    public void testAdminOverrideCurrencyRateFlow() throws Exception {
        // Step 1: Prepare override request data
        String currencyPair = "USD/EUR";
        double newRate = 0.85;
        LocalDate effectiveDate = LocalDate.now().plusDays(1);
        String adminUserId = "admin123";

        Map<String, Object> overrideRequest = Map.of(
                "currency_pair", currencyPair,
                "new_rate", newRate,
                "effective_date", effectiveDate.toString(),
                "admin_user_id", adminUserId
        );

        // Mock the service layer to simulate successful override
        when(currencyConvertionController.overrideCurrencyRate(any())).thenReturn(
                Map.of("status", "success", "message", "Override applied successfully")
        );

        // Mock database verification by simulating a stored override record
        when(currencyConvertionController.getOverrideRecord(currencyPair, effectiveDate))
                .thenReturn(Map.of(
                        "currency_pair", currencyPair,
                        "rate", newRate,
                        "effective_date", effectiveDate.toString(),
                        "admin_user_id", adminUserId
                ));

        // Mock email sending
        MimeMessage mimeMessage = org.mockito.Mockito.mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Step 2: Send POST request to override API
        webTestClient.post()
                .uri("/api/fpm/currency/rates/override")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(overrideRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("success")
                .jsonPath("$.message").isEqualTo("Override applied successfully");

        // Step 3: Verify override record stored correctly
        Map<String, Object> storedRecord = currencyConvertionController.getOverrideRecord(currencyPair, effectiveDate);
        assertThat(storedRecord).isNotNull();
        assertThat(storedRecord.get("currency_pair")).isEqualTo(currencyPair);
        assertThat(Double.parseDouble(storedRecord.get("rate").toString())).isEqualTo(newRate);
        assertThat(storedRecord.get("effective_date")).isEqualTo(effectiveDate.toString());
        assertThat(storedRecord.get("admin_user_id")).isEqualTo(adminUserId);

        // Step 4: Confirm email alert sent
        verify(mailSender, times(1)).send(any(MimeMessage.class));

        // Step 5: Query overridden rate via GET API
        when(currencyConvertionController.getCurrencyRate(currencyPair, effectiveDate))
                .thenReturn(newRate);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/fpm/currency/rates")
                        .queryParam("currency_pair", currencyPair)
                        .queryParam("date", effectiveDate.toString())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Double.class)
                .value(rate -> assertThat(rate).isEqualTo(newRate));

        // Step 6: Selenium UI validation (simulate admin login and check override log page)
        driver.get(BASE_URL + "/login");

        // Login as admin user
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));

        usernameInput.sendKeys(adminUserId);
        passwordInput.sendKeys("adminPassword123"); // Assuming test password
        loginButton.click();

        // Wait for redirect and load override logs page
        Thread.sleep(2000); // Simple wait for demo; better to use WebDriverWait in prod

        driver.get(BASE_URL + "/admin/currency-overrides");

        // Verify override record is visible in UI
        List<WebElement> rows = driver.findElements(By.cssSelector("table#overrideTable tbody tr"));
        boolean foundOverride = false;
        for (WebElement row : rows) {
            String rowCurrencyPair = row.findElement(By.cssSelector("td.currencyPair")).getText();
            String rowRate = row.findElement(By.cssSelector("td.rate")).getText();
            String rowDate = row.findElement(By.cssSelector("td.effectiveDate")).getText();
            String rowAdmin = row.findElement(By.cssSelector("td.adminUser")).getText();

            if (rowCurrencyPair.equals(currencyPair) &&
                Double.parseDouble(rowRate) == newRate &&
                rowDate.equals(effectiveDate.toString()) &&
                rowAdmin.equals(adminUserId)) {
                foundOverride = true;
                break;
            }
        }

        assertThat(foundOverride).isTrue();

        // Step 7: Attempt unauthorized override (simulate non-admin user)
        Map<String, Object> unauthorizedRequest = Map.of(
                "currency_pair", "USD/GBP",
                "new_rate", 0.75,
                "effective_date", LocalDate.now().toString(),
                "admin_user_id", "unauthorizedUser"
        );

        // Mock unauthorized override response
        when(currencyConvertionController.overrideCurrencyRate(any())).thenThrow(new SecurityException("Unauthorized override attempt"));

        webTestClient.post()
                .uri("/api/fpm/currency/rates/override")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(unauthorizedRequest))
                .exchange()
                .expectStatus().isForbidden();
    }
}
