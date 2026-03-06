/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-34
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:27:54
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FpmAuditTrailTest {

    private WebDriver driver;

    @Autowired
    private CurrencyConvertionController currencyConvertionController;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");
    }

    @Test
    public void testAuditTrailForRejection() {
        // Step 1: Log in as an authorized user
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("authorizedUser");
        passwordField.sendKeys("password123");
        loginButton.click();

        // Step 2: Reject a request
        WebElement rejectButton = driver.findElement(By.id("rejectRequestButton"));
        rejectButton.click();

        // Step 3: Check the audit log for the rejection entry
        driver.get("http://localhost:8080/audit-log");
        WebElement auditLogEntry = driver.findElement(By.xpath("//tr[td[contains(text(), 'Rejection')]]"));

        assertNotNull(auditLogEntry, "Audit log entry for rejection should exist.");
        String entryText = auditLogEntry.getText();
        assertTrue(entryText.contains("authorizedUser"), "Audit log should contain user details.");
        assertTrue(entryText.contains("Rejection"), "Audit log should contain rejection action.");
        assertTrue(entryText.contains(java.time.LocalDateTime.now().toString().substring(0, 10)), "Audit log should contain today's date.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}