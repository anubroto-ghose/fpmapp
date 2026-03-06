/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-33
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:27:39
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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ApprovalLoggingTest {

    private WebDriver driver;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");
    }

    @Test
    public void testApprovalLogging() throws InterruptedException {
        // Mocking the service response
        when(fpmCommonController.getUserDetails(Mockito.anyLong())).thenReturn(new User(1L, "testuser", "testuser@example.com"));

        // Step 1: Log in as an authorized user
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("testuser");
        passwordField.sendKeys("password");
        loginButton.click();

        // Wait for login to complete
        Thread.sleep(2000);

        // Step 2: Approve a request
        WebElement approveButton = driver.findElement(By.id("approveButton"));
        approveButton.click();

        // Wait for approval to be processed
        Thread.sleep(2000);

        // Step 3: Check the audit log for the approval entry
        driver.get("http://localhost:8080/audit-log");
        WebElement auditLogEntry = driver.findElement(By.xpath("//tr[td[contains(text(), 'Approved')]]"));

        // Assertions
        assertNotNull(auditLogEntry, "Audit log entry should exist.");
        assertTrue(auditLogEntry.getText().contains("testuser"), "Audit log should contain user details.");
        assertTrue(auditLogEntry.getText().contains("Approved"), "Audit log should contain approval action.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}