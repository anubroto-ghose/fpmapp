/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7220
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:09:34
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(username = "user", roles = {"USER"})
public class FpmDealsheetFilterTest {

    private WebDriver driver;

    @MockBean
    private FpmCommonController fpmCommonController;

    @BeforeEach
    public void setup() {
        // Set the path to the WebDriver executable
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/fpm-records"); // URL of the FPM records page
    }

    @Test
    public void testFilterWithNoResults() {
        // Mocking the service to return no records for the filter
        when(fpmCommonController.filterRecordsByStatus("non-existent-status"))
            .thenReturn(new ArrayList<>());

        // Click on status filter dropdown
        WebElement statusFilterDropdown = driver.findElement(By.id("statusFilter"));
        statusFilterDropdown.click();

        // Select a non-existent status
        WebElement statusOption = driver.findElement(By.xpath("//option[text()='non-existent-status']"));
        statusOption.click();

        // Click on Apply button
        WebElement applyButton = driver.findElement(By.id("applyButton"));
        applyButton.click();

        // Check for no records found message
        WebElement noRecordsMessage = driver.findElement(By.id("noRecordsMessage"));
        String messageText = noRecordsMessage.getText();

        // Validate error message
        assertEquals("No records found for the selected filter.", messageText);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}