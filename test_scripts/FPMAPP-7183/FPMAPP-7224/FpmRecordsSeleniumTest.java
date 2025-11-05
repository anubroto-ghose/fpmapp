/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7224
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:08:10
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

import java.time.Duration;

public class FpmRecordsSeleniumTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("http://localhost:8080/fpm/records");
    }

    @Test
    public void testResponseTimeForPaginatedDataRetrieval() {
        long startTime = System.currentTimeMillis();

        // Trigger the action to load records
        WebElement loadRecordsButton = driver.findElement(By.id("loadRecordsButton"));
        loadRecordsButton.click();

        // Wait for the records to be loaded and displayed
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("recordsTable")));

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Verify response time
        assertTrue(responseTime <= 600, "Response time exceeded 600 ms: " + responseTime + " ms");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
