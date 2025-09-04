/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-5259
 * Epic: FPMAPP-5250
 * Generated on: 2025-09-04 13:53:27
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import com.webapp.fpmapp.dto.FpmDealsheetController;
import com.webapp.fpmapp.services.FpmCommonController;
import com.webapp.fpmapp.services.FpmForecastController;
import com.webapp.fpmapp.services.CurrencyConvertionController;
import com.webapp.fpmapp.dto.FpmTravelController;
import com.webapp.fpmapp.dto.FpmUserProfileController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // Assuming app runs on defined port (e.g. 8080)
@ActiveProfiles("test")
public class ApprovalNotificationEmailIT {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @MockBean
    private FpmForecastController fpmForecastController;

    @MockBean
    private FpmCommonController fpmCommonController;

    @MockBean
    private FpmDealsheetController fpmDealsheetController;

    @MockBean
    private FpmTravelController fpmTravelController;

    @MockBean
    private FpmUserProfileController fpmUserProfileController;

    @BeforeAll
    public static void setup() {
        // Setup ChromeDriver (assuming chromedriver executable is in PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Preconditions: 
     * User is logged in as approver with a pending request.
     */
    private void loginAsApprover() {
        driver.get("http://localhost:8080/login");

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("loginButton"));

        usernameInput.clear();
        usernameInput.sendKeys("approverUser");
        passwordInput.clear();
        passwordInput.sendKeys("ApproverPass123");
        loginBtn.click();

        // Wait for dashboard or approval requests page
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Verify login success by presence of some element
        WebElement welcomeBanner = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("welcomeBanner")));
        assertTrue(welcomeBanner.getText().toLowerCase().contains("welcome approveruser"));
    }

    /**
     * Setup mock for pending approval requests and notification sending
     */
    private void setupMocks() {
        // Mock currency conversion service - no impact on this test
        Mockito.when(currencyConvertionController.getSupportedCurrencies()).thenReturn(List.of("USD", "EUR", "GBP"));

        // Mock user profile to return approver role
        Mockito.when(fpmUserProfileController.getCurrentUserRole()).thenReturn("APPROVER");

        // Mock common controller for notification sending to succeed
        Mockito.doNothing().when(fpmCommonController).sendApprovalNotification(Mockito.anyString(), Mockito.anyString());

        // Mock dealsheet and travel controllers if they provide approval requests
        // For this test, assume one valid request with id "REQ123" pending
    }

    @Test
    public void testApproverReceivesNotificationOnApproval() throws Exception {
        setupMocks();

        loginAsApprover();

        // Navigate to pending approvals page
        driver.get("http://localhost:8080/approvals/pending");

        // Wait for the table, locate the pending request ID 'REQ123'
        WebElement requestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td/text()='REQ123']")));
        assertNotNull(requestRow, "Pending request REQ123 not found");

        // Click the approve button in that row
        WebElement approveBtn = requestRow.findElement(By.xpath(".//button[contains(@class,'approve-btn')]");
        approveBtn.click();

        // Confirmation dialog appears
        WebElement confirmDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmApprovalDialog")));
        WebElement confirmBtn = confirmDialog.findElement(By.id("confirmApproveBtn"));
        confirmBtn.click();

        // Wait for success toast or message
        WebElement successToast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-success")));
        assertTrue(successToast.getText().toLowerCase().contains("approved"));

        // Poll email inbox for notification
        boolean emailReceived = waitForEmailNotification("approverUser@example.com", "Approval Notification", "REQ123", 60);
        assertTrue(emailReceived, "Expected approval notification email was not received within timeout");
    }

    /**
     * Connect to test email inbox and check for notification email arrival.
     * This uses IMAP over test mail server assumed configured for testing with username/password same as approverUser.
     * @param toEmail recipient email
     * @param subjectContains string expected in subject
     * @param bodyContains string expected in email body
     * @param timeoutInSeconds maximum wait time for email arrival
     * @return true if email received with expected content; false otherwise
     * @throws Exception
     */
    private boolean waitForEmailNotification(String toEmail, String subjectContains, String bodyContains, int timeoutInSeconds) throws Exception {
        Properties props = new Properties();
        // Assuming test SMTP/IMAP server URI and port
        props.setProperty("mail.store.protocol", "imaps");
        String host = "imap.testmail.local";
        String username = "approverUser@example.com";
        String password = "ApproverPass123";

        long endTime = System.currentTimeMillis() + timeoutInSeconds * 1000;

        while (System.currentTimeMillis() < endTime) {
            try {
                Session session = Session.getDefaultInstance(props, null);
                Store store = session.getStore("imaps");
                store.connect(host, username, password);

                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);

                // Search for unread mails
                Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

                for (Message message : messages) {
                    Address[] toAddresses = message.getRecipients(Message.RecipientType.TO);
                    boolean toMatch = false;
                    if (toAddresses != null) {
                        for (Address addr : toAddresses) {
                            if (addr.toString().equalsIgnoreCase(toEmail)) {
                                toMatch = true;
                                break;
                            }
                        }
                    }
                    if (!toMatch) continue;

                    String subject = message.getSubject();
                    if (subject == null || !subject.toLowerCase().contains(subjectContains.toLowerCase())) {
                        continue;
                    }

                    String content = extractTextFromMessage(message);
                    if (content != null && content.contains(bodyContains)) {
                        // Mark message as read
                        message.setFlag(Flags.Flag.SEEN, true);
                        inbox.close(false);
                        store.close();
                        return true;
                    }
                }

                inbox.close(false);
                store.close();

                Thread.sleep(5000); // wait before retrying
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
                return false;
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Extract text content from javax.mail.Message object
     * Handles text/plain and multipart
     */
    private String extractTextFromMessage(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof String) {
                return (String) content;
            } else if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("text/plain")) {
                        return (String) bodyPart.getContent();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
