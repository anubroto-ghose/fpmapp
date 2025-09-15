/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-6181
 * Epic: FPMAPP-6167
 * Generated on: 2025-09-15 06:11:25
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp.integration;

import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.dto.FpmDealsheetController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * Integration test to verify that changing an approval status triggers an automatic email notification.
 * 
 * Preconditions:
 * - User is assigned an approval task.
 * - SMTP service is configured and operational.
 * - Email notification module integrated with approval status changes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class ApprovalStatusEmailNotificationIT {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmCommonController emailNotificationService;

    // Test data constants
    private static final String BASE_URL = "http://localhost:8080";
    private static final String TEST_USER_USERNAME = "approvalUser1";
    private static final String TEST_USER_PASSWORD = "TestPass123!";
    private static final String APPROVAL_TASK_ID = "12345";

    // Email test config
    private static final String SMTP_HOST = "smtp.testmail.com";
    private static final int SMTP_PORT = 587;
    private static final String EMAIL_USERNAME = "noreply@fpmapp.test";
    private static final String EMAIL_PASSWORD = "emailPassword";
    private static final String RECIPIENT_EMAIL = "approver1@bank.com";

    @BeforeAll
    public static void setupClass() {
        // Setup ChromeDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setup() {
        // Reset mocks before each test
        // Mock email sending behavior: verify that emailNotificationService.sendEmail(...) is called
        doAnswer(invocation -> {
            // Simulate instant email send success
            Object[] args = invocation.getArguments();
            assertNotNull(args[0], "Email recipient cannot be null");
            assertNotNull(args[1], "Email subject cannot be null");
            assertNotNull(args[2], "Email body cannot be null");
            return null;
        }).when(emailNotificationService).sendEmail(any(), any(), any());
    }

    @Test
    public void testApprovalStatusChangeTriggersEmailNotification() throws Exception {
        // Step 1: Login user
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        usernameInput.sendKeys(TEST_USER_USERNAME);
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys(TEST_USER_PASSWORD);
        driver.findElement(By.id("login-submit")).click();

        // Verify landing on user dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Step 2: Open approval task
        driver.get(BASE_URL + "/approvals/task/" + APPROVAL_TASK_ID);

        // Verify approval task page loaded
        WebElement statusElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("approval-status")));
        assertEquals("Pending", statusElement.getText(), "Initial approval status should be Pending");

        // Step 3: Change status from pending to approved
        WebElement approveButton = driver.findElement(By.id("btn-approve"));
        approveButton.click();

        // Wait for status to update
        wait.until(ExpectedConditions.textToBe(By.id("approval-status"), "Approved"));

        // Verify new status
        statusElement = driver.findElement(By.id("approval-status"));
        assertEquals("Approved", statusElement.getText(), "Approval status should change to Approved");

        // Step 4: Verify email notification triggered
        // Verify mock service was called with expected params
        verify(emailNotificationService).sendEmail(
            org.mockito.ArgumentMatchers.eq(RECIPIENT_EMAIL),
            org.mockito.ArgumentMatchers.contains("Approval Status Update"),
            org.mockito.ArgumentMatchers.contains("approved")
        );

        // Step 5: (Optional) Validate email delivery through SMTP logs or IMAP inbox
        // This step would normally require reading from a test mailbox via IMAP to confirm receipt
        // For demonstration, simulate email read from mailbox
        boolean emailReceived = checkEmailReceived(RECIPIENT_EMAIL, "Approval Status Update", "approved");
        assertTrue(emailReceived, "Expected email notification was not received in recipient mailbox");
    }

    /**
     * Connects to the SMTP test mailbox via IMAP and checks for an email containing subject and body text.
     * 
     * @param recipient recipient email address to check
     * @param subjectExpected expected substring in the email subject
     * @param bodyContains expected substring in the email body
     * @return true if such an email is found, false otherwise
     */
    private boolean checkEmailReceived(String recipient, String subjectExpected, String bodyContains) {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.testmail.com", EMAIL_USERNAME, EMAIL_PASSWORD);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Fetch unseen messages
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            for (Message msg : messages) {
                if (msg.getSubject() != null && msg.getSubject().contains(subjectExpected)) {
                    String content = getTextFromMessage(msg).toLowerCase();
                    if (content.contains(bodyContains.toLowerCase())) {
                        inbox.close(false);
                        store.close();
                        return true;
                    }
                }
            }

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to check email inbox: " + e.getMessage());
        }

        return false;
    }

    /**
     * Extracts plain text from javax.mail.Message supporting multipart
     * @param message javax.mail.Message instance
     * @return plain text content
     * @throws Exception on failure
     */
    private String getTextFromMessage(Message message) throws Exception {
        Object content = message.getContent();
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                result.append(bodyPart.getContent().toString());
            }
            return result.toString();
        }
        return "";
    }
}
