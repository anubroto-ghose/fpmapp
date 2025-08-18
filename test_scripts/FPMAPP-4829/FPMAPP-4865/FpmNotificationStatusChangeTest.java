/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-4865
 * Epic: FPMAPP-4829
 * Generated on: 2025-08-18 14:16:52
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
public class FpmNotificationStatusChangeTest {

    @MockBean
    private RestTemplate restTemplate;

    @InjectMocks
    private FpmCommonController fpmCommonController;

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        // Set up WebDriver (ChromeDriver in this case)
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
    }

    @Test
    public void testNotificationOnStatusChange() throws InterruptedException {
        // Mocking the service response
        when(restTemplate.getForObject("/api/notifications", String.class)).thenReturn("Notification sent");

        // Step 1: Log in as admin
        driver.get("http://localhost:8080/admin/login");
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("admin");
        passwordField.sendKeys("admin123");
        loginButton.click();

        // Step 2: Change the status of the request
        driver.get("http://localhost:8080/admin/requests");
        WebElement changeStatusButton = driver.findElement(By.id("changeStatusButton"));
        changeStatusButton.click();

        // Step 3: Check the notification
        Thread.sleep(2000); // Wait for notification to be sent
        driver.get("http://localhost:8080/user/notifications");
        String notificationMessage = driver.findElement(By.id("notificationMessage")).getText();

        // Assert that the notification was received
        assertTrue(notificationMessage.contains("Your request status has changed"), "Notification not received");
    }

    @AfterEach
    public void tearDown() {
        // Close the browser
        if (driver != null) {
            driver.quit();
        }
    }
}