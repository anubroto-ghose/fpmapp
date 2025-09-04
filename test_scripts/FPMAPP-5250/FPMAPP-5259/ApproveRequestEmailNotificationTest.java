/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5259
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 10:19:25
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.mail.*;
import javax.mail.search.FlagTerm;

import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
import org.springframework.test.context.ActiveProfiles;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import java.util.Properties;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ApproveRequestEmailNotificationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    // Assuming we have service controllers we might need to mock
    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    // Mock email service or simulate it
    @MockBean
    private EmailServiceMock emailServiceMock;

    private static final String BASE_URL = "http://localhost:8080";

    // Test user, approver with pending request
    private User approverUser;

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        // Prepare mock behavior
        approverUser = new User();
        approverUser.setId(1001L);
        approverUser.setUsername("approverUser");
        approverUser.setEmail("approver@example.com");

        // Prepare mocks for common controller
        given(fpmCommonController.getUserByUsername("approverUser")).willReturn(approverUser);

        // Mock email sending behavior
        doNothing().when(emailServiceMock).sendApprovalNotificationEmail(approverUser.getEmail(), "Request #1234 approved");

        // Anything else to initialize or reset
    }

    @Test
    public void testSuccessfulEmailNotificationOnApproval() throws Exception {
        // Step 1: Log in as an approver
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        usernameInput.sendKeys("approverUser");
        passwordInput.sendKeys("password123");
        loginButton.click();

        // Wait for redirect to dashboard / requests page
        wait.until(ExpectedConditions.urlContains("/approvals"));

        // Verify user is on the approvals page
        assertThat(driver.getCurrentUrl()).contains("/approvals");

        // Step 2: Approve the pending request

        // Assume the request row has id 'request-1234' with an approve button
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("approve-btn-1234")));
        approveButton.click();

        // Wait for some confirmation message
        WebElement confirmationMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approval-confirmation-1234")));
        assertThat(confirmationMsg.getText()).contains("Request approved successfully");

        // Step 3: Verify email notification
        // Here we simulate or check email inbox, using the mocked email service or a test mail server

        boolean emailReceived = emailServiceMock.hasEmailWithSubjectAndRecipient(
                "Request #1234 approved", approverUser.getEmail(), 30);

        assertThat(emailReceived).withFailMessage("Expected email notification was not received").isTrue();

        // Also verify email sending was invoked with correct params
        verify(emailServiceMock).sendApprovalNotificationEmail(approverUser.getEmail(), "Request #1234 approved");
    }

    /**
     * Mock EmailService for testing email sending and reception.
     * This is a placeholder implementation. In real test we would
     * interact with a Mail Server like GreenMail or use testcontainers
     * or use in-memory mocking.
     */
    public static class EmailServiceMock {

        // In-memory store for sent emails
        private java.util.List<Email> mailBox = new java.util.ArrayList<>();

        public void sendApprovalNotificationEmail(String toEmail, String subject) {
            mailBox.add(new Email(toEmail, subject));
        }

        public boolean hasEmailWithSubjectAndRecipient(String subject, String toEmail, int waitSeconds) throws InterruptedException {
            int waited = 0;
            while (waited < waitSeconds) {
                for (Email mail : mailBox) {
                    if (mail.getTo().equalsIgnoreCase(toEmail) && mail.getSubject().equalsIgnoreCase(subject)) {
                        return true;
                    }
                }
                TimeUnit.SECONDS.sleep(1);
                waited++;
            }
            return false;
        }

        private static class Email {
            private final String to;
            private final String subject;

            Email(String to, String subject) {
                this.to = to;
                this.subject = subject;
            }

            public String getTo() {
                return to;
            }

            public String getSubject() {
                return subject;
            }
        }
    }
}
