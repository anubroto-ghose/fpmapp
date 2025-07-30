/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4667
 * Epic: BANK-4644
 * Generated on: 2025-07-30 16:59:32
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FpmApprovalNotificationTest {

    private WebDriver driver;

    @MockBean
    private JavaMailSender mailSender;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");
    }

    @Test
    public void testEmailNotificationForRejectedApproval() throws InterruptedException {
        // Step 1: Login as finance manager
        driver.findElement(By.id("username")).sendKeys("finance_manager");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.id("loginButton")).click();

        // Step 2: Navigate to the financial entry
        driver.findElement(By.id("financialEntriesLink")).click();
        driver.findElement(By.id("entryId")).click();

        // Step 3: Change the approval status to 'rejected'
        driver.findElement(By.id("changeStatusButton")).click();
        driver.findElement(By.id("statusDropdown")).click();
        driver.findElement(By.xpath("//option[text()='Rejected']")).click();
        driver.findElement(By.id("submitStatusChangeButton")).click();

        // Step 4: Wait for email notification
        Thread.sleep(5000); // Wait for email to be sent

        // Verify email was sent
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("finance_manager@example.com");
        message.setSubject("Approval Status Notification");
        message.setText("The financial entry has been rejected.");

        verify(mailSender).send(message);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}