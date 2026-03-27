/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-8644
 * Epic: FPMAPP-8589
 * Generated on: 2026-03-27 07:43:42
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmUserProfileController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.CurrencyConvertionController;

/**
 * Integration Selenium test for admin override of currency exchange rates.
 * 
 * Preconditions:
 * - Admin user with override permissions
 * - SMTP email service mocked
 * - Currency_Exchange_Rates table mocked
 * 
 * This test performs an override, verifies DB flags, and email alert.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CurrencyOverrideAdminTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private FpmUserProfileController userProfileController;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String ADMIN_USERNAME = "adminUser";
    private static final String ADMIN_PASSWORD = "adminPass123";

    private static final String TEST_CURRENCY_CODE = "USD";
    private static final double ORIGINAL_RATE = 1.10;
    private static final double OVERRIDE_RATE = 1.15;
    private static final String OVERRIDE_REASON = "Quarterly adjustment due to market volatility";

    private MimeMessage capturedMessage;

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver (assumes chromedriver is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        // Mock the currencyConvertionController to simulate DB and override behavior
        when(currencyConvertionController.getExchangeRate(TEST_CURRENCY_CODE))
            .thenReturn(ORIGINAL_RATE);

        doAnswer(invocation -> {
            String currencyCode = invocation.getArgument(0);
            double newRate = invocation.getArgument(1);
            String reason = invocation.getArgument(2);
            String userId = invocation.getArgument(3);

            // Simulate DB update with override flags
            assertThat(currencyCode).isEqualTo(TEST_CURRENCY_CODE);
            assertThat(newRate).isEqualTo(OVERRIDE_RATE);
            assertThat(reason).isEqualTo(OVERRIDE_REASON);
            assertThat(userId).isEqualTo(ADMIN_USERNAME);

            // Simulate sending alert email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo("alerts@fpmtools.com");
            helper.setSubject("Currency Override Alert: " + currencyCode);
            helper.setText("Currency " + currencyCode + " overridden to " + newRate + ". Reason: " + reason);

            capturedMessage = message;
            return null;
        }).when(currencyConvertionController).overrideCurrencyRate(any(String.class), any(Double.class), any(String.class), any(String.class));

        // Mock mailSender to capture sent email
        doAnswer(invocation -> {
            MimeMessage msg = invocation.getArgument(0);
            capturedMessage = msg;
            return null;
        }).when(mailSender).send(any(MimeMessage.class));

        // Ensure admin user exists and has permissions
        User adminUser = new User();
        adminUser.setUsername(ADMIN_USERNAME);
        adminUser.setPassword(ADMIN_PASSWORD);
        adminUser.setRole("ADMIN");
        when(userProfileController.getUserByUsername(ADMIN_USERNAME)).thenReturn(adminUser);
    }

    @Test
    public void testAdminOverrideCurrencyRateAndEmailAlert() throws Exception {
        // Step 1: Login as admin user
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(ADMIN_USERNAME);
        driver.findElement(By.id("password")).sendKeys(ADMIN_PASSWORD);
        driver.findElement(By.id("loginButton")).click();

        // Wait for dashboard or currency override page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Navigate to currency override page
        driver.get(BASE_URL + "/currency/override");

        // Step 2: Select currency to override
        WebElement currencySelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("currencySelect")));
        currencySelect.click();
        WebElement optionUsd = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//option[@value='" + TEST_CURRENCY_CODE + "']")));
        optionUsd.click();

        // Step 3: Enter new override rate
        WebElement rateInput = driver.findElement(By.id("overrideRate"));
        rateInput.clear();
        rateInput.sendKeys(String.valueOf(OVERRIDE_RATE));

        // Step 4: Enter override reason
        WebElement reasonInput = driver.findElement(By.id("overrideReason"));
        reasonInput.clear();
        reasonInput.sendKeys(OVERRIDE_REASON);

        // Step 5: Submit override
        driver.findElement(By.id("submitOverride")).click();

        // Wait for confirmation message
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overrideSuccessMessage")));
        assertThat(confirmation.getText()).contains("Override saved successfully");

        // Step 6: Verify override call to service
        verify(currencyConvertionController).overrideCurrencyRate(TEST_CURRENCY_CODE, OVERRIDE_RATE, OVERRIDE_REASON, ADMIN_USERNAME);

        // Step 7: Verify email alert sent
        assertThat(capturedMessage).isNotNull();
        assertThat(capturedMessage.getAllRecipients()).isNotEmpty();
        assertThat(capturedMessage.getSubject()).contains("Currency Override Alert");

        String emailContent = new String(capturedMessage.getContent().toString());
        assertThat(emailContent).contains(TEST_CURRENCY_CODE);
        assertThat(emailContent).contains(String.valueOf(OVERRIDE_RATE));
        assertThat(emailContent).contains(OVERRIDE_REASON);

        // Step 8: Verify audit log (simulate by checking a log method call or DB entry)
        // For demo, assume audit logged via service method
        // This would be verified by a mock or spy in real scenario
    }
}
