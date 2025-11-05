/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7229
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:05:59
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@WebMvcTest(FpmDealsheetController.class)
public class FpmDealsheetControllerTest {

    @InjectMocks
    private FpmDealsheetController fpmDealsheetController;

    @Mock
    private CurrencyConvertionController currencyConvertionController;

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080");
    }

    @Test
    public void testCurrencyFilter() throws Exception {
        given(currencyConvertionController.getConversions("INR", "JPY"))
            .willReturn(ResponseEntity.ok(Arrays.asList("Conversion1", "Conversion2")));
        
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transaction-history")));

        driver.findElement(By.id("currency-filter")).click();
        driver.findElement(By.xpath("//option[text()='INR to JPY']")).click();
        driver.findElement(By.id("filter-button")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("conversion-list")));
        String conversionList = driver.findElement(By.id("conversion-list")).getText();

        assertEquals("Conversion1\nConversion2", conversionList);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
