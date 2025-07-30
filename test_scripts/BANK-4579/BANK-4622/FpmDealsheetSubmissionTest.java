/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4622
 * Epic: BANK-4579
 * Generated on: 2025-07-30 04:57:45
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FpmDealsheetSubmissionTest {

    private WebDriver driver;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        // Mocking the service response
        when(fpmCommonController.submitDealsheet(any())).thenReturn("Approval workflow triggered");
    }

    @Test
    public void testDealsheetSubmissionTriggersApprovalWorkflow() {
        driver.get("http://localhost:8080/dealsheet/submit");

        // Fill out the dealsheet form
        WebElement titleField = driver.findElement(By.id("dealsheetTitle"));
        titleField.sendKeys("Q1 2024 Budget Proposal");

        WebElement amountField = driver.findElement(By.id("dealsheetAmount"));
        amountField.sendKeys("10000");

        WebElement submitButton = driver.findElement(By.id("submitDealsheet"));
        submitButton.click();

        // Verify confirmation message
        WebElement confirmationMessage = driver.findElement(By.id("confirmationMessage"));
        assertEquals("Approval workflow triggered", confirmationMessage.getText());
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}