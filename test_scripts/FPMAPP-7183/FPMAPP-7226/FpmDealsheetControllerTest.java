/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7226
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:07:27
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
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FpmDealsheetControllerTest {

    private WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testInrToJpyConversion() {
        // Mock service response
        when(currencyConvertionController.convertInrToJpy(5000)).thenReturn(approximately(6825.00));
        // Navigate to the transaction history page
        driver.get("http://localhost:8080/transaction/history");
        // Locate INR to JPY conversion entry
        WebElement entry = driver.findElement(By.xpath("//div[@class='conversion-entry' and contains(text(), 'INR to JPY')]"));
        assertNotNull(entry, "Entry not found");

        // Check date format
        WebElement dateElement = entry.findElement(By.className("date"));
        assertTrue(dateElement.getText().matches("\d{2}-\d{2}-\d{4}"), "Date format is incorrect");

        // Check INR amount
        WebElement inrAmountElement = entry.findElement(By.className("inr-amount"));
        assertEquals("₹5000", inrAmountElement.getText(), "INR amount displayed incorrectly");

        // Check JPY amount
        WebElement jpyAmountElement = entry.findElement(By.className("jpy-amount"));
        assertEquals("¥6825", jpyAmountElement.getText(), "JPY amount calculated incorrect");

        // Check status
        WebElement statusElement = entry.findElement(By.className("status"));
        assertEquals("Completed", statusElement.getText(), "Status displayed incorrectly");
    }
}