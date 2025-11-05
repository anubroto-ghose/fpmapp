/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7218
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:10:19
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FpmDealsheetSortingTest {

    private WebDriver driver;

    @MockBean
    private FpmCommonController fpmCommonController;

    @Mock
    private FpmForecastController fpmForecastController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");  // Update for your path
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/fpm/html");  // Replace with your actual app URL

        // Mock the backend call for sorting to simulate a failure
        when(fpmCommonController.sortFpmRecords()).thenThrow(new RuntimeException("Sorting functionality is disabled"));
    }

    @Test
    public void testSortingFailureDisplaysErrorMessage() {
        WebElement sortButton = driver.findElement(By.id("sort-project-name"));  // Assumed button ID for sorting
        sortButton.click();

        // Check for error message
        WebElement errorMessage = driver.findElement(By.id("error-message"));  // Assumed error message ID
        assertNotNull(errorMessage);
        assertEquals("Sorting has failed. Please try again later.", errorMessage.getText());
    }

    @AfterEach
    public void teardown() {
        driver.quit();
    }
}