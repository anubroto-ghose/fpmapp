/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4614
 * Epic: BANK-4579
 * Generated on: 2025-07-30 05:04:15
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

public class HistoricalCurrencyRatesTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
        login();
    }

    private void login() {
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        usernameField.sendKeys("testUser");
        passwordField.sendKeys("testPassword");
        driver.findElement(By.id("loginButton")).click();
    }

    @Test
    public void testInvalidHistoricalCurrencyRates() {
        driver.get("http://localhost:8080/historical-currency-rates");

        WebElement currencyPairField = driver.findElement(By.id("currencyPair"));
        currencyPairField.sendKeys("INVALID_PAIR");

        WebElement retrieveButton = driver.findElement(By.id("retrieveRatesButton"));
        retrieveButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        WebElement errorMessage = driver.findElement(By.id("errorMessage"));

        assertTrue(errorMessage.isDisplayed(), "Error message should be displayed");
        assertEquals("Invalid criteria provided for retrieving historical rates.", errorMessage.getText());
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}