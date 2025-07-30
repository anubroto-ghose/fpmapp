/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4671
 * Epic: BANK-4644
 * Generated on: 2025-07-30 16:58:23
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FinancialEntryApprovalTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
        loginAsFinancialAnalyst();
    }

    private void loginAsFinancialAnalyst() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("financialAnalyst");
        passwordField.sendKeys("password123");
        loginButton.click();
    }

    @Test
    public void testPreventDirectSavingOfFinancialEntry() {
        driver.get("http://localhost:8080/financial-entry-form");

        WebElement field1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("field1")));
        WebElement field2 = driver.findElement(By.id("field2"));
        WebElement saveButton = driver.findElement(By.id("saveButton"));

        field1.sendKeys("Valid Data 1");
        field2.sendKeys("Valid Data 2");

        saveButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        assertTrue(errorMessage.isDisplayed(), "Error message should be displayed");
        assertEquals("Approval is required before saving.", errorMessage.getText(), "Error message text is incorrect");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}