/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-27
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:43:19
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrencyExchangeRateProviderEvaluationTest {

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
    public void testEvaluateCurrencyExchangeRateProviders() {
        driver.get("http://localhost:8080/currency-exchange-rate-providers");

        // Verify the list of potential providers is displayed
        WebElement providerList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("providerList")));
        assertTrue(providerList.isDisplayed(), "Provider list should be displayed.");

        // Select a provider to evaluate
        WebElement firstProvider = providerList.findElement(By.cssSelector("li:first-child"));
        firstProvider.click();

        // Verify the selected provider details are displayed
        WebElement providerDetails = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("providerDetails")));
        assertTrue(providerDetails.isDisplayed(), "Provider details should be displayed after selection.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}