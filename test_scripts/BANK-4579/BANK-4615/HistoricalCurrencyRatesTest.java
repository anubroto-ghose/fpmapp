/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4615
 * Epic: BANK-4579
 * Generated on: 2025-07-30 05:03:19
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
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("testUser");
        passwordField.sendKeys("testPassword");
        loginButton.click();
    }

    @Test
    public void testFilterHistoricalCurrencyRates() {
        navigateToHistoricalRates();
        filterRatesByDateRange("2023-01-01", "2023-01-31");

        WebElement ratesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ratesTable")));
        assertTrue(ratesTable.isDisplayed(), "Rates table should be displayed.");
        assertTrue(ratesTable.getText().contains("2023-01-01"), "Rates for the specified date should be present.");
        assertTrue(ratesTable.getText().contains("2023-01-31"), "Rates for the specified date should be present.");
    }

    private void navigateToHistoricalRates() {
        WebElement historicalRatesLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("historicalRatesLink")));
        historicalRatesLink.click();
    }

    private void filterRatesByDateRange(String startDate, String endDate) {
        WebElement startDateField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startDate")));
        WebElement endDateField = driver.findElement(By.id("endDate"));
        WebElement filterButton = driver.findElement(By.id("filterButton"));

        startDateField.sendKeys(startDate);
        endDateField.sendKeys(endDate);
        filterButton.click();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}