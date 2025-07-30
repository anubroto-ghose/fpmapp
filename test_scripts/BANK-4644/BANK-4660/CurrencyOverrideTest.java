/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4660
 * Epic: BANK-4644
 * Generated on: 2025-07-30 17:01:42
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

public class CurrencyOverrideTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/admin");
        loginAsAdmin();
    }

    private void loginAsAdmin() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("admin");
        passwordField.sendKeys("adminpassword");
        loginButton.click();
    }

    @Test
    public void testManualOverrideCurrencyRates() {
        navigateToCurrencyOverride();
        WebElement currencyCodeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyCode")));
        WebElement rateField = driver.findElement(By.id("newRate"));
        WebElement submitButton = driver.findElement(By.id("submitOverride"));

        currencyCodeField.sendKeys("USD");
        rateField.sendKeys("1.25");
        submitButton.click();

        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmationMessage")));
        assertTrue(confirmationMessage.isDisplayed(), "Confirmation message should be displayed");
        assertEquals("Currency rate updated successfully!", confirmationMessage.getText());
    }

    private void navigateToCurrencyOverride() {
        WebElement currencyOverrideLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currencyOverrideLink")));
        currencyOverrideLink.click();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}