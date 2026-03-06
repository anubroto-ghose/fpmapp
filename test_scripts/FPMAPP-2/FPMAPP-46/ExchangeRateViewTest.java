/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-46
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:48:53
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
import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRateViewTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
    }

    @Test
    public void testViewCurrentAndHistoricalExchangeRates() {
        // Step 1: Navigate to the exchange rates page.
        driver.get("http://localhost:8080/exchange-rates");

        // Step 2: Select a currency pair to view.
        WebElement currencyPairDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("currencyPairDropdown")));
        currencyPairDropdown.click();
        WebElement selectedCurrencyPair = driver.findElement(By.xpath("//option[text()='USD/EUR']"));
        selectedCurrencyPair.click();

        // Step 3: Check for current and historical rates.
        WebElement currentRateElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currentRate")));
        String currentRate = currentRateElement.getText();
        assertNotNull(currentRate, "Current rate should be displayed.");

        WebElement historicalRatesButton = driver.findElement(By.id("historicalRatesButton"));
        historicalRatesButton.click();

        WebElement historicalRatesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("historicalRatesTable")));
        assertTrue(historicalRatesTable.isDisplayed(), "Historical rates should be accessible.");

        // Additional assertions can be added to verify the accuracy of the rates.
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}