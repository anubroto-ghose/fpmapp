/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7211
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:12:48
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FpmDealsheetControllerTest {

    private WebDriver driver;

    @Test
    public void testGetFPMListUnauthorized() throws Exception {
        // Setup WebDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run headless for CI/CD
        driver = new ChromeDriver(options);

        try {
            // Make a GET request without JWT token
            driver.get("http://localhost:8080/api/fpm/list");
            // Mock expected response
            WebElement response = driver.findElement(By.tagName("body"));
            String responseBody = response.getText();

            // Assertions
            assertThat(responseBody).contains("401 Unauthorized");
        } finally {
            // Close the driver
            driver.quit();
        }
    }
}
