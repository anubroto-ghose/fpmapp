/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7209
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:13:33
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
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class CurrencyFilterTest {
    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        // Set the path for the WebDriver executable
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/transaction-history");
    }

    @Test
    public void testCurrencyFilter() {
        // Locate currency filter dropdown
        WebElement currencyDropdown = driver.findElement(By.id("currency-dropdown"));
        currencyDropdown.click();

        // Select "INR to JPY"
        WebElement filterOption = driver.findElement(By.xpath("//option[text()='INR to JPY']"));
        filterOption.click();

        // Apply filter
        WebElement applyFilterButton = driver.findElement(By.id("apply-filter-button"));
        applyFilterButton.click();

        // Check filtered results
        List<WebElement> rows = driver.findElements(By.cssSelector(".transaction-row"));
        for (WebElement row : rows) {
            String conversion = row.findElement(By.cssSelector(".conversion-type")).getText();
            assertTrue(conversion.contains("INR to JPY"), "Filtered result does not contain expected conversion type: " + conversion);
        }

        // Ensure no other currency conversions are displayed
        String errorMessage = "Not all transactions are filtered properly. Currency pairs other than INR to JPY should not be visible.";
        assertEquals(rows.size(), 5, errorMessage);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}