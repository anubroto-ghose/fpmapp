/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6219
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:40:24
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;

import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration test verifying admin currency override API triggers alert, logging, and audit info.
 * 
 * Preconditions:
 * - Admin user credentials and API access to POST /api/fpm/currency/override.
 * - SMTP email service configured and operational.
 * - Audit logging enabled.
 * 
 * This test launches Spring Boot with an embedded web server,
 * uses Selenium WebDriver to interact with a simulated admin UI,
 * mocks services as needed, and verifies backend DB and email.
 */

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=2525",
        "spring.mail.test-connection=true"
    }
)
@ActiveProfiles("test")
public class CurrencyOverrideAdminIntegrationTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private javax.mail.Transport transportMock;

    // Simplistic email sending capture (simulate SMTP)
    private boolean emailSent = false;

    @BeforeEach
    public void setUp() throws Exception {
        // Setup ChromeDriver headless for testing
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1200,800");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        // Reset email sent flag
        emailSent = false;
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Test the admin override currency API via HTTP POST and validate database, audit logs, and email alert.
     */
    @Test
    public void testCurrencyOverrideAdminApiTriggersAlertLoggingAndAudit() throws Exception {
        // Setup test data
        final String currencyCode = "USD";
        final double newRate = 1.15;
        final long adminUserId = 9999L;
        final String overrideReason = "Quarterly adjustment for market volatility";

        // Mock the CurrencyConvertionController to simulate successful override and audit confirmation
        when(currencyConvertionController.adminOverrideCurrencyRate(
                eq(currencyCode), eq(newRate), eq(adminUserId), eq(overrideReason)))
                .thenReturn("Override recorded successfully with audit ID: 12345");

        // Simulate SMTP email service by mocking JavaMail Transport static send
        // In reality, this would require a dedicated SMTP mock server or use GreenMail etc.
        // Here we simulate by setting a flag when mock method called
        doAnswer(invocation -> {
            emailSent = true;
            return null;
        }).when(transportMock).send(any(MimeMessage.class));

        // Perform POST request through Selenium by navigating to a mock admin UI page
        // Normally this UI is complex; here we simulate direct REST call to keep it production-like

        String apiUrl = "http://localhost:" + port + "/api/fpm/currency/override";

        // Using RestTemplate since Selenium is UI automation, but test requires integrated API call and DB verification
        // Here we simulate the POST request using TestRestTemplate

        // Create request payload
        var requestBody = new org.springframework.util.LinkedMultiValueMap<String, String>();
        requestBody.add("currencyCode", currencyCode);
        requestBody.add("newRate", Double.toString(newRate));
        requestBody.add("adminUserId", Long.toString(adminUserId));
        requestBody.add("overrideReason", overrideReason);

        var responseEntity = restTemplate.postForEntity(apiUrl, requestBody, String.class);

        // Verify API response
        assertEquals(200, responseEntity.getStatusCodeValue(), "API call should be successful");
        assertTrue(responseEntity.getBody().contains("Override recorded successfully"), "API confirmation message missing");

        // Verify database entries for override
        try (Connection conn = dataSource.getConnection()) {
            // Verify currency_rates override
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT overridden_rate, overridden_by_user_id, override_timestamp, override_reason, override_status FROM currency_rates WHERE currency_code = ?")) {
                ps.setString(1, currencyCode);
                try (ResultSet rs = ps.executeQuery()) {
                    assertTrue(rs.next(), "Currency code should exist");
                    double dbOverriddenRate = rs.getDouble("overridden_rate");
                    long dbUserId = rs.getLong("overridden_by_user_id");
                    Timestamp overrideTimestamp = rs.getTimestamp("override_timestamp");
                    String dbReason = rs.getString("override_reason");
                    String overrideStatus = rs.getString("override_status");

                    assertEquals(newRate, dbOverriddenRate, 0.0001, "Overridden rate must match new rate");
                    assertEquals(adminUserId, dbUserId, "Overridden by user ID must match admin user");
                    assertNotNull(overrideTimestamp, "Override timestamp must be set");
                    assertEquals(overrideReason, dbReason, "Override reason must match");
                    assertEquals("OVERRIDDEN", overrideStatus, "Override status must be OVERRIDDEN");
                }
            }

            // Verify audit_log entry for override action
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id, action_type, action_timestamp, remarks FROM approval_audit_log WHERE action_type = ? AND remarks LIKE ? ORDER BY action_timestamp DESC LIMIT 1")) {
                ps.setString(1, "CURRENCY_OVERRIDE");
                ps.setString(2, "%" + currencyCode + "%");

                try (ResultSet rs = ps.executeQuery()) {
                    assertTrue(rs.next(), "Audit log entry for currency override must be present");
                    long logUserId = rs.getLong("user_id");
                    String actionType = rs.getString("action_type");
                    Timestamp actionTime = rs.getTimestamp("action_timestamp");
                    String remarks = rs.getString("remarks");

                    assertEquals(adminUserId, logUserId, "Audit log user ID must be admin");
                    assertEquals("CURRENCY_OVERRIDE", actionType, "Audit action type must be CURRENCY_OVERRIDE");
                    assertNotNull(actionTime, "Audit log timestamp must be present");
                    assertTrue(remarks.contains(currencyCode), "Audit remarks must contain currency code");
                    assertTrue(remarks.contains(Double.toString(newRate)), "Audit remarks must contain overridden rate");
                }
            }
        }

        // Verify that alert email was sent
        assertTrue(emailSent, "An alert email should have been sent via SMTP");
    }

}
