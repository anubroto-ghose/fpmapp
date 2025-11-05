/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7222
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:08:49
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Import({FpmDealsheetController.class})
public class FpmDealsheetPaginationTest {

    private WebDriver driver;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        // Set the path for the ChromeDriver.
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        // Initialize MockMvc if necessary for tests
        this.mockMvc = MockMvcBuilders.standaloneSetup(mockMvc).build();
        driver.manage().window().maximize();
    }

    @Test
    public void testPaginatedFpmRecordDisplay() {
        // Given
        driver.get("http://localhost:8080/fpm/records");

        // When
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("recordsList")));
        int recordsOnPage = driver.findElements(By.className("fpmRecord")).size();

        // Then
        int expectedRecordsPerPage = 10; // Example value based on pagination setting
        assertThat("Number of records displayed should match the expected count", recordsOnPage, is(expectedRecordsPerPage));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}