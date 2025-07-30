/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4670
 * Epic: BANK-4644
 * Generated on: 2025-07-30 16:58:44
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmailNotificationTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private FpmDealsheetController dealsheetController;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void testEmailNotificationForPendingApproval() throws InterruptedException {
        // Simulate user submitting a dealsheet for approval
        dealsheetController.submitDealsheet();

        // Wait for the email notification to be sent
        Thread.sleep(5000); // Wait for 5 seconds for the email to be sent

        // Check the email inbox for the notification
        driver.get("http://localhost:8080/email-inbox");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email-notification")));

        // Verify the email notification is present
        String emailContent = driver.findElement(By.id("email-notification")).getText();
        assertTrue(emailContent.contains("Your dealsheet is pending approval"), "Email notification not found.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}