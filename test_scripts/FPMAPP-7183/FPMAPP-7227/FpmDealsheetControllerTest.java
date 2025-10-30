/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7227
 * Epic: FPMAPP-7183
 * Generated on: 2025-10-30 17:59:31
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FpmDealsheetControllerTest {

    private WebDriver driver;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        // Mocking the service response
        when(fpmCommonController.getTransactionHistory(anyString())).thenReturn(new ArrayList<>());
    }

    @Test
    public void testNoInrToJpyTransactions() {
        driver.get("http://localhost:8080/transaction-history");

        // Filter for INR to JPY conversion
        WebElement filterDropdown = driver.findElement(By.id("currencyFilter"));
        filterDropdown.sendKeys("INR to JPY");
        WebElement filterButton = driver.findElement(By.id("filterButton"));
        filterButton.click();

        // Check for the message
        WebElement messageElement = driver.findElement(By.id("noTransactionsMessage"));
        String message = messageElement.getText();
        assertEquals("No INR to JPY transactions found", message);

        // Ensure no unrelated transactions are shown
        WebElement transactionList = driver.findElement(By.id("transactionList"));
        assertTrue(transactionList.getText().isEmpty(), "Transaction list should be empty");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}