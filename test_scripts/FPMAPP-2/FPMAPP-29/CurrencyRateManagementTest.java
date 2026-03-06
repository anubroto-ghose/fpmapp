/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-29
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:43:57
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

public class CurrencyRateManagementTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        driver.get("http://localhost:8080/login");
        loginAsAdmin();
    }

    private void loginAsAdmin() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("admin");
        passwordField.sendKeys("adminPassword");
        loginButton.click();
    }

    @Test
    public void testAdminOverrideCurrencyRate() {
        driver.get("http://localhost:8080/currency-rates");

        // Identify an incorrect currency rate
        WebElement incorrectRate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[text()='USD']]/td[@class='rate']")));
        String originalRate = incorrectRate.getText();

        // Use the admin override option to correct the rate
        WebElement overrideButton = driver.findElement(By.xpath("//tr[td[text()='USD']]/td/button[@class='override']"));
        overrideButton.click();

        WebElement newRateField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newRate")));
        newRateField.clear();
        newRateField.sendKeys("1.25"); // Correcting the rate

        WebElement saveButton = driver.findElement(By.id("saveButton"));
        saveButton.click();

        // Verify the currency rate is updated
        wait.until(ExpectedConditions.textToBePresentInElement(incorrectRate, "1.25"));
        String updatedRate = incorrectRate.getText();

        assertEquals("1.25", updatedRate);
        assertTrue(!updatedRate.equals(originalRate), "The currency rate should be updated.");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}