/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7219
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:09:57
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FpmRecordsFilterTest {

    @Autowired
    private WebDriver driver;

    @MockBean
    private FpmService fpmService;

    @BeforeEach
    public void setUp() {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void testFilterFpmRecordsByStatus() {
        // Mock the service response
        when(fpmService.getFpmRecordsWithFilter("active", null, null, null)).thenReturn(List.of(new FpmRecord(...)));

        driver.get("http://localhost:8080/api/fpm/list");

        // Click on the status filter dropdown
        WebElement statusDropdown = driver.findElement(By.id("statusFilterDropdown"));
        statusDropdown.click();

        // Select an active status from the dropdown
        WebElement activeStatusOption = driver.findElement(By.xpath("//option[text()='Active']"));
        activeStatusOption.click();

        // Click on the Apply button to filter records
        WebElement applyButton = driver.findElement(By.id("applyFilterButton"));
        applyButton.click();

        // Wait for results to be displayed
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fpmRecordsList")));

        // Verify that the displayed records contain the selected status
        List<WebElement> displayedRecords = driver.findElements(By.className("fpmRecord"));
        for (WebElement record : displayedRecords) {
            assertTrue(record.getText().contains("Active"), "Record should be active");
        }
    }
}