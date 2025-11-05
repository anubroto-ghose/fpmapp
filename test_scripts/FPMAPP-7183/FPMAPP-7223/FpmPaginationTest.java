/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7223
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:08:26
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
import io.github.bonigarcia.wdm.WebDriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FpmPaginationTest {
    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/fpm/list");
    }

    @Test
    public void testPaginationNextAndPrevious() {
        // Step 1: Click the 'Next' button
        WebElement nextButton = driver.findElement(By.id("next-button"));
        nextButton.click();

        // Verifying that records on the next page are displayed
        WebElement resultArea = driver.findElement(By.id("record-display-area"));
        assertTrue(resultArea.getText().contains("Page 2 Records")); // Assuming records contain this text

        // Step 2: Click the 'Previous' button
        WebElement previousButton = driver.findElement(By.id("previous-button"));
        previousButton.click();

        // Verifying that records return to the previous page
        assertTrue(resultArea.getText().contains("Page 1 Records")); // Assuming records contain this text
    }

    @Test
    public void testSelectSpecificPage() {
        // Step 3: Select a specific page number from the pagination controls
        WebElement pageInput = driver.findElement(By.id("page-number-input"));
        pageInput.clear();
        pageInput.sendKeys("3");
        pageInput.submit();

        // Verifying that the correct records are displayed
        WebElement resultArea = driver.findElement(By.id("record-display-area"));
        assertTrue(resultArea.getText().contains("Page 3 Records")); // Assuming records contain this text

        // Assert that the current page is displayed correctly
        WebElement currentPageIndicator = driver.findElement(By.id("current-page"));
        assertEquals("3", currentPageIndicator.getText());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}