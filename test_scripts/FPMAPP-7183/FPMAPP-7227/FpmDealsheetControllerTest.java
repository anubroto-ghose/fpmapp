/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7227
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:07:02
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class FpmDealsheetControllerTest {

    private WebDriver driver;

    @Mock
    private CurrencyConvertionController currencyConvertionController;

    @InjectMocks
    private FpmDealsheetController fpmDealsheetController;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/fpm/list");
    }

    @Test
    @Rollback
    public void testNoInrToJpyConversionsExist() {
        // Mocking service call response
        Mockito.when(currencyConvertionController.getCurrentConversions()).thenReturn(new ArrayList<>());

        WebElement filterInput = driver.findElement(By.id("currencyFilter"));
        filterInput.sendKeys("INR to JPY");

        WebElement filterButton = driver.findElement(By.id("filterButton"));
        filterButton.click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noResultsMessage")));

        WebElement noResultsMessage = driver.findElement(By.id("noResultsMessage"));
        assertEquals("No INR to JPY transactions found", noResultsMessage.getText());
        assertTrue(driver.findElements(By.className("transactionRow")).isEmpty());
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
