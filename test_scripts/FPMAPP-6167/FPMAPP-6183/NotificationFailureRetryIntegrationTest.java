/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6183
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:08:55
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.entities.User;
import com.webapp.fpmapp.services.FpmCommonController;

/**
 * Integration test verifying notification failure handling and retry mechanism.
 * 
 * Preconditions:
 * - SMTP service is mocked as disabled/failing.
 * - Retry and error logging enabled as per FPMAPP-6175 requirements.
 * 
 * This test runs a Spring Boot context with Selenium WebDriver to simulate UI approval status change,
 * then verifies backend notification retry and error log.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({})
@Tag("integration")
public class NotificationFailureRetryIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(NotificationFailureRetryIntegrationTest.class);

    private WebDriver driver;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FpmDealsheetController dealsheetController;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private FpmCommonController fpmCommonController;

    /**
     * Counter to mock retry attempts
     */
    private AtomicInteger sendMailCallCount;

    @BeforeAll
    public void setUpAll() {
        // Note: Make sure chromedriver binary is available in PATH or set webdriver.chrome.driver
        System.setProperty("webdriver.chrome.silentOutput", "true");
        driver = new ChromeDriver();
        sendMailCallCount = new AtomicInteger(0);
    }

    @AfterAll
    public void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setUp() {
        sendMailCallCount.set(0);
        // Mocking mailSender to throw MailSendException simulating SMTP failure
        doThrow(new MailSendException("Simulated SMTP down"))
            .doAnswer(invocation -> {
                logger.info("Simulated email send success on retry.");
                return null;
            })
            .when(mailSender).send(any());

        logger.info("Test setup complete: mailSender mocked to fail first then succeed.");
    }

    @AfterEach
    public void tearDown() {
        // Reset mocks if necessary
    }

    @Test
    public void testNotificationFailureRetryHandling() throws Exception {
        logger.info("Starting test: Notification failure and retry handling");

        // Given: A user and approval item state
        User testUser = new User();
        testUser.setId(1001L);
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@bank.com");

        long testApprovalId = 5001L; // test deal sheet approval id

        // Preconditions: The approval currently pending
        // (Assuming we have an API or direct DB setup for the test environment)
        boolean initialStatus = dealsheetController.isApprovalPending(testApprovalId);
        if (!initialStatus) {
            throw new IllegalStateException("Approval must be initially pending for this test");
        }

        // Step 1: Navigate UI to approval page and change the approval status to trigger notification
        driver.get("http://localhost:8080/fpm/dealsheet/approval/" + testApprovalId);

        // Find approval status dropdown/select
        WebElement approvalDropdown = driver.findElement(By.id("approvalStatusSelect"));

        // Change status to approved which triggers notification email
        approvalDropdown.click();
        WebElement approvedOption = driver.findElement(By.xpath("//option[@value='APPROVED']"));
        approvedOption.click();

        // Click save/submit button
        WebElement submitButton = driver.findElement(By.id("submitApprovalChange"));
        submitButton.click();

        // Wait for some seconds for backend processing and retry mechanisms to action
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(3))
                .ignoring(Exception.class);

        wait.until((ExpectedCondition<Boolean>) d -> {
            // The dealsheetController should reflect the approved status now
            return dealsheetController.getApprovalStatus(testApprovalId).equals("APPROVED");
        });

        // Step 2,3: Observe system handling of email failure and retry
        // Verify mailSender.send was called at least twice (fail + retry success)
        verify(mailSender, times(2)).send(any());

        // Step 4: Check error logs for proper failure recording
        boolean errorLogged = fpmCommonController.isNotificationErrorLogged(testApprovalId, "MailSendException");
        if (!errorLogged) {
            throw new AssertionError("Expected notification failure error log record not found");
        }

        // Step 5: Simulate SMTP restore by changing mail sender to not throw exception
        // Already simulated via doThrow...doAnswer(..) above: second call is success

        // Confirm notification eventually succeeded (indicated by mailSender.send second call and
        // system audit)
        boolean notificationSuccess = fpmCommonController.isNotificationSuccessRecorded(testApprovalId);
        if (!notificationSuccess) {
            throw new AssertionError("Notification success after retry not recorded");
        }

        logger.info("Test completed successfully: Notification failure retry and logging works as expected.");
    }
}