/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7215
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:11:26
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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FpmDealsheetReturnTest {
    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/fpm/dealsheet/detail");
    }

    @Test
    public void testReturnToFpmRecordList() throws InterruptedException {
        // Simulate user interaction by clicking return button
        WebElement returnButton = driver.findElement(By.id("return-button"));
        returnButton.click();

        // Wait for the page to load
        Thread.sleep(1000);

        // Verify that the current URL is the FPM record list
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/fpm/list"), "Should navigate to the FPM records list");

        // Assuming a mechanism to retain selection is in place, verify that the proper record is selected
        WebElement selectedRecord = driver.findElement(By.className("selected-fpm-record"));
        assertNotNull(selectedRecord, "Should be back on the same FPM record");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}