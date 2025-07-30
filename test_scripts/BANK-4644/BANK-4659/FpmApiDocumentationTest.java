/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: BANK-4659
 * Epic: BANK-4644
 * Generated on: 2025-07-30 17:01:57
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FpmApiDocumentationTest {

    private WebDriver driver;

    @Test
    public void validateApiDocumentation() {
        // Set the path for the ChromeDriver
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();

        try {
            // Step 1: Access the API documentation
            driver.get("http://localhost:8080/api-docs");

            // Step 2: Check the section that describes the response structure
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("getfpmlist-doc")));

            // Step 3: Verify the new approval status is documented
            String documentationText = driver.findElement(By.id("getfpmlist-doc")).getText();
            assertTrue(documentationText.contains("approval_status"), "API documentation does not include new approval status.");

            // Check for example response
            assertTrue(documentationText.contains("examples:"), "No examples found in the documentation.");
        } finally {
            // Close the browser
            driver.quit();
        }
    }
}