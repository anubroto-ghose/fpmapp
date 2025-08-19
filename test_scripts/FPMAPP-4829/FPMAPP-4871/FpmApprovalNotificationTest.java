/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4871
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-19 06:06:29
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ActiveProfiles("test")
public class FpmApprovalNotificationTest {

    private WebDriver driver;

    @MockBean
    private CurrencyConvertionController currencyConvertionController;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        // Mock the service to simulate a successful response
        Mockito.when(currencyConvertionController.convertCurrency(any(), any(), any())).thenReturn(100.0);
    }

    @Test
    public void testFallbackNotificationOnWebSocketFailure() {
        // Simulate WebSocket disconnection
        doThrow(new RuntimeException("WebSocket Disconnected")).when(currencyConvertionController).notifyApprovalStatus(any());

        // Navigate to the application
        driver.get("http://localhost:8080/fpmapp");

        // Trigger the approval status change
        driver.findElement(By.id("triggerApprovalChangeButton")).click();

        // Wait for the fallback notification to appear
        try {
            Thread.sleep(5000); // Wait for 5 seconds for the fallback notification to appear
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check for fallback notification in the UI
        boolean fallbackNotificationDisplayed = driver.findElements(By.id("fallbackNotification")).size() > 0;
        assertTrue(fallbackNotificationDisplayed, "Fallback notification should be displayed when WebSocket is disconnected.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}