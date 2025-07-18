/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-3962
 * Epic: BANK-3931
 * Generated on: 2025-07-18 13:39:28
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class ManualRateOverrideTest {
    private WebDriver driver;

    @Mock
    private CurrencyConvertionController currencyConvertionController;

    @InjectMocks
    private ManualRateOverrideScreen manualRateOverrideScreen;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/manual-rate-override");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testManualRateOverride() throws Exception {
        // Mock service response
        when(currencyConvertionController.overrideManualRate(anyString(), anyDouble())).thenReturn("Rate overridden successfully");

        // Locate input fields and buttons
        WebElement currencyPairInput = driver.findElement(By.id("currencyPair"));
        WebElement rateInput = driver.findElement(By.id("overrideRate"));
        WebElement submitButton = driver.findElement(By.id("submitOverride"));

        // Perform actions
        currencyPairInput.sendKeys("USD/EUR");
        rateInput.sendKeys("0.85");
        submitButton.click();

        // Assert expected outcomes
        String expectedSuccessMessage = "Rate overridden successfully";
        // Assuming the result message is shown in a <div> with id 'resultMessage'
        WebElement resultMessage = driver.findElement(By.id("resultMessage"));
        assertEquals(expectedSuccessMessage, resultMessage.getText(), "The manual rate override did not work as expected.");
    }
}