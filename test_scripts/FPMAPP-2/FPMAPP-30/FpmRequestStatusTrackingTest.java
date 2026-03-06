/**
 * Test Case ID: TEST_CASE
 * Generated from Jira Ticket: FPMAPP-30
 * Epic: FPMAPP-2
 * Generated on: 2026-03-06 12:44:13
 * 
 * This is an auto-generated Selenium test script.
 * Modify with caution as changes may be overwritten.
 */

package com.webapp.fpmapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FpmRequestStatusTrackingTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");
        login();
    }

    private void login() {
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));

        usernameField.sendKeys("requester@example.com");
        passwordField.sendKeys("password123");
        loginButton.click();
    }

    @Test
    public void testRequestStatusVisibility() {
        driver.findElement(By.linkText("My Requests")).click();
        WebElement request = driver.findElement(By.xpath("//div[@class='request-item'][1]"));
        request.click();

        WebElement statusElement = driver.findElement(By.id("requestStatus"));
        String currentStatus = statusElement.getText();

        assertTrue(currentStatus != null && !currentStatus.isEmpty(), "Status should be visible");
        assertEquals("Pending", currentStatus, "Status should reflect the correct state");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}