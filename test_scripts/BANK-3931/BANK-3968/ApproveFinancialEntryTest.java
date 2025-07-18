/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-3968
 * Epic: BANK-3931
 * Generated on: 2025-07-18 13:33:08
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import io.github.bonigarcia.wdm.WebDriverManager;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ApproveFinancialEntryTest {

    @Autowired
    private WebDriver driver;

    @MockBean
    private FpmMailController fpmMailController;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testProgramDirectorRejectsFinancialEntry() {
        // Step 1: Program Director logs in
        driver.get("http://localhost:8080/login");
        WebElement usernameField = driver.findElement(By.name("username"));
        WebElement passwordField = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("program_director");
        passwordField.sendKeys("securePassword");
        loginButton.click();

        // Assertion: Verify login success
        assertTrue(driver.getCurrentUrl().contains("/dashboard"));

        // Step 2: Navigate to the approval section
        WebElement approvalSection = driver.findElement(By.id("approvalSection"));
        approvalSection.click();

        // Step 3: Select the financial entry to reject
        WebElement financialEntry = driver.findElement(By.id("financialEntry1"));
        financialEntry.click();
        WebElement rejectButton = driver.findElement(By.id("rejectButton"));
        rejectButton.click();

        // Step 4: Confirm reject action
        WebElement confirmRejectButton = driver.findElement(By.id("confirmRejectButton"));
        confirmRejectButton.click();

        // Assertion: Check success message
        WebElement successMessage = driver.findElement(By.id("successMessage"));
        assertEquals("Financial entry has been rejected successfully.", successMessage.getText());

        // Check the status change in DB or UI
        // Mock the fpmMailController to verify notification
        verify(fpmMailController, times(1)).sendApprovalNotification(any());
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}