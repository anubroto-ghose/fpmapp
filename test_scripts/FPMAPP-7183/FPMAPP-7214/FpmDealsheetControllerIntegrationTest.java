/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-7214
 * Epic: FPMAPP-7183
 * Generated on: 2025-11-05 11:11:49
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; 

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest
@AutoConfigureMockMvc
public class FpmDealsheetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testFpmRecordDetailsView() throws Exception {
        // Given: Mocking the expected response
        String expectedResponse = "{\"data\":{\"projectName\":\"Sample Project\", \"customer\":\"ABC Corp\", \"status\":\"Active\", \"lastModified\":\"2025-11-05T12:00:00Z\"}}";
        mockMvc.perform(MockMvcRequestBuilders.get("/api/fpm/details/1"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    assertThat(jsonResponse).isEqualTo(expectedResponse);
                });

        // When: Navigate to detailed view page
        driver.get("http://localhost:8080/fpm/details/1");

        // Then: Verify the data on the UI
        WebElement projectName = driver.findElement(By.id("project-name"));
        WebElement customer = driver.findElement(By.id("customer"));
        WebElement status = driver.findElement(By.id("status"));
        WebElement lastModified = driver.findElement(By.id("last-modified"));

        assertThat(projectName.getText()).isEqualTo("Sample Project");
        assertThat(customer.getText()).isEqualTo("ABC Corp");
        assertThat(status.getText()).isEqualTo("Active");
        assertThat(lastModified.getText()).isEqualTo("2025-11-05T12:00:00Z");
    }
}