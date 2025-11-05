/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7213
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:12:04
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
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FpmDealsheetNavigationTest {

    private WebDriver driver;
    
    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/fpm/records");
    }

    @Test
    public void testNavigateToFpmRecordDetail() {
        // Fetch the first FPM record and click on it
        WebElement firstRecord = driver.findElement(By.cssSelector(".fpm-record:first-child"));
        firstRecord.click();
        
        // Verify that the navigation was successful
        String expectedUrl = "http://localhost:8080/fpm/record/detail";
        String currentUrl = driver.getCurrentUrl();
        assertEquals(expectedUrl, currentUrl);
    }
    
    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}