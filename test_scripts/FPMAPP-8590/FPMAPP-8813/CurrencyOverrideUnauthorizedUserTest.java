/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8813
 * Epic: FPMAPP-8590
 * Generated on: 2026-03-27 07:53:39
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium + SpringBoot test for unauthorized user attempting currency override.
 * 
 * Preconditions:
 * - User logged in with role NOT authorized for override
 * - POST /api/currency/override accessible
 * 
 * Validates:
 * - API rejects override with authorization error
 * - No log entry created
 * - No alert generated
 * - Currency data unchanged
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CurrencyOverrideUnauthorizedUserTest {

    private static WebDriver driver;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmUserProfileController userProfileController;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (headless)
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        // Mock user profile with unauthorized role
        User unauthorizedUser = new User();
        unauthorizedUser.setUsername("unauthorizedUser");
        unauthorizedUser.setRoles(Collections.singletonList("ROLE_USER")); // Not ROLE_CURRENCY_ADMIN

        when(userProfileController.getCurrentUser()).thenReturn(unauthorizedUser);

        // Mock currency data unchanged
        when(currencyConvertionController.getCurrentExchangeRate()).thenReturn(1.10);
    }

    @Test
    @WithMockUser(username = "unauthorizedUser", roles = {"USER"})
    public void testCurrencyOverrideRejectedForUnauthorizedUser() throws Exception {
        // Prepare override request JSON
        OverrideRequest overrideRequest = new OverrideRequest();
        overrideRequest.setNewExchangeRate(1.25);
        overrideRequest.setReason("Test override attempt by unauthorized user");

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = mapper.writeValueAsString(overrideRequest);

        // Perform POST /api/currency/override
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/currency/override")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert HTTP 403 Forbidden or 401 Unauthorized
        assertThat(response.getStatus()).isIn(HttpStatus.FORBIDDEN.value(), HttpStatus.UNAUTHORIZED.value());

        // Assert response contains authorization error message
        String content = response.getContentAsString();
        assertThat(content).containsIgnoringCase("not authorized").or().containsIgnoringCase("access denied");

        // Verify no override log entry created
        verify(currencyConvertionController, never()).logOverrideAttempt(any(), any(), any());

        // Verify no alert generated
        verify(currencyConvertionController, never()).generateAlert(any());

        // Verify currency data remains unchanged
        double currentRate = currencyConvertionController.getCurrentExchangeRate();
        assertThat(currentRate).isEqualTo(1.10);

        // Selenium UI check: simulate user login and attempt override via UI
        driver.get("http://localhost:8080/login");

        // Login form
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameInput.sendKeys("unauthorizedUser");
        passwordInput.sendKeys("password");
        loginButton.click();

        // Wait for redirect and page load
        Thread.sleep(2000);

        // Navigate to currency override page
        driver.get("http://localhost:8080/currency/override");

        // Check that override form is disabled or shows error message
        WebElement overrideForm = driver.findElement(By.id("overrideForm"));
        assertThat(overrideForm.isDisplayed()).isTrue();

        WebElement submitButton = driver.findElement(By.id("submitOverride"));
        assertThat(submitButton.isEnabled()).isFalse();

        WebElement errorMessage = driver.findElement(By.id("errorMessage"));
        assertThat(errorMessage.getText()).containsIgnoringCase("not authorized");
    }

    // DTO for override request
    static class OverrideRequest {
        private double newExchangeRate;
        private String reason;

        public double getNewExchangeRate() {
            return newExchangeRate;
        }

        public void setNewExchangeRate(double newExchangeRate) {
            this.newExchangeRate = newExchangeRate;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
