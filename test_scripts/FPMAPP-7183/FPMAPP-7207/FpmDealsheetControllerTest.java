/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7207
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:14:17
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class FpmDealsheetControllerTest {

    private WebDriver driver;

    @Mock
    private FpmCommonController fpmCommonController;

    @InjectMocks
    private FpmDealsheetController fpmDealsheetController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/fpm/dealsheet");
    }

    @Test
    public void testNoInrToJpyTransactions() {
        // Mocking the service call to return no transactions
        when(fpmCommonController.getInrToJpyTransactions()).thenReturn(new ArrayList<>());

        // Interacting with the UI elements
        WebElement filterInput = driver.findElement(By.id("filterInput"));
        filterInput.sendKeys("INR to JPY");
        WebElement filterButton = driver.findElement(By.id("filterButton"));
        filterButton.click();

        // Expecting a message indicating no transactions
        WebElement messageElement = driver.findElement(By.id("noTransactionsMessage"));
        assertTrue(messageElement.isDisplayed());
        assertEquals("No INR to JPY transactions found", messageElement.getText());

        // Verifying that the mocked method was called
        verify(fpmCommonController).getInrToJpyTransactions();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
