/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7205
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:15:01
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TransactionHistoryTest {

    private WebDriver driver;

    @Mock
    private FpmCommonController fpmCommonController;

    @InjectMocks
    private CurrencyConvertionController currencyConvertionController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void testTransactionHistoryForINRToJPY() {
        // Step: Navigate to the transaction history page
        driver.get("http://localhost:8080/transactionHistory");

        // Mock service response for transaction history
        when(fpmCommonController.getTransactionHistory("INR", "JPY"))
            .thenReturn(List.of(new Transaction("2025-11-01", 1000, 7200, "Completed"),
                                 new Transaction("2025-11-02", 500, 3600, "Completed")));

        // Step: Filter or select currency pair as INR to JPY
        WebElement currencyFilter = driver.findElement(By.id("currencyFilter"));
        currencyFilter.sendKeys("INR to JPY");

        WebElement filterButton = driver.findElement(By.id("filterButton"));
        filterButton.click();

        // Step: Observe the listed transactions
        List<WebElement> transactionRows = driver.findElements(By.cssSelector("#transactionTable tbody tr"));

        // Assertions
        Assertions.assertEquals(2, transactionRows.size(), "Should show 2 transactions");
        Assertions.assertEquals("2025-11-01", transactionRows.get(0).findElement(By.cssSelector(".transactionDate")).getText());
        Assertions.assertEquals("1000", transactionRows.get(0).findElement(By.cssSelector(".amountINR")).getText());
        Assertions.assertEquals("7200", transactionRows.get(0).findElement(By.cssSelector(".amountJPY")).getText());
        Assertions.assertEquals("Completed", transactionRows.get(0).findElement(By.cssSelector(".transactionStatus")).getText());

        Assertions.assertEquals("2025-11-02", transactionRows.get(1).findElement(By.cssSelector(".transactionDate")).getText());
        Assertions.assertEquals("500", transactionRows.get(1).findElement(By.cssSelector(".amountINR")).getText());
        Assertions.assertEquals("3600", transactionRows.get(1).findElement(By.cssSelector(".amountJPY")).getText());
        Assertions.assertEquals("Completed", transactionRows.get(1).findElement(By.cssSelector(".transactionStatus")).getText());
    }

    // Add cleanup method if needed
    // @AfterEach
    // public void tearDown() {
    //     driver.quit();
    // }
}