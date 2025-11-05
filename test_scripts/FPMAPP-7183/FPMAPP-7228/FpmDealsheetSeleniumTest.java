/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7228
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:06:20
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@SpringBootTest
public class FpmDealsheetSeleniumTest {

    private WebDriver driver;

    @Mock
    private CurrencyConvertionController currencyConvertionController;

    @Mock
    private FpmForecastController fpmForecastController;

    @InjectMocks
    private FpmUserProfileController fpmUserProfileController;

    @BeforeEach
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testPaginationForINRtoJPY() {
        // Mocking service behavior
        when(currencyConvertionController.getConversionData()).thenReturn(mockConversionData());

        // Test steps
        driver.get("http://localhost:8080/transactions");
        WebElement paginationControl = driver.findElement(By.id("pagination-control"));
        paginationControl.click(); // Simulates clicking on next page

        // Assertions
        WebElement transactionList = driver.findElement(By.id("transaction-list"));
        assertNotNull(transactionList);
        assertTrue(transactionList.getText().contains("INR to JPY"), "Page did not load INR to JPY transactions.");
        // Assert pagination works as expected
        WebElement nextPage = driver.findElement(By.id("next-page"));
        assertTrue(nextPage.isDisplayed(), "Next page should be visible when there are more transactions.");
    }

    private List<ConversionData> mockConversionData() {
        // Populate mock data for conversion
        return Arrays.asList(
                new ConversionData("INR", "JPY", 1.5),
                new ConversionData("INR", "JPY", 1.6)
        );
    }
}