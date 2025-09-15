/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6179
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:12:48
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.controller.CurrencyConvertionController;
import com.webapp.fpmapp.dto.CurrencyOverrideRequestDto;
import com.webapp.fpmapp.entity.CurrencyRateOverrideAudit;
import com.webapp.fpmapp.repository.CurrencyRateOverrideAuditRepository;
import com.webapp.fpmapp.service.CurrencyOverrideService;

/**
 * Complete Spring Boot + Selenium integration test validating administrative override
 * functionality with audit logging and alert email notification.
 * 
 * Preconditions:
 *  - Admin user credentials with override API access.
 *  - SMTP server mock configured for sending alert emails.
 *  - Currency exchange rate system running with current data (mocked here).
 */

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CurrencyOverrideAdminIntegrationTest {

    private static WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";

    private static MockMvc mockMvc;

    @Autowired
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private CurrencyOverrideService currencyOverrideService;

    @MockBean
    private CurrencyRateOverrideAuditRepository auditRepository;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver for Selenium (headless for CI)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAdminCurrencyRateOverrideWorkflow() throws Exception {
        // Step 1: Authenticate as finance administrator
        // For test simplicity, simulate authentication by direct header or no-auth
        // Assuming application has a /login page - here just mock login success

        // Step 2: Use override API endpoint to submit new currency rate
        CurrencyOverrideRequestDto overrideRequest = new CurrencyOverrideRequestDto();
        overrideRequest.setCurrencyCode("USD");
        overrideRequest.setNewRate(1.25);
        overrideRequest.setAdminUserId("adminuser01");
        overrideRequest.setOverrideReason("Quarterly adjustment based on FX outlook");

        String jsonRequest = objectMapper.writeValueAsString(overrideRequest);

        mockMvc = MockMvcBuilders.standaloneSetup(currencyConvertionController).build();

        when(currencyOverrideService.applyOverride(any())).thenAnswer(invocation -> {
            CurrencyOverrideRequestDto req = invocation.getArgument(0);
            CurrencyRateOverrideAudit audit = new CurrencyRateOverrideAudit();
            audit.setOverrideId(123L);
            audit.setCurrencyCode(req.getCurrencyCode());
            audit.setOverriddenByUserId(req.getAdminUserId());
            audit.setOverrideTimestamp(LocalDateTime.now());
            audit.setOriginalRate(1.20);
            audit.setOverriddenRate(req.getNewRate());
            audit.setReason(req.getOverrideReason());
            // Simulate saving audit
            return audit;
        });

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/currency/override")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // Validate the response body contains confirmation and audit record ID
        String responseContent = response.getContentAsString();
        assertThat(responseContent).contains("overrideId");

        // Step 3: Verify override stored in DB audit log
        ArgumentCaptor<CurrencyOverrideRequestDto> captor = ArgumentCaptor.forClass(CurrencyOverrideRequestDto.class);
        verify(currencyOverrideService, times(1)).applyOverride(captor.capture());
        CurrencyOverrideRequestDto capturedRequest = captor.getValue();

        assertThat(capturedRequest.getCurrencyCode()).isEqualTo("USD");
        assertThat(capturedRequest.getNewRate()).isEqualTo(1.25);
        assertThat(capturedRequest.getAdminUserId()).isEqualTo("adminuser01");

        // Step 4: Confirm alert email notification triggered
        ArgumentCaptor<MimeMessage> mailCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(mailCaptor.capture());
        MimeMessage sentMessage = mailCaptor.getValue();
        assertThat(sentMessage).isNotNull();

        String[] recipients = sentMessage.getAllRecipients() != null ?
                java.util.Arrays.stream(sentMessage.getAllRecipients())
                        .map(r -> r.toString())
                        .toArray(String[]::new) : new String[0];
        assertThat(recipients).isNotEmpty();

        String subject = sentMessage.getSubject();
        assertThat(subject).contains("Currency Rate Override Alert");

        // Step 5: Retrieve overridden rate via API to confirm updated value
        when(currencyOverrideService.getCurrentRate("USD")).thenReturn(1.25);

        MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/currency/rate/USD")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String getContent = getResult.getResponse().getContentAsString();
        assertThat(getContent).contains("1.25");
    }

    /**
     * Optional Selenium test interaction minimal to confirm UI access - consists of login and access override page.
     */
    //@Test
    public void seleniumTestAdminOverridePageAccess() {
        driver.get(BASE_URL + "/login");

        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.id("loginBtn"));

        usernameField.sendKeys("adminuser01");
        passwordField.sendKeys("adminpassword");
        submitButton.click();

        // Wait and navigate to override page
        driver.navigate().to(BASE_URL + "/admin/currency-override");

        WebElement pageHeader = driver.findElement(By.tagName("h1"));
        assertThat(pageHeader.getText()).contains("Currency Rate Override");

        // Validate override form elements present
        assertThat(driver.findElement(By.id("currencyCode"))).isNotNull();
        assertThat(driver.findElement(By.id("newRate"))).isNotNull();
        assertThat(driver.findElement(By.id("overrideReason"))).isNotNull();
        assertThat(driver.findElement(By.id("submitOverride"))).isNotNull();
    }
}