/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6198
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 05:57:29
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.controller.CurrencyConvertionController;
import com.webapp.fpmapp.entity.CurrencyRateOverrides;
import com.webapp.fpmapp.entity.User;
import com.webapp.fpmapp.repository.CurrencyRateOverridesRepository;
import com.webapp.fpmapp.repository.CurrencyRatesRepository;
import com.webapp.fpmapp.repository.UserRepository;
import com.webapp.fpmapp.service.ApprovalAuditService;

/**
 * Integration Test for Admin Override of Currency Rate with audit
 * logging and alert email.
 * 
 * Preconditions:
 * - Admin user credentials valid and authorized for override
 * - SMTP email service configured and operational
 * - CurrencyRates DB supports override fields
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CurrencyRateOverrideIntegrationTest {

    private WebDriver driver;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private ApprovalAuditService approvalAuditService;

    @MockBean
    private CurrencyRatesRepository currencyRatesRepository;

    @MockBean
    private CurrencyRateOverridesRepository currencyRateOverridesRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private CurrencyConvertionController currencyConvertionController;

    private AutoCloseable mocks;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    @DisplayName("Admin override currency rate API, DB audit, and email verification")
    public void testCurrencyRateOverride_withAuditAndEmail() throws Exception {
        // Setup test data
        String currencyCode = "EUR";
        double overriddenRate = 1.2345;
        String overrideReason = "Manual correction after audit";
        String adminUserId = "admin123";

        // Mock admin user retrieval
        User adminUser = new User();
        adminUser.setUserId(adminUserId);
        adminUser.setUsername("adminUser");
        adminUser.setEmail("admin@example.com");
        adminUser.setRole("ADMIN");
        when(userRepository.findByUserId(adminUserId)).thenReturn(Optional.of(adminUser));

        // Mock existing currency rate exists
        when(currencyRatesRepository.existsByCurrencyCode(currencyCode)).thenReturn(true);

        // Mock saving override
        CurrencyRateOverrides savedOverride = new CurrencyRateOverrides();
        savedOverride.setOverrideId("override-001");
        savedOverride.setCurrencyCode(currencyCode);
        savedOverride.setOverriddenByUserId(adminUserId);
        savedOverride.setOverriddenRate(overriddenRate);
        savedOverride.setOverrideReason(overrideReason);
        savedOverride.setOverrideTimestamp(Instant.now());
        when(currencyRateOverridesRepository.save(any(CurrencyRateOverrides.class))).thenReturn(savedOverride);

        // Simulate API request body
        String overrideJson = objectMapper.writeValueAsString(new OverrideRequest(currencyCode, overriddenRate, overrideReason, adminUserId));

        // Perform POST request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/currency/rates/override")
                .contentType(MediaType.APPLICATION_JSON)
                .content(overrideJson))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        OverrideResponse overrideResponse = objectMapper.readValue(responseContent, OverrideResponse.class);

        // Assert API response
        assertThat(overrideResponse.isSuccess()).isTrue();
        assertThat(overrideResponse.getOverrideId()).isNotBlank();

        // Verify database override repository save called
        ArgumentCaptor<CurrencyRateOverrides> overrideCaptor = ArgumentCaptor.forClass(CurrencyRateOverrides.class);
        verify(currencyRateOverridesRepository, times(1)).save(overrideCaptor.capture());
        CurrencyRateOverrides capturedOverride = overrideCaptor.getValue();
        assertThat(capturedOverride.getCurrencyCode()).isEqualTo(currencyCode);
        assertThat(capturedOverride.getOverriddenRate()).isEqualTo(overriddenRate);
        assertThat(capturedOverride.getOverrideReason()).isEqualTo(overrideReason);
        assertThat(capturedOverride.getOverriddenByUserId()).isEqualTo(adminUserId);
        assertThat(capturedOverride.getOverrideTimestamp()).isNotNull();

        // Verify approval audit log service called
        verify(approvalAuditService, times(1)).logAction(any(String.class), any(String.class), any(String.class), any(Instant.class));

        // Verify email sent
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());
        MimeMessage sentMessage = messageCaptor.getValue();
        MimeMessageHelper helper = new MimeMessageHelper(sentMessage);

        assertThat(helper.getTo()).contains("admin@example.com");
        String mailContent = sentMessage.getContent().toString();
        assertThat(mailContent).contains(currencyCode);
        assertThat(mailContent).contains(String.valueOf(overriddenRate));
        assertThat(mailContent).contains(overrideReason);
        assertThat(mailContent).contains(adminUserId);

        // Selenium part: Simulate login and validate UI confirmation message
        driver.get("http://localhost:8080/login");
        driver.findElement(By.id("username")).sendKeys(adminUserId);
        driver.findElement(By.id("password")).sendKeys("dummyPassword");
        driver.findElement(By.id("loginButton")).click();

        // Navigate to override confirmation page
        driver.get("http://localhost:8080/currency/override-confirmation?overrideId=" + overrideResponse.getOverrideId());

        // Wait and assert confirmation message displayed
        Thread.sleep(1000);
        String confirmationMsg = driver.findElement(By.id("confirmationMessage")).getText();
        assertThat(confirmationMsg).contains("Override successful");
        assertThat(confirmationMsg).contains(currencyCode);
        assertThat(confirmationMsg).contains(String.valueOf(overriddenRate));
    }

    static final class OverrideRequest {
        public String currencyCode;
        public double overriddenRate;
        public String overrideReason;
        public String adminUserId;

        public OverrideRequest(String currencyCode, double overriddenRate, String overrideReason, String adminUserId) {
            this.currencyCode = currencyCode;
            this.overriddenRate = overriddenRate;
            this.overrideReason = overrideReason;
            this.adminUserId = adminUserId;
        }
    }

    static final class OverrideResponse {
        private boolean success;
        private String overrideId;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getOverrideId() {
            return overrideId;
        }

        public void setOverrideId(String overrideId) {
            this.overrideId = overrideId;
        }
    }
}
